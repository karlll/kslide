@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.mcp

import com.ninjacontrol.kslide.mcp.model.Layout
import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.mcp.model.Template
import com.ninjacontrol.kslide.mcp.model.Templates
import com.ninjacontrol.kslide.service.SlideShowService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideshowGeneratorServiceTest {
    private lateinit var slideShowService: SlideShowService
    private lateinit var templates: Templates
    private lateinit var layouts: Map<String, Layouts>
    private lateinit var service: SlideshowGeneratorService

    @BeforeEach
    fun setUp() {
        // Use real Template and Layout objects for test data
        templates =
            listOf(
                Template(
                    name = "template1",
                    description = "desc1",
                    filename = "file1.pptx",
                    layoutsFilename = "layouts1.json",
                ),
                Template(
                    name = "template2",
                    description = "desc2",
                    filename = "file2.pptx",
                    layoutsFilename = "layouts2.json",
                ),
            )
        layouts =
            mapOf(
                "template1" to
                    listOf(
                        Layout(name = "layoutA", description = "Layout A", index = 0),
                        Layout(name = "layoutB", description = "Layout B", index = 1),
                    ),
                "template2" to
                    listOf(
                        Layout(name = "layoutC", description = "Layout C", index = 0),
                    ),
            )
        slideShowService = mockk()
        service = SlideshowGeneratorService(slideShowService, templates, layouts)
    }

    @Test
    fun `listTemplates returns available templates`() {
        val result = service.listTemplates()
        assertTrue(result.contains("template1"))
        assertTrue(result.contains("template2"))
    }

    @Test
    fun `listTemplates throws if no templates`() {
        val emptyTemplates = emptyList<Template>()
        val serviceWithNoTemplates = SlideshowGeneratorService(slideShowService, emptyTemplates, layouts)
        val exception =
            assertThrows(IllegalStateException::class.java) {
                serviceWithNoTemplates.listTemplates()
            }
        assertTrue(exception.message!!.contains("No templates available"))
    }

    @Test
    fun `listLayouts returns layouts for template`() {
        val result = service.listLayouts("template1")
        assertTrue(result.contains("layoutA"))
        assertTrue(result.contains("layoutB"))
    }

    @Test
    fun `listLayouts throws if template not found`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.listLayouts("nonexistent")
            }
        assertTrue(exception.message!!.contains("template 'nonexistent' not found"))
    }
}
