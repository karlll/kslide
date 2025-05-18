package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.output.Output
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.nio.file.Paths

@Command(command = ["load"], group = "Load", description = "Load from file ...")
class LoadCommands(
    private val slideShowService: SlideShowService,
    private val output: Output,
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
                output.out("Please set the active directory first.")
                return
            }

        // write the current slideshow to a file
        val file = java.io.File(filePath)
        if (file.exists()) {
            val id = slideShowService.createSlideShow(filePath)
            output.out("Loaded the slideshow from $filePath (id=$id)")
        } else {
            output.out("File $filePath does not exist.")
        }
    }
}
