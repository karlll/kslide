package com.ninjacontrol.kslide.config

import com.ninjacontrol.kslide.repository.SlideShowRepository
import com.ninjacontrol.kslide.repository.SlideShowRepositoryMap
import com.ninjacontrol.kslide.service.SlideShowService
import com.ninjacontrol.kslide.service.SlideShowServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SlideShowConfig {
    
    @Bean
    fun slideShowRepository(): SlideShowRepository {
        return SlideShowRepositoryMap()
    }
    
    @Bean
    fun slideShowService(slideShowRepository: SlideShowRepository): SlideShowService {
        return SlideShowServiceImpl(slideShowRepository)
    }
}