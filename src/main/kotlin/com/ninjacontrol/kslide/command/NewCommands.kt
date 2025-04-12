package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

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
    fun newSlide(
        @Option(description = "title", required = false) title: String?,
    ) {
        val slideShowId = slideShowService.getActiveSlideShowId()
        val slideShow = slideShowService.getSlideShowById(slideShowId)
        val newSlide = slideShow.createSlide()
        slideShowService.setActiveSlide(newSlide.slideNumber)
        println("Created new slide (#${newSlide.slideNumber}) in slideshow with id $slideShowId")
    }

    @Command(command = ["textbox"], group = "New", description = "Create a new text box in a slide")
    fun newTextBox(
        @Option(description = "x coordinate") x: Int,
        @Option(description = "y coordinate") y: Int,
        @Option(description = "width") width: Int,
        @Option(description = "height") height: Int,
    ) {
        val slideShowId = slideShowService.getActiveSlideShowId()
        val currentSlide = slideShowService.getActiveSlide()
        val textBox = currentSlide.createTextBox()

        println(
            "Created new text box (id=${textBox.shapeId},x=$x,y=$y,w=$width,h=$height) in slide (#${currentSlide.slideNumber}) of slideshow with id $slideShowId. ",
        )
    }
}
