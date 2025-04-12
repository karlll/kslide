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
            println("Slides in the current slideshow:")
            slides.forEach { slide ->
                println("${slide.slideNumber}: ${slide.title ?: "<untitled>"}")
            }
        }
    }
}
