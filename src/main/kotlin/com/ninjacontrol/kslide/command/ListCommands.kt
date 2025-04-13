package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command

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

    @Command(command = ["textboxes"], group = "List", description = "List all textboxes in the current slide")
    fun listTextBoxes() {
    }
}
