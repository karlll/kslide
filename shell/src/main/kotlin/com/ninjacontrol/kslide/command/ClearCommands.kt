package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.output.Output
import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option

@Command(command = ["clear"], group = "Clear", description = "Clear ...")
class ClearCommands(
    private val slideShowService: SlideShowService,
    private val output: Output,
) {
    @Command(command = ["current text-box"], group = "Clear", description = "Clear the current text box")
    fun clearActiveTextBox() {
        slideShowService.clearActiveTextBox()
        output.out("Cleared the current text box")
    }

    @Command(command = ["current text-box"], group = "Clear", description = "Clear the current text box")
    fun clearTextBox(
        @Option(description = "text box id", required = true) id: Int,
    ) {
        slideShowService.clearTextBox(id)
        output.out("Cleared the text box with id $id")
    }
}
