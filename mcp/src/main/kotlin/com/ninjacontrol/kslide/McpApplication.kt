package com.ninjacontrol.kslide

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.mcp.model.Templates
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries

@SpringBootApplication
class McpApplication

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

fun main(args: Array<String>) {
    val options = parseArgs(args)
    val templatePath = options["templatePath"] ?: error("Missing --templatePath argument")
    val templates = loadTemplates(Path.of(templatePath))
    val layouts = loadLayouts(Path.of(templatePath), templates)

    runApplication<McpApplication>(*args)
}

fun parseArgs(args: Array<String>): Map<String, String> =
    args
        .filter { it.startsWith("--") && it.contains("=") }
        .associate {
            val (key, value) = it.removePrefix("--").split("=", limit = 2)
            key to value
        }
