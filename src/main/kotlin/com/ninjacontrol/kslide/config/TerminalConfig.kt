package com.ninjacontrol.kslide.config

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.shell.jline.PromptProvider

@Configuration
class TerminalConfig {
    @Bean
    fun myPromptProvider(): PromptProvider =
        PromptProvider {
            AttributedString("> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW))
        }
}
