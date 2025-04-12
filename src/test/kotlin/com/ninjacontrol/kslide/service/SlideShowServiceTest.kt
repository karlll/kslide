@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.service

import com.ninjacontrol.kslide.model.*
import com.ninjacontrol.kslide.repository.SlideShowRepository
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.io.File
import java.util.UUID

class SlideShowServiceTest {
    private lateinit var mockRepository: SlideShowRepository
    private lateinit var slideShowService: SlideShowService

    @BeforeEach
    fun setUp() {
        mockRepository = mock(SlideShowRepository::class.java)
        slideShowService = SlideShowServiceImpl(mockRepository)
    }

    @Test
    fun `test getActiveSlideShow throws exception when no active slideshow`() {
        val exception =
            assertThrows<IllegalStateException> {
                slideShowService.getActiveSlideShow()
            }
        assertEquals("No active slideshow exists", exception.message)
    }

    @Test
    fun `test getActiveSlideShowId throws exception when no active slideshow`() {
        val exception =
            assertThrows<IllegalStateException> {
                slideShowService.getActiveSlideShowId()
            }
        assertEquals("No active slideshow exists", exception.message)
    }

    @Test
    fun `test getSlideShowById returns slideshow from repository`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)

        // Act
        val result = slideShowService.getSlideShowById(id)

        // Assert
        assertNotNull(result)
        verify(mockRepository).get(id)
    }

    @Test
    fun `test getSlideShowById throws exception when slideshow not found`() {
        // Arrange
        val id = UUID.randomUUID()
        `when`(mockRepository.get(id)).thenReturn(null)

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                slideShowService.getSlideShowById(id)
            }
        assertEquals("SlideShow with id $id not found", exception.message)
        verify(mockRepository).get(id)
    }

    @Test
    fun `test setActiveSlideShow sets slideshow as active`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)

        // Act
        slideShowService.setActiveSlideShow(id)

        // Assert
        verify(mockRepository).get(id)

        // Verify the slideshow is now active by calling getActiveSlideShowId
        assertEquals(id, slideShowService.getActiveSlideShowId())
    }

    @Test
    fun `test setActiveSlideShow throws exception when slideshow not found`() {
        // Arrange
        val id = UUID.randomUUID()
        `when`(mockRepository.get(id)).thenReturn(null)

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                slideShowService.setActiveSlideShow(id)
            }
        assertEquals("SlideShow with id $id not found", exception.message)
        verify(mockRepository).get(id)
    }

    @Test
    fun `test getActiveSlide throws exception when no active slideshow`() {
        // Act & Assert
        val exception =
            assertThrows<IllegalStateException> {
                slideShowService.getActiveSlide()
            }
        assertEquals("No active slideshow", exception.message)
    }

    @Test
    fun `test getActiveSlide throws exception when no active slide`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id, withCurrentSlide = false)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act & Assert
        val exception =
            assertThrows<IllegalStateException> {
                slideShowService.getActiveSlide()
            }
        assertEquals("No active slide", exception.message)
    }

    @Test
    fun `test setActiveSlide sets slide as active`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id, withCurrentSlide = true)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act
        slideShowService.setActiveSlide(0) // Assuming slide number 0 exists

        // No exception means test passed
    }

    @Test
    fun `test getAllSlides returns slides from active slideshow`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act
        val slides = slideShowService.getAllSlides()

        // Assert
        assertNotNull(slides)
    }

    @Test
    fun `test getActiveSlideNumber returns slide number of active slide`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id, withCurrentSlide = true)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act
        val slideNumber = slideShowService.getActiveSlideNumber()

        // Assert
        assertEquals(1, slideNumber) // Slide number is 1-based
    }

    @Test
    fun `test createSlideShow creates new slideshow and adds to repository`() {
        // Act
        val id = slideShowService.createSlideShow()

        // Assert
        assertNotNull(id)
    }

    @Test
    fun `test createSlide creates new slide in active slideshow`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act
        val slideNumber = slideShowService.createSlide()

        // Assert
        assertNotNull(slideNumber)
    }

    @Test
    fun `test removeSlideShow removes slideshow from repository`() {
        // Arrange
        val id = UUID.randomUUID()

        // Act
        slideShowService.removeSlideShow(id)

        // Assert
        verify(mockRepository).remove(id)
    }

    @Test
    fun `test removeSlide removes slide from active slideshow`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id, withCurrentSlide = true)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act
        slideShowService.removeSlide(0) // Assuming slide number 0 exists

        // Assert
        // No exception means test passed
    }

    @Test
    fun `test saveActiveSlideShow saves active slideshow to repository`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)

        // Act
        val savedId = slideShowService.saveActiveSlideShow()

        // Assert
        assertEquals(id, savedId)
    }

    @Test
    fun `test exportActiveSlideShow exports active slideshow to file`() {
        // Arrange
        val id = UUID.randomUUID()
        val slideShowState = createTestSlideShowState(id)
        `when`(mockRepository.get(id)).thenReturn(slideShowState)
        slideShowService.setActiveSlideShow(id)
        val filename = "test_export.pptx"

        // Act
        slideShowService.exportActiveSlideShow(filename)

        // Assert
        val file = File(filename)
        assertTrue(file.exists())
        file.delete() // Clean up
    }

    // Helper method to create a test SlideShowState
    private fun createTestSlideShowState(
        id: UUID,
        withCurrentSlide: Boolean = false,
    ): SlideShowState {
        val ppt = XMLSlideShow()
        val slide = if (withCurrentSlide) ppt.createSlide() else null
        return SlideShowState(
            metadata =
                SlideShowMetadata(
                    id = id,
                    filename = "test.pptx",
                    title = "Test Slideshow",
                    createdAt = java.time.Instant.now(),
                    author = "Test Author",
                ),
            slides =
                SlideState(
                    ppt = ppt,
                    currentSlide = slide,
                    currentTextBox = null,
                    currentTextParagraph = null,
                    currentTextRun = null,
                ),
        )
    }
}
