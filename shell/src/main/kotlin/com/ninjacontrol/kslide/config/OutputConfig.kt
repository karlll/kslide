package com.ninjacontrol.kslide.config

import com.ninjacontrol.kslide.output.Output
import com.ninjacontrol.kslide.output.StdoutWriter
import com.ninjacontrol.kslide.output.Writer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OutputConfig {
    @Bean
    fun output(): Output =
        Writer().apply {
            addWriter(StdoutWriter())
        }
}
