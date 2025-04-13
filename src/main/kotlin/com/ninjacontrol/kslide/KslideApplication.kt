package com.ninjacontrol.kslide

import com.ninjacontrol.kslide.command.ListCommands
import com.ninjacontrol.kslide.command.NewCommands
import com.ninjacontrol.kslide.command.SetCommands
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(NewCommands::class, ListCommands::class, SetCommands::class)
class KslideApplication

fun main(args: Array<String>) {
    runApplication<KslideApplication>(*args)
}
