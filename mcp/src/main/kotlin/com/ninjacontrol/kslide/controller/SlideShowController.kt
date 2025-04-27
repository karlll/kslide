package com.ninjacontrol.kslide.controller

import com.ninjacontrol.kslide.service.SlideShowService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/slideshows")
class SlideShowController(
    private val slideShowService: SlideShowService,
) {

    @PostMapping
    fun createSlideShow(): ResponseEntity<Map<String, UUID>> {
        val id = slideShowService.createSlideShow()
        return ResponseEntity.ok(mapOf("id" to id))
    }

    @PostMapping("/template")
    fun createSlideShowFromTemplate(@RequestParam template: String): ResponseEntity<Map<String, UUID>> {
        val id = slideShowService.createSlideShow(template)
        return ResponseEntity.ok(mapOf("id" to id))
    }

    @GetMapping("/{id}")
    fun getSlideShow(@PathVariable id: UUID): ResponseEntity<Any> {
        try {
            slideShowService.setActiveSlideShow(id)
            return ResponseEntity.ok(mapOf(
                "id" to id,
                "slideCount" to slideShowService.getAllSlides().size
            ))
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/slides")
    fun createSlide(
        @PathVariable id: UUID,
        @RequestParam(required = false) title: String?
    ): ResponseEntity<Map<String, Any>> {
        try {
            slideShowService.setActiveSlideShow(id)
            val slideNumber = slideShowService.createSlide(title)
            val titleValue: Any = if (title != null) title else "Untitled Slide"
            return ResponseEntity.ok(mapOf(
                "slideNumber" to slideNumber,
                "title" to titleValue
            ))
        } catch (e: Exception) {
            val errorMessage: Any = e.message ?: "Unknown error"
            return ResponseEntity.badRequest().body(mapOf("error" to errorMessage))
        }
    }

    @GetMapping("/{id}/slides")
    fun getAllSlides(@PathVariable id: UUID): ResponseEntity<List<Map<String, Any>>> {
        try {
            slideShowService.setActiveSlideShow(id)
            val slides = slideShowService.getAllSlides()
            return ResponseEntity.ok(
                slides.mapIndexed { index, _ ->
                    mapOf(
                        "slideNumber" to index,
                        "title" to "Slide ${index + 1}"
                    )
                }
            )
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(emptyList())
        }
    }

    @DeleteMapping("/{id}")
    fun deleteSlideShow(@PathVariable id: UUID): ResponseEntity<Any> {
        try {
            slideShowService.removeSlideShow(id)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            val errorMessage: Any = e.message ?: "Unknown error"
            return ResponseEntity.badRequest().body(mapOf("error" to errorMessage))
        }
    }

    @DeleteMapping("/{id}/slides/{slideNumber}")
    fun deleteSlide(
        @PathVariable id: UUID,
        @PathVariable slideNumber: Int
    ): ResponseEntity<Any> {
        try {
            slideShowService.setActiveSlideShow(id)
            slideShowService.deleteSlide(slideNumber)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            val errorMessage: Any = e.message ?: "Unknown error"
            return ResponseEntity.badRequest().body(mapOf("error" to errorMessage))
        }
    }
}
