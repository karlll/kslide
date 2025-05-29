package com.ninjacontrol.kslide.config

import com.ninjacontrol.kslide.repository.SlideShowRepository
import com.ninjacontrol.kslide.repository.SlideShowRepositoryMap
import com.ninjacontrol.kslide.service.SlideShowService
import com.ninjacontrol.kslide.service.SlideShowServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlideshowConfig {
    @Bean
    fun slideShowRepository(): SlideShowRepository = SlideShowRepositoryMap()

    @Bean
    fun slideShowService(slideShowRepository: SlideShowRepository): SlideShowService = SlideShowServiceImpl(slideShowRepository)
}
