package com.ninjacontrol.kslide

import com.ninjacontrol.kslide.command.Commands
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(Commands::class)
class KslideApplication

fun main(args: Array<String>) {
    runApplication<KslideApplication>(*args)
}
