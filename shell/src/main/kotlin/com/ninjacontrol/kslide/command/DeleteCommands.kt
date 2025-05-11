package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

@Command(command = ["delete"], group = "Delete", description = "Delete ...")
class DeleteCommands(
    private val slideShowService: SlideShowService,
) {
    @Command(command = ["slide"], group = "Delete", description = "Delete the slide")
    fun deleteSlide(
        @Option(description = "slide number", required = true) slideNumber: Int,
    ) {
        slideShowService.deleteSlide(slideNumber)
        println("Deleted slide #$slideNumber")
    }

    @Command(command = ["current text-box"], group = "Delete", description = "Delete the current text box")
    fun deleteCurrentTextBox() {
        slideShowService.deleteActiveTextBox()
        println("Deleted the current text box")
    }

    @Command(command = ["text-box"], group = "Delete", description = "Delete the current text box")
    fun deleteCurrentTextBox(
        @Option(description = "text box id", required = true) textBoxId: Int,
    ) {
        slideShowService.deleteTextBox(textBoxId)
        println("Deleted text box #$textBoxId")
    }

    @Command(command = ["current paragraph"], group = "Delete", description = "Delete the current paragraph")
    fun deleteCurrentParagraph() {
        slideShowService.deleteActiveParagraph()
        println("Deleted the current paragraph")
    }

    @Command(command = ["property"], group = "Delete", description = "Delete a property")
    fun deleteProperty(
        @Option(description = "property name", required = true) propertyName: String,
    ) {
        slideShowService.unsetProperty(propertyName)
        println("Deleted property '$propertyName'")
    }
}
