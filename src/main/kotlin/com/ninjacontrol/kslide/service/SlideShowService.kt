package com.ninjacontrol.kslide.service

import com.ninjacontrol.kslide.model.new
import com.ninjacontrol.kslide.model.newSlide
import com.ninjacontrol.kslide.repository.SlideShowRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SlideShowService(
    private val slideShowRepository: SlideShowRepository,
) {
    final var currentSlideShowId: UUID? = null
        private set

    fun createSlideShow(): UUID {
        val slideShow = new()
        slideShowRepository.add(slideShow)
        currentSlideShowId = slideShow.id
        return slideShow.id
    }

    fun createSlide() {
        val id = currentSlideShowId ?: throw IllegalStateException("No current slideshow")
        val slideShow =
            slideShowRepository.get(id)
                ?: throw IllegalArgumentException("Slide show with id $id not found")
        newSlide(slideShow)
        slideShowRepository.add(slideShow)
    }

    fun removeSlideShow(id: UUID) {
        slideShowRepository.remove(id)
        if (currentSlideShowId == id) {
            currentSlideShowId = null
        }
    }

    fun loadSlideShow(id: UUID): UUID {
        val slideShow =
            slideShowRepository.get(id)
                ?: throw IllegalArgumentException("Slide show with id $id not found")
        currentSlideShowId = id
        return slideShow.id
    }
}
