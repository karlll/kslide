package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.util.UUID

@Command(command = ["new"], group = "Slideshow", description = "Create a new ...")
class Commands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["slideshow"], group = "New", description = "Create a new slideshow")
    fun newSlideshow() {
        val id = slideShowService.createSlideShow()
        println("Created new slideshow with id $id")
    }

    @Command(command = ["slide"], group = "New", description = "Create a new slide in a slideshow")
    fun newSlideInSlideshow(
        @Option(required = false, description = "The UUID (string) of a slideshow. If omitted, the current loaded slideshow is assumed") id:
            String?,
    ) {
        if (id == null) {
            if (slideShowService.currentSlideShowId == null) {
                println("No current slideshow")
                return
            } else {
                slideShowService.createSlide()
            }
        } else {
            try {
                val slideShowId = UUID.fromString(id)
                slideShowService.loadSlideShow(slideShowId)
                slideShowService.createSlide()
            } catch (e: IllegalArgumentException) {
                println("Invalid UUID format: $id")
                return
            }
        }
    }
}
