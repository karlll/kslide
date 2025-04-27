@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide

import com.ninjacontrol.kslide.command.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(NewCommands::class, ListCommands::class, SetCommands::class, WriteCommands::class, DeleteCommands::class)
class KSlideShellApplication

fun main(args: Array<String>) {
    runApplication<KSlideShellApplication>(*args)
}
