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
        val slideNumber = slideShowService.createSlide(title)
        println(
            "Created new slide (id=$slideNumber) in slideshow with id ${slideShowService.getActiveSlideShowId()}. ",
        )
    }

    @Command(command = ["textbox"], group = "New", description = "Create a new text box in a slide")
    fun newTextBox(
        @Option(description = "x coordinate", required = true) x: Int,
        @Option(description = "y coordinate", required = true) y: Int,
        @Option(description = "width", required = true) width: Int,
        @Option(description = "height", required = true) height: Int,
    ) {
        val slideShowId = slideShowService.getActiveSlideShowId()
        val currentSlide = slideShowService.getActiveSlide()
        val textBox = currentSlide.createTextBox()
        slideShowService.createTextBox(x, y, width, height)

        println(
            "Created new text box (id=${textBox.shapeId},x=$x,y=$y,w=$width,h=$height) in slide (#${currentSlide.slideNumber}) of slideshow with id $slideShowId. ",
        )
    }
}
