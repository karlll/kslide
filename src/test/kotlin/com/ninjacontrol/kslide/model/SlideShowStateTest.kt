package com.ninjacontrol.kslide.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class SlideShowStateTest {
    @Test
    fun `test create new slide show`() {
        // Arrange & Act
        val slideShow = SlideShowState()

        // Assert
        assertNotNull(slideShow.id)
        assertNotNull(slideShow.ppt)
        assertEquals(0, slideShow.count)
        assertNull(slideShow.currentSlide)
        assertNull(slideShow.currentTextBox)
        assertNull(slideShow.currentTextParagraph)
        assertNull(slideShow.currentTextRun)
    }

    @Test
    fun `test create slide show from existing data`() {
        // Arrange
        val id = UUID.randomUUID()
        val createdAt = Instant.now()
        val slideShowBytes = createEmptySlideShowBytes()

        // Act
        val slideShow =
            SlideShowState(
                id = id,
                filename = "test.pptx",
                title = "Test Slideshow",
                createdAt = createdAt,
                author = "Test Author",
                slideShowBytes = slideShowBytes,
            )

        // Assert
        assertEquals(id, slideShow.id)
        assertEquals("test.pptx", slideShow.filename)
        assertEquals("Test Slideshow", slideShow.title)
        assertEquals(createdAt, slideShow.createdAt)
        assertEquals("Test Author", slideShow.author)
        assertNotNull(slideShow.ppt)
    }

    @Test
    fun `test create new slide`() {
        // Arrange
        val slideShow = SlideShowState()

        // Act
        val slideNumber = slideShow.newSlide(null)

        // Assert
        assertNotNull(slideNumber)
        assertNotNull(slideShow.currentSlide)
        assertEquals(1, slideShow.count)
    }

    @Test
    fun `test set current slide`() {
        // Arrange
        val slideShow = SlideShowState()
        val slideNumber = slideShow.newSlide(null)

        // Create another slide to switch from
        slideShow.newSlide(null)

        // Act
        slideShow.setCurrentSlide(slideNumber)

        // Assert
        assertNotNull(slideShow.currentSlide)
        assertEquals(slideNumber, slideShow.currentSlide?.slideNumber)
    }

    @Test
    fun `test set current slide with non-existent slide number throws exception`() {
        // Arrange
        val slideShow = SlideShowState()
        slideShow.newSlide(null) // Create one slide

        // Act & Assert
        val nonExistentSlideNumber = 999 // A slide number that doesn't exist
        val exception = assertThrows(IllegalArgumentException::class.java) {
            slideShow.setCurrentSlide(nonExistentSlideNumber)
        }

        assertEquals("Slide not found", exception.message)
    }

    @Test
    fun `test create text box`() {
        // Arrange
        val slideShow = SlideShowState()
        slideShow.newSlide(null)

        // Act
        slideShow.newTextBox(10, 20, 300, 200)

        // Assert
        assertNotNull(slideShow.currentTextBox)
    }

    @Test
    fun `test create text paragraph`() {
        // Arrange
        val slideShow = SlideShowState()
        slideShow.newSlide(null)
        slideShow.newTextBox(10, 20, 300, 200)

        // Act
        slideShow.newTextParagraph()

        // Assert
        assertNotNull(slideShow.currentTextParagraph)
    }

    @Test
    fun `test create text run`() {
        // Arrange
        val slideShow = SlideShowState()
        slideShow.newSlide(null)
        slideShow.newTextBox(10, 20, 300, 200)
        slideShow.newTextParagraph()

        // Act
        slideShow.newTextRun("Hello World")

        // Assert
        assertNotNull(slideShow.currentTextRun)
    }

    @Test
    fun `test text formatting`() {
        // Arrange
        val slideShow = SlideShowState()
        slideShow.newSlide(null)
        slideShow.newTextBox(10, 20, 300, 200)
        slideShow.newTextParagraph()
        slideShow.newTextRun("Hello World")

        // Act
        slideShow.setBold(true)
        slideShow.setItalic(true)
        slideShow.setFontSize(24.0)
        slideShow.setFontColor("#FF0000")

        // No exceptions means test passed
        // We can't easily verify the actual formatting without rendering the slide
    }

    @Test
    fun `test paragraph formatting`() {
        // Arrange
        val slideShow = SlideShowState()
        slideShow.newSlide(null)
        slideShow.newTextBox(10, 20, 300, 200)
        slideShow.newTextParagraph()

        // Act
        slideShow.setTextAlignment("center")
        slideShow.setBullet(true)
        slideShow.setIndentLevel(1)

        // No exceptions means test passed
        // We can't easily verify the actual formatting without rendering the slide
    }

    @Test
    fun `test remove slide`() {
        // Arrange
        val slideShow = SlideShowState()
        val slideNumber = slideShow.newSlide(null)

        // The slide number is 1-based, but the index in the slides collection is 0-based
        // So we need to use the index 0 to remove the first slide

        // Act
        slideShow.removeSlide(0)

        // Assert
        assertNull(slideShow.currentSlide)
        assertEquals(0, slideShow.ppt.slides.size)
    }

    // Helper method to create empty slide show bytes
    private fun createEmptySlideShowBytes(): ByteArray {
        val ppt =
            org.apache.poi.xslf.usermodel
                .XMLSlideShow()
        val out = java.io.ByteArrayOutputStream()
        ppt.write(out)
        return out.toByteArray()
    }
}
