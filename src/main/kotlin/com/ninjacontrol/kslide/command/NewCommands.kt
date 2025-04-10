package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.util.UUID

@Command(command = ["new"], group = "New", description = "Create a new ...")
class NewCommands(
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
        var num = -1
        if (id == null) {
            if (slideShowService.currentSlideShowId == null) {
                println("No current slideshow")
                return
            } else {
                num = slideShowService.createSlide()
            }
        } else {
            try {
                val slideShowId = UUID.fromString(id)
                slideShowService.loadSlideShow(slideShowId)
                num = slideShowService.createSlide()
            } catch (e: IllegalArgumentException) {
                println("Invalid UUID format: $id")
                return
            }
        }
        println("Created new slide (#$num) in slideshow (${slideShowService.currentSlideShowId})")
    }
}
