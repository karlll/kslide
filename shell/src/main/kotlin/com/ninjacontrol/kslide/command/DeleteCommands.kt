package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.output.Output
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

@Command(command = ["delete"], group = "Delete", description = "Delete ...")
class DeleteCommands(
    private val slideShowService: SlideShowService,
    private val output: Output,
) {
    @Command(command = ["slide"], group = "Delete", description = "Delete the slide")
    fun deleteSlide(
        @Option(description = "slide number", required = true) slideNumber: Int,
    ) {
        slideShowService.deleteSlide(slideNumber)
        output.out("Deleted slide #$slideNumber")
    }

    @Command(command = ["current text-box"], group = "Delete", description = "Delete the current text box")
    fun deleteCurrentTextBox() {
        slideShowService.deleteActiveTextBox()
        output.out("Deleted the current text box")
    }

    @Command(command = ["text-box"], group = "Delete", description = "Delete the current text box")
    fun deleteCurrentTextBox(
        @Option(description = "text box id", required = true) textBoxId: Int,
    ) {
        slideShowService.deleteTextBox(textBoxId)
        output.out("Deleted text box #$textBoxId")
    }

    @Command(command = ["current paragraph"], group = "Delete", description = "Delete the current paragraph")
    fun deleteCurrentParagraph() {
        slideShowService.deleteActiveParagraph()
        output.out("Deleted the current paragraph")
    }

    @Command(command = ["property"], group = "Delete", description = "Delete a property")
    fun deleteProperty(
        @Option(description = "property name", required = true) propertyName: String,
    ) {
        slideShowService.unsetProperty(propertyName)
        output.out("Deleted property '$propertyName'")
    }
}
