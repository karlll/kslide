package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.nio.file.Paths

@Command(command = ["load"], group = "Load", description = "Load from file ...")
class LoadCommands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["file"], group = "Load", description = "Load a slideshow from file")
    fun loadTemplate(
        @Option(description = "filename", required = true) filename: String,
    ) {
        val activeDirectory = slideShowService.getActiveDirectory()

        val filePath =
            if (!activeDirectory.isNullOrEmpty()) {
                Paths.get(activeDirectory, filename).toString()
            } else {
                println("Please set the active directory first.")
                return
            }

        // write the current slideshow to a file
        val file = java.io.File(filePath)
        if (file.exists()) {
            val id = slideShowService.createSlideShow(filePath)
            println("Loaded the slideshow from $filePath (id=$id)")
        } else {
            println("File $filePath does not exist.")
        }
    }
}
