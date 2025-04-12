package com.ninjacontrol.kslide

import com.ninjacontrol.kslide.command.ListCommands
import com.ninjacontrol.kslide.command.NewCommands
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(NewCommands::class, ListCommands::class)
class KslideApplication

fun main(args: Array<String>) {
    runApplication<KslideApplication>(*args)
}
