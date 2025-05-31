package com.ninjacontrol.kslide.mcp

import com.ninjacontrol.kslide.mcp.model.Layout
import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.service.SlideShowService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SlideshowGeneratorServiceTest {
    private val slideShowService: SlideShowService = mockk()

    private fun layouts(vararg names: String): Layouts = names.mapIndexed { i, n -> Layout(n, "$n description", i) }

    @Test
    fun `listTemplates returns available templates`() {
        val service = SlideshowGeneratorService(slideShowService, layouts("template1", "template2"))
        val result = service.listTemplates()
        assertEquals("Available templates: template1, template2", result)
    }

    @Test
    fun `listLayouts returns error for unknown template`() {
        val service = SlideshowGeneratorService(slideShowService, layouts("template1"))
        val result = service.listLayouts("unknown")
        assertEquals("Error: template 'unknown' not found. Available templates: template1", result)
    }

    @Test
    fun `listLayouts returns available layouts for template`() {
        val service = SlideshowGeneratorService(slideShowService, layouts("template1"))
        val result = service.listLayouts("template1")
        assertEquals("Available layouts for 'template1': template1", result)
    }

    @Test
    fun `createSlideshow returns error for unknown template`() {
        val service = SlideshowGeneratorService(slideShowService, layouts("template1"))
        val result = service.createSlideshow("unknown")
        assertEquals("Error: template 'unknown' not found. Available templates: template1", result)
    }

    @Test
    fun `createSlideshow returns success for known template`() {
        val service = SlideshowGeneratorService(slideShowService, layouts("template1"))
        val result = service.createSlideshow("template1")
        assertEquals("Slideshow created", result)
    }
}
