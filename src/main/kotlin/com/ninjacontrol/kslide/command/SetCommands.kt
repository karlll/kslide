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
}
