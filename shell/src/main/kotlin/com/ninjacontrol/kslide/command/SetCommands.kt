package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.output.Output
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

@Command(command = ["set"], group = "Set")
class SetCommands(
    private val slideShowService: SlideShowService,
    private val output: Output,
) {
    @Command(command = ["slide"], group = "Set", description = "Set the active slide")
    fun setActiveSlideNumber(slideNumber: Int) {
        slideShowService.setActiveSlide(slideNumber)
        output.out("Set active slide to #$slideNumber")
    }

    @Command(command = ["dir"], group = "Set", description = "Set the active directory for reading/writing files")
    fun setActiveDirectory(directory: String) {
        slideShowService.setActiveDirectory(directory)
        output.out("Set active directory to $directory")
    }

    @Command(command = ["layout"], group = "Set", description = "Set the active layout")
    fun setActiveLayout(layoutIndex: Int) {
        val layouts = slideShowService.getAvailableLayouts()
        if (layoutIndex < 0 || layoutIndex >= layouts.size) {
            output.out("Invalid layout index. Please choose a valid index.")
            return
        }
        val selectedLayout = layouts[layoutIndex].second
        slideShowService.setActiveLayout(selectedLayout)
        output.out("Set active layout to '${selectedLayout.name} (type=${selectedLayout.type})'")
    }

    @Command(command = ["text-box"], group = "Set", description = "Set the active text box")
    fun setActiveTextBox(textBoxId: Int) {
        slideShowService.setActiveTextBox(textBoxId)
        output.out("Set active text box to #$textBoxId")
    }

    @Command(command = ["text-run"], group = "Set", description = "Set the text of the active text paragraph")
    fun setActiveTextParagraph(text: String) {
        slideShowService.setTextRunInActiveParagraph(text)
        output.out("Set text of the active text paragraph to '$text'")
    }

    @Command(command = ["property"], group = "Set", description = "Set a property")
    fun setProperty(
        @Option(description = "property name", required = true) propertyName: String,
        @Option(description = "property value", required = true) propertyValue: String,
    ) {
        slideShowService.setProperty(propertyName, propertyValue)
        output.out("Set property '$propertyName' to '$propertyValue'")
    }
}
