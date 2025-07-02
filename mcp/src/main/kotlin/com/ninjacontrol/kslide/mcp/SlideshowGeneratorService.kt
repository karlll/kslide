package com.ninjacontrol.kslide.mcp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.mcp.model.Templates
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.UUID

@Service
@Suppress("ktlint:standard:max-line-length")
class SlideshowGeneratorService(
    private val slideShowService: SlideShowService,
    private val templates: Templates,
    private val layouts: Map<String, Layouts>,
    private val templatePath: Path,
    private val outputPath: Path,
) {
    @Tool(description = "List available slideshow templates.")
    fun listTemplates(): String {
        if (templates.isEmpty()) throw IllegalStateException("No templates available. Please check your configuration.")
        return "Available templates:\n ${templates.joinToString("\n") { "- \"${it.name}\", ${it.description}" }}"
    }

    @Tool(
        description =
            "List available layouts for a given template. The layouts contain placeholders for slide content. Each layout has a name, description, and index. The index is used to reference the layout when creating a slide. The placeholder ids are used to identify which part of the slide layout the text (or image) content should be added.",
    )
    fun listLayouts(
        @ToolParam(description = "Slideshow template") template: String,
    ): String {
        validateTemplate(template)

        val availableLayouts = layouts[template] ?: emptyList()
        return if (availableLayouts.isEmpty()) {
            "No layouts found for template '$template'."
        } else {
            "Available layouts for '$template':\n ${
                availableLayouts.joinToString(
                    "\n",
                ) { "- \"${it.name}\" (index=${it.index}), ${it.description}" }
            }"
        }
    }

    private fun validateTemplate(template: String) {
        if (template !in templates.map { it.name }) {
            throw IllegalArgumentException("Error: template '$template' not found.")
        }
    }

    private fun validateTemplateFile(templateFile: Path) {
        if (!templateFile.toFile().exists()) {
            throw IllegalArgumentException("Error: template file '${templateFile.fileName}' does not exist.")
        }
        if (!templateFile.toFile().isFile) {
            throw IllegalArgumentException("Error: '${templateFile.fileName}' is not a file.")
        }
        if (!templateFile.toFile().canRead()) {
            throw IllegalArgumentException("Error: template file '${templateFile.fileName}' is not readable.")
        }
    }

    private fun setDirectory(
        path: String,
        shouldWrite: Boolean = false,
    ) {
        val directoryPath = Path.of(path)
        if (!directoryPath.toFile().exists()) {
            throw IllegalArgumentException("Error: directory '$path' does not exist.")
        }
        if (!directoryPath.toFile().isDirectory) {
            throw IllegalArgumentException("Error: '$path' is not a directory.")
        }
        if (shouldWrite) {
            if (!directoryPath.toFile().canWrite()) {
                throw IllegalArgumentException("Error: directory '$path' is not writable.")
            }
        }

        slideShowService.setActiveDirectory(path)
    }

    @Tool(description = "Create a new slideshow using a template. Returns a unique identifier for the slideshow.")
    fun createSlideshow(
        @ToolParam(description = "Slideshow template") template: String,
    ): String {
        validateTemplate(template)
        val templateFile = templates.find { it.name == template }!!.filename
        val templateFileFullPath = templatePath.resolve(templateFile)
        validateTemplateFile(templateFileFullPath)
        val uuid = slideShowService.createSlideShow(templateFileFullPath.toString())

        return "Slideshow created. Identifier: $uuid\n" +
            "Template: $template\n" +
            "You can now add slides to this slideshow using the identifier."
    }

    @Tool(
        description = "Create a new slide in the current slideshow using a specific layout. The layout contains placeholders. The slide content is provided as a map of placeholder ids and their corresponding text content.",
    )
    fun createSlide(
        @ToolParam(description = "Slideshow identifier") slideshowId: String,
        @ToolParam(description = "Layout index") layoutIndex: Int,
        @ToolParam(description = "Slide content. A map of placeholder ids and their corresponding text content.") content: Map<Int, String>,
    ): String {
        val objectMapper = jacksonObjectMapper()
        val contentJson = objectMapper.writeValueAsString(content)
        slideShowService.setActiveSlideShow(UUID.fromString(slideshowId))
        val availableLayouts = slideShowService.getAvailableLayouts()
        if (layoutIndex < 0 || layoutIndex >= availableLayouts.size) {
            throw IllegalArgumentException("Invalid layout index. Please choose a valid index.")
        }
        val selectedLayout = availableLayouts[layoutIndex].second
        slideShowService.setActiveLayout(selectedLayout)

        val slideNumber = slideShowService.createSlide(null)
        addContentToSlide(content)

        return "Created slide (#$slideNumber) in slideshow '$slideshowId' with layout index $layoutIndex and content: $contentJson\n"
    }

    @Tool(
        description = "Create a new slide with markdown-formatted content. Content can include unordered list (must be prefixed by '-','*' or '+'), **bold**, *italic*, and `inline code`.",
    )
    fun createSlideWithMarkdown(
        @ToolParam(description = "Slideshow identifier") slideshowId: String,
        @ToolParam(description = "Layout index") layoutIndex: Int,
        @ToolParam(description = "Slide content with markdown formatting. Map of placeholder IDs to markdown strings.") content:
            Map<Int, String>,
    ): String {
        val objectMapper = jacksonObjectMapper()
        val contentJson = objectMapper.writeValueAsString(content)
        slideShowService.setActiveSlideShow(UUID.fromString(slideshowId))
        val availableLayouts = slideShowService.getAvailableLayouts()
        if (layoutIndex < 0 || layoutIndex >= availableLayouts.size) {
            throw IllegalArgumentException("Invalid layout index. Please choose a valid index.")
        }
        val selectedLayout = availableLayouts[layoutIndex].second
        slideShowService.setActiveLayout(selectedLayout)

        val slideNumber = slideShowService.createSlide(null)
        addMarkdownContentToSlide(content)

        return "Created slide (#$slideNumber) in slideshow '$slideshowId' with layout index $layoutIndex and markdown content: $contentJson\n"
    }

    @Tool(
        description = "Save the current slideshow to a named file.",
    )
    open fun saveSlideshow(
        @ToolParam(description = "Slideshow identifier") slideshowId: String,
        @ToolParam(description = "File name to save the slideshow (without file ending)") fileName: String,
    ): String {
        if (fileName.isBlank()) {
            throw IllegalArgumentException("Error: file name cannot be empty.")
        }
        val fullFilename =
            if (fileName.endsWith(".pptx")) {
                fileName
            } else {
                "$fileName.pptx"
            }
        val filePath = outputPath.resolve(fullFilename)
        setDirectory(outputPath.toString(), true)
        slideShowService.setActiveSlideShow(UUID.fromString(slideshowId))
        slideShowService.exportActiveSlideShow(filePath.toString())
        return "Slideshow '$slideshowId' saved as '$fileName'."
    }

    private fun addContentToSlide(content: Map<Int, String>) {
        content.forEach { (placeholderId, text) ->
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.createParagraph(text)
        }
    }

    private fun addMarkdownContentToSlide(content: Map<Int, String>) {
        content.forEach { (placeholderId, markdownText) ->
            slideShowService.addMarkdownContent(placeholderId, markdownText)
        }
    }
}
