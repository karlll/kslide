package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.nio.file.Paths

@Command(command = ["write"], group = "Write", description = "Write to file ...")
class WriteCommands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["file"], group = "Write", description = "Write the current slideshow to a file")
    fun writeFile(
        @Option(description = "filename", required = true) filename: String,
    ) {
        // append pptx to filename if not present
        val pptx =
            if (filename.endsWith(".pptx")) {
                filename
            } else {
                "$filename.pptx"
            }

        // Get the active directory from the service
        val activeDirectory = slideShowService.getActiveDirectory()

        // Use Paths.get to join the directory and filename
        val filePath =
            if (!activeDirectory.isNullOrEmpty()) {
                Paths.get(activeDirectory, pptx).toString()
            } else {
                println("Please set the active directory first.")
                return
            }

        // write the current slideshow to a file
        val file = java.io.File(filePath)
        slideShowService.exportActiveSlideShow(filePath)
        println("Exported the current slideshow to $filePath")
    }

    @Command(command = ["image"], group = "Write", description = "Render a slide to an image")
    fun writeImage(
        @Option(description = "slide number", required = true) slideNumber: Int,
        @Option(description = "filename", required = true) filename: String,
    ) {
        // append png to filename if not present
        val png =
            if (filename.endsWith(".png")) {
                filename
            } else {
                "$filename.png"
            }

        // Get the active directory from the service
        val activeDirectory = slideShowService.getActiveDirectory()

        // Use Paths.get to join the directory and filename
        val filePath =
            if (!activeDirectory.isNullOrEmpty()) {
                Paths.get(activeDirectory, png).toString()
            } else {
                println("Please set the active directory first.")
                return
            }

        // write the current slideshow to a file
        val file = java.io.File(filePath)
        slideShowService.renderSlideToImage(slideNumber, file)
        println("Exported slide #$slideNumber to $filePath")
    }

    @Command(command = ["images"], group = "Write", description = "Render all slides to images")
    fun writeImages(
        @Option(description = "prefix", required = true) prefix: String,
    ) {
        // Get the active directory from the service
        val activeDirectory = slideShowService.getActiveDirectory()

        // write the current slideshow to a file
        slideShowService.renderSlidesToImages(activeDirectory.toString(), prefix)
        println("Exported all slides to $activeDirectory")
    }
}
