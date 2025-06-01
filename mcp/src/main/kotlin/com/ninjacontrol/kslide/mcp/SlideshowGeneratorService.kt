package com.ninjacontrol.kslide.mcp

import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.mcp.model.Templates
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class SlideshowGeneratorService(
    private val slideShowService: SlideShowService,
    private val templates: Templates,
    private val layouts: Map<String, Layouts>,
) {
    @Tool(description = "List available slideshow templates.")
    fun listTemplates(): String {
        if (templates.isEmpty()) throw IllegalStateException("No templates available. Please check your configuration.")
        return "Available templates: ${templates.joinToString(", ") { it.name }}"
    }

    @Tool(description = "List available layouts for a given template.")
    fun listLayouts(
        @ToolParam(description = "Slideshow template") template: String,
    ): String {
        validateTemplate(template)

        val availableLayouts = layouts[template] ?: emptyList()
        return if (availableLayouts.isEmpty()) {
            "No layouts found for template '$template'."
        } else {
            "Available layouts for '$template': ${availableLayouts.joinToString(", ") { it.name }}"
        }
    }

    private fun validateTemplate(template: String) {
        if (template !in templates.map { it.name }) {
            throw IllegalArgumentException("Error: template '$template' not found.")
        }
    }

    @Tool(description = "Create a new slideshow using a template. Returns a unique identifier for the slideshow.")
    fun createSlideshow(
        @ToolParam(description = "Slideshow template") template: String,
    ): String {
        validateTemplate(template)

        return "Slideshow created"
    }
}
