@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.mcp

import com.ninjacontrol.kslide.mcp.model.Layout
import com.ninjacontrol.kslide.mcp.model.Layouts
import com.ninjacontrol.kslide.mcp.model.Template
import com.ninjacontrol.kslide.mcp.model.Templates
import com.ninjacontrol.kslide.service.SlideShowService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import java.util.UUID

class SlideshowGeneratorServiceTest {
    private lateinit var slideShowService: SlideShowService
    private lateinit var templates: Templates
    private lateinit var layouts: Map<String, Layouts>
    private lateinit var service: SlideshowGeneratorService
    private lateinit var templatePath: java.nio.file.Path
    private lateinit var outputPath: java.nio.file.Path

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
        templatePath = Paths.get("/tmp/template")
        outputPath = Paths.get("/tmp/output")
        service = SlideshowGeneratorService(slideShowService, templates, layouts, templatePath, outputPath)
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
        val serviceWithNoTemplates =
            SlideshowGeneratorService(slideShowService, emptyTemplates, layouts, templatePath, outputPath)
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

    @Test
    fun `createSlideWithMarkdown should process markdown content correctly`() {
        val slideshowId = UUID.randomUUID().toString()
        val layoutIndex = 0
        val content = mapOf(
            1 to "This is **bold** text",
            2 to "- First bullet\n- Second bullet"
        )

        every { slideShowService.setActiveSlideShow(UUID.fromString(slideshowId)) } returns Unit
        every { slideShowService.getAvailableLayouts() } returns listOf(0 to mockk())
        every { slideShowService.setActiveLayout(any()) } returns Unit
        every { slideShowService.createSlide(null) } returns 1
        every { slideShowService.addMarkdownContent(any(), any()) } returns Unit

        val result = service.createSlideWithMarkdown(slideshowId, layoutIndex, content)

        verify {
            slideShowService.setActiveSlideShow(UUID.fromString(slideshowId))
            slideShowService.createSlide(null)
            slideShowService.addMarkdownContent(1, "This is **bold** text")
            slideShowService.addMarkdownContent(2, "- First bullet\n- Second bullet")
        }

        assertTrue(result.contains("Created slide (#1)"))
        assertTrue(result.contains("markdown content"))
    }

    @Test
    fun `createSlideWithMarkdown should throw exception for invalid layout index`() {
        val slideshowId = UUID.randomUUID().toString()
        val invalidLayoutIndex = 999
        val content = mapOf(1 to "test content")

        every { slideShowService.setActiveSlideShow(UUID.fromString(slideshowId)) } returns Unit
        every { slideShowService.getAvailableLayouts() } returns listOf(0 to mockk())

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.createSlideWithMarkdown(slideshowId, invalidLayoutIndex, content)
        }

        assertTrue(exception.message!!.contains("Invalid layout index"))
    }
}
