package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.output.Output
import com.ninjacontrol.kslide.service.SlideShowService
import org.apache.poi.xslf.usermodel.SlideLayout
import org.apache.poi.xslf.usermodel.XSLFSlideLayout
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

@Command(command = ["list"], group = "List")
class ListCommands(
    private val slideShowService: SlideShowService,
    private val output: Output,
) {
    @Command(command = ["slides"], group = "List", description = "List all slides in the current slideshow")
    fun listSlides() {
        val slides = slideShowService.getAllSlides()
        if (slides.isEmpty()) {
            output.out("No slides found in the current slideshow.")
        } else {
            val activeSlide = slideShowService.getActiveSlideNumber()
            output.out("Slides in the current slideshow:")
            slides.forEach { slide ->
                val isActive = if (slide.slideNumber == activeSlide) "(active)" else ""
                output.out("#${slide.slideNumber} - ${slide.title ?: "<untitled>"} $isActive")
            }
        }
    }

    @Command(command = ["slide-details"], group = "List", description = "List details of the current slide")
    fun listSlideDetails() {
        val slide = slideShowService.getActiveSlide()
        if (slide == null) {
            output.out("No active slide found.")
            return
        }
        output.out("Details of the current slide:")
        output.out("Title: ${slide.title ?: "<untitled>"}")
        output.out("Slide number: ${slide.slideNumber}")
        output.out("Placeholders:")
        slide.placeholders.forEach { placeholder ->
            if (placeholder.isPlaceholder) {
                output.out(
                    "-  id: ${placeholder.shapeId}, type: ${placeholder.placeholder}, text: \"${placeholder.text}\", anchor: (x=${placeholder.anchor.x}, y=${placeholder.anchor.y}, w=${placeholder.anchor.width}, h=${placeholder.anchor.height})",
                )
            }
        }
    }

    @Command(command = ["textboxes"], group = "List", description = "List all textboxes in the current slide")
    fun listTextBoxes() {
    }

    @Command(command = ["all-layouts"], group = "List", description = "List all valid layouts")
    fun listLayouts() {
        val layouts = SlideLayout.values()
        if (layouts.isEmpty()) {
            output.out("No layouts found.")
        } else {
            output.out("All valid layouts:")
            layouts.forEach { layout ->
                output.out("- ${layout.name}")
            }
        }
    }

    @Command(command = ["layouts"], group = "List", description = "List available layouts (needs a slideshow to be loaded)")
    fun listCurrentLayouts() {
        val layouts = slideShowService.getAvailableLayouts()
        for ((i, item) in layouts.withIndex()) {
            val layout = item.second
            val masterIndex = item.first
            output.out("$i - ${layout.name} (type=${layout.type}, masterIndex=$masterIndex)")
        }
    }

    @Command(command = ["layout-details"], group = "List", description = "List details of a specific layout")
    fun listLayoutDetails(
        @Option(description = "layout index, see `list layouts`", required = true) layoutIndex: Int,
    ) {
        val layouts = slideShowService.getAvailableLayouts()
        if (layoutIndex < 0 || layoutIndex >= layouts.size) {
            output.out("Invalid layout index. Please choose a valid index.")
            return
        }
        val selectedLayout = layouts[layoutIndex].second
        printLayoutDetails(selectedLayout, layoutIndex, long = true)
    }

    @Command(command = ["all-layout-details"], group = "List", description = "List details of all layouts")
    fun listAllLayoutDetails() {
        val layouts = slideShowService.getAvailableLayouts()
        if (layouts.isEmpty()) {
            output.out("No layouts found.")
            return
        }
        output.out("Layout details:")
        output.out("-----------------------------")
        layouts.forEachIndexed { index, item ->
            val layout = item.second
            printLayoutDetails(layout, index, long = false)
            output.out("-----------------------------")
        }
    }

    private fun printLayoutDetails(
        layout: XSLFSlideLayout,
        index: Int,
        long: Boolean = false,
    ) {
        output.out("Layout name: ${layout.name} (index=$index)")
        if (long) {
            output.out("Layout type: ${layout.type}")
        }

        output.out("Placeholders:")
        layout.placeholders.forEach { placeholder ->
            if (long) {
                output.out(
                    "- id: ${placeholder.shapeId}, type: ${placeholder.placeholder}, text: \"${placeholder.text}\", anchor: (x=${placeholder.anchor.x}, y=${placeholder.anchor.y}, w=${placeholder.anchor.width}, h=${placeholder.anchor.height})",
                )
            } else {
                output.out(
                    "- id: ${placeholder.shapeId}, type: ${placeholder.placeholder}",
                )
            }
        }
    }

    @Command(command = ["properties"], group = "List", description = "List all properties")
    fun listProperties() {
        val properties = slideShowService.getProperties()
        if (properties.isEmpty()) {
            output.out("No properties found.")
        } else {
            output.out("Properties:")
            properties.forEach { (key, value) ->
                output.out("- $key: $value")
            }
        }
    }

    @Command(command = ["property"], group = "List", description = "List a property")
    fun listProperty(
        @Option(description = "property name", required = true) propertyName: String,
    ) {
        val property = slideShowService.getProperty(propertyName)
        if (property == null) {
            output.out("Property '$propertyName' not found.")
        } else {
            output.out("Property '$propertyName': $property")
        }
    }
}
