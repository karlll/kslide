package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command

@Command(command = ["set"], group = "Set")
class SetCommands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["slide"], group = "Set", description = "Set the active slide")
    fun setActiveSlideNumber(slideNumber: Int) {
        slideShowService.setActiveSlide(slideNumber)
        println("Set active slide to #$slideNumber")
    }

    @Command(command = ["dir"], group = "Set", description = "Set the active directory for reading/writing files")
    fun setActiveDirectory(directory: String) {
        slideShowService.setActiveDirectory(directory)
        println("Set active directory to $directory")
    }

    @Command(command = ["layout"], group = "Set", description = "Set the active layout")
    fun setActiveLayout(layoutIndex: Int) {
        val layouts = slideShowService.getAvailableLayouts()
        if (layoutIndex < 0 || layoutIndex >= layouts.size) {
            println("Invalid layout index. Please choose a valid index.")
            return
        }
        val selectedLayout = layouts[layoutIndex].second
        slideShowService.setActiveLayout(selectedLayout)
        println("Set active layout to '${selectedLayout.name} (type=${selectedLayout.type})'")
    }

    @Command(command = ["text-box"], group = "Set", description = "Set the active text box")
    fun setActiveTextBox(textBoxId: Int) {
        slideShowService.setActiveTextBox(textBoxId)
        println("Set active text box to #$textBoxId")
    }
}
