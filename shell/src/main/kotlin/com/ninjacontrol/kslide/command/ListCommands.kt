package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.apache.poi.xslf.usermodel.SlideLayout
import org.apache.poi.xslf.usermodel.XSLFSlideLayout
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

@Command(command = ["list"], group = "List")
class ListCommands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["slides"], group = "List", description = "List all slides in the current slideshow")
    fun listSlides() {
        val slides = slideShowService.getAllSlides()
        if (slides.isEmpty()) {
            println("No slides found in the current slideshow.")
        } else {
            val activeSlide = slideShowService.getActiveSlideNumber()
            println("Slides in the current slideshow:")
            slides.forEach { slide ->
                val isActive = if (slide.slideNumber == activeSlide) "(active)" else ""
                println("#${slide.slideNumber} - ${slide.title ?: "<untitled>"} $isActive")
            }
        }
    }

    @Command(command = ["slide-details"], group = "List", description = "List details of the current slide")
    fun listSlideDetails() {
        val slide = slideShowService.getActiveSlide()
        if (slide == null) {
            println("No active slide found.")
            return
        }
        println("Details of the current slide:")
        println("Title: ${slide.title ?: "<untitled>"}")
        println("Slide number: ${slide.slideNumber}")
        println("Placeholders:")
        slide.placeholders.forEach { placeholder ->
            if (placeholder.isPlaceholder) {
                println(
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
            println("No layouts found.")
        } else {
            println("All valid layouts:")
            layouts.forEach { layout ->
                println("- ${layout.name}")
            }
        }
    }

    @Command(command = ["layouts"], group = "List", description = "List available layouts (needs a slideshow to be loaded)")
    fun listCurrentLayouts() {
        val layouts = slideShowService.getAvailableLayouts()
        for ((i, item) in layouts.withIndex()) {
            val layout = item.second
            val masterIndex = item.first
            println("$i - ${layout.name} (type=${layout.type}, masterIndex=$masterIndex)")
        }
    }

    @Command(command = ["layout-details"], group = "List", description = "List details of a specific layout")
    fun listLayoutDetails(
        @Option(description = "layout index, see `list layouts`", required = true) layoutIndex: Int,
    ) {
        val layouts = slideShowService.getAvailableLayouts()
        if (layoutIndex < 0 || layoutIndex >= layouts.size) {
            println("Invalid layout index. Please choose a valid index.")
            return
        }
        val selectedLayout = layouts[layoutIndex].second
        printLayoutDetails(selectedLayout, layoutIndex, long = true)
    }

    @Command(command = ["all-layout-details"], group = "List", description = "List details of all layouts")
    fun listAllLayoutDetails() {
        val layouts = slideShowService.getAvailableLayouts()
        if (layouts.isEmpty()) {
            println("No layouts found.")
            return
        }
        println("Layout details:")
        println("-----------------------------")
        layouts.forEachIndexed { index, item ->
            val layout = item.second
            printLayoutDetails(layout, index, long = false)
            println("-----------------------------")
        }
    }

    private fun printLayoutDetails(
        layout: XSLFSlideLayout,
        index: Int,
        long: Boolean = false,
    ) {
        println("Layout name: ${layout.name} (index=$index)")
        if (long) {
            println("Layout type: ${layout.type}")
        }

        println("Placeholders:")
        layout.placeholders.forEach { placeholder ->
            if (long) {
                println(
                    "- id: ${placeholder.shapeId}, type: ${placeholder.placeholder}, text: \"${placeholder.text}\", anchor: (x=${placeholder.anchor.x}, y=${placeholder.anchor.y}, w=${placeholder.anchor.width}, h=${placeholder.anchor.height})",
                )
            } else {
                println(
                    "- id: ${placeholder.shapeId}, type: ${placeholder.placeholder}",
                )
            }
        }
    }

    @Command(command = ["properties"], group = "List", description = "List all properties")
    fun listProperties() {
        val properties = slideShowService.getProperties()
        if (properties.isEmpty()) {
            println("No properties found.")
        } else {
            println("Properties:")
            properties.forEach { (key, value) ->
                println("- $key: $value")
            }
        }
    }

    @Command(command = ["property"], group = "List", description = "List a property")
    fun listProperty(
        @Option(description = "property name", required = true) propertyName: String,
    ) {
        val property = slideShowService.getProperty(propertyName)
        if (property == null) {
            println("Property '$propertyName' not found.")
        } else {
            println("Property '$propertyName': $property")
        }
    }
}
