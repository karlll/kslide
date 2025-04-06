package com.ninjacontrol.kslide.config

import com.ninjacontrol.kslide.repository.SlideShowRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlideShowRepositoryConfig {
    @Bean
    fun slideShowRepository(): SlideShowRepository =
        com.ninjacontrol.kslide.repository
            .SlideShowRepositoryMap()
}
