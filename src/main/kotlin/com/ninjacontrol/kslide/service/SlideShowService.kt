package com.ninjacontrol.kslide.service

import com.ninjacontrol.kslide.model.new
import com.ninjacontrol.kslide.repository.SlideShowRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SlideShowService(
    private val slideShowRepository: SlideShowRepository,
) {
    fun createSlideShow(): UUID {
        val slideShow = new()
        slideShowRepository.add(slideShow)
        return slideShow.id
    }

    fun removeSlideShow(id: UUID) {
        slideShowRepository.remove(id)
    }
}
