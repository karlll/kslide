package com.ninjacontrol.kslide.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.mcp.model.Templates
import com.ninjacontrol.kslide.repository.SlideShowRepository
import com.ninjacontrol.kslide.repository.SlideShowRepositoryMap
import com.ninjacontrol.kslide.service.SlideShowService
import com.ninjacontrol.kslide.service.SlideShowServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries

@Configuration
class SlideshowConfig {
    @Bean
    fun slideShowRepository(): SlideShowRepository = SlideShowRepositoryMap()

    @Bean
    fun slideShowService(slideShowRepository: SlideShowRepository): SlideShowService = SlideShowServiceImpl(slideShowRepository)

    @Bean
    fun templatePath(): Path {
        val templatePath = System.getProperty("templatePath") ?: error("Missing --templatePath system property")
        return Path.of(templatePath)
    }

    @Bean
    fun templates(templatePath: Path): Templates = loadTemplates(templatePath)

    @Bean
    fun layouts(templates: Templates): Map<String, Layouts> {
        val templatePath = System.getProperty("templatePath") ?: error("Missing --templatePath system property")
        return loadLayouts(Path.of(templatePath), templates)
    }

    @Bean
    fun outputPath(): Path {
        val outputPath = System.getProperty("outputPath") ?: error("Missing --outputPath system property")
        return Path.of(outputPath)
    }

    fun loadTemplates(path: Path): Templates {
        val files = path.listDirectoryEntries()
        val templateFile =
            files.find { it.fileName.toString() == "templates.json" }
                ?: throw IllegalArgumentException("No templates.json file found in $path")

        val objectMapper = jacksonObjectMapper()
        return templateFile.inputStream().use {
            objectMapper.readValue(it)
        }
    }

    fun loadLayouts(
        path: Path,
        templates: Templates,
    ): Map<String, Layouts> {
        val objectMapper = jacksonObjectMapper()
        return templates.associate { template ->
            val layoutsFile =
                path
                    .resolve(template.layoutsFilename)
                    .takeIf { it.toFile().exists() }
                    ?: throw IllegalArgumentException("Layouts file ${template.layoutsFilename} not found for template ${template.name}")

            val layouts: Layouts =
                layoutsFile.inputStream().use {
                    objectMapper.readValue(it)
                }
            template.name to layouts
        }
    }
}
