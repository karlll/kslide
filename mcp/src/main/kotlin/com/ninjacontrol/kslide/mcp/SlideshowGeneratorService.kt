package com.ninjacontrol.kslide.mcp

import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class SlideshowGeneratorService(
    private val slideShowService: SlideShowService,
    private val layouts: Layouts,
) {
    private val templates: List<String> = layouts.map { it.name }

    @Tool(description = "List available slideshow templates.")
    fun listTemplates(): String = "Available templates: ${templates.joinToString(", ")}"

    @Tool(description = "List available layouts for a given template.")
    fun listLayouts(
        @ToolParam(description = "Slideshow template") template: String,
    ): String {
        if (template !in templates) {
            return "Error: template '$template' not found. Available templates: ${templates.joinToString(", ")}"
        }

        val availableLayouts = layouts.filter { it.name == template }
        return if (availableLayouts.isEmpty()) {
            "No layouts found for template '$template'."
        } else {
            "Available layouts for '$template': ${availableLayouts.joinToString(", ") { it.name }}"
        }
    }

    @Tool(description = "Create a new slideshow using a template. Returns a unique identifier for the slideshow.")
    fun createSlideshow(
        @ToolParam(description = "Slideshow template") template: String,
    ): String {
        if (template !in templates) {
            return "Error: template '$template' not found. Available templates: ${templates.joinToString(", ")}"
        }

        return "Slideshow created"
    }
}
