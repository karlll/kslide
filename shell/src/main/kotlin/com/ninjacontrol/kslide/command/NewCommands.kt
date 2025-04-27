package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.nio.file.Paths

@Command(command = ["new"], alias = ["n"], group = "New", description = "Create a new ...")
class NewCommands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["slideshow"], group = "New", description = "Create a new slideshow")
    fun newSlideshow(
        @Option(description = "template", required = false) template: String?,
    ) {
        if (template.isNullOrEmpty()) {
            val id = slideShowService.createSlideShow()
            println("Created new slideshow with id $id")
        } else {
            val directory = slideShowService.getActiveDirectory()
            if (directory.isNullOrEmpty()) {
                println("Please set the active directory first when loading a template.")
            } else {
                val templateFile = Paths.get(directory, template).toString()
                slideShowService.createSlideShow(templateFile)
                println("Creating a new slideshow using template $template.")
            }
        }
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
        slideShowService.createTextBox(x, y, width, height)
        val textBox = slideShowService.getActiveTextBox()

        println(
            "Created new text box (id=${textBox.shapeId},x=$x,y=$y,w=$width,h=$height) in slide (#${currentSlide.slideNumber}) of slideshow with id $slideShowId. ",
        )
    }

    @Command(command = ["paragraph"], group = "New", description = "Create a new paragraph in a text box")
    fun newParagraph(
        @Option(description = "text", required = true) text: String,
    ) {
        val slideShowId = slideShowService.getActiveSlideShowId()
        val currentSlide = slideShowService.getActiveSlide()
        slideShowService.createParagraph(text)
        val textBox = slideShowService.getActiveTextBox()

        println(
            "Created new paragraph (id=${textBox.shapeId},text=$text) in text box (#${textBox.shapeId}) of slide (#${currentSlide.slideNumber}) of slideshow with id $slideShowId. ",
        )
    }

    @Command(command = ["bullet"], group = "New", description = "Add a new bullet")
    fun newBullet(
        @Option(description = "level", required = true) level: Int,
        @Option(description = "text", required = true) text: String,
    ) {
        val slideShowId = slideShowService.getActiveSlideShowId()
        val currentSlide = slideShowService.getActiveSlide()
        slideShowService.addBullet(level, text)
        val textBox = slideShowService.getActiveTextBox()

        println(
            "Created new bullet (id=${textBox.shapeId},text=$text) in text box (#${textBox.shapeId}) of slide (#${currentSlide.slideNumber}) of slideshow with id $slideShowId. ",
        )
    }
}