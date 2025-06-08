package com.ninjacontrol.kslide

import com.ninjacontrol.kslide.mcp.SlideshowGeneratorService
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class KSlideMcpApplication {
    @Bean
    fun slideGeneratorTools(slideShowGeneratorService: SlideshowGeneratorService): ToolCallbackProvider =
        MethodToolCallbackProvider.builder().toolObjects(slideShowGeneratorService).build()
}

fun main(args: Array<String>) {
    val options = parseArgs(args)
    val templatePath = options["templatePath"] ?: error("Missing --templatePath argument")
    val ouputPath = options["outputPath"] ?: error("Missing --outputPath argument")
    System.setProperty("templatePath", templatePath)
    System.setProperty("outputPath", ouputPath)
    runApplication<KSlideMcpApplication>(*args)
}

fun parseArgs(args: Array<String>): Map<String, String> =
    args
        .filter { it.startsWith("--") && it.contains("=") }
        .associate {
            val (key, value) = it.removePrefix("--").split("=", limit = 2)
            key to value
        }
