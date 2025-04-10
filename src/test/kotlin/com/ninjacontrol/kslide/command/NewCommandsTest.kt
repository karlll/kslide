package com.ninjacontrol.kslide.command

import com.ninjacontrol.kslide.service.SlideShowService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class NewCommandsTest {

    @Mock
    private lateinit var slideShowService: SlideShowService

    private lateinit var newCommands: NewCommands
    private val standardOut = System.out
    private val outputStreamCaptor = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        // Set up the command class with the mocked service
        newCommands = NewCommands(slideShowService)

        // Set up output capturing
        System.setOut(PrintStream(outputStreamCaptor))
        outputStreamCaptor.reset()
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        // Restore the standard output
        System.setOut(standardOut)
    }

    @Test
    fun `test newSlideshow creates a new slideshow and prints the ID`() {
        // Arrange
        val testId = UUID.randomUUID()
        `when`(slideShowService.createSlideShow()).thenReturn(testId)

        // Act
        newCommands.newSlideshow()

        // Assert
        verify(slideShowService, times(1)).createSlideShow()
        assertEquals("Created new slideshow with id $testId", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `test newSlideInSlideshow with null ID and no current slideshow`() {
        // Arrange
        `when`(slideShowService.currentSlideShowId).thenReturn(null)

        // Act
        newCommands.newSlideInSlideshow(null)

        // Assert
        // The property is accessed only once in the check, then the method returns early
        verify(slideShowService, times(1)).currentSlideShowId
        verify(slideShowService, times(0)).createSlide()
        assertEquals("No current slideshow", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `test newSlideInSlideshow with null ID and current slideshow`() {
        // Arrange
        val currentId = UUID.randomUUID()
        val slideNumber = 3
        `when`(slideShowService.currentSlideShowId).thenReturn(currentId)
        `when`(slideShowService.createSlide()).thenReturn(slideNumber)

        // Act
        newCommands.newSlideInSlideshow(null)

        // Assert
        // The property is accessed in the check and in the output message
        verify(slideShowService, times(2)).currentSlideShowId
        verify(slideShowService, times(1)).createSlide()
        assertEquals("Created new slide (#$slideNumber) in slideshow ($currentId)", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `test newSlideInSlideshow with valid ID`() {
        // Arrange
        val slideShowId = UUID.randomUUID()
        val slideNumber = 2
        `when`(slideShowService.loadSlideShow(slideShowId)).thenReturn(slideShowId)
        `when`(slideShowService.createSlide()).thenReturn(slideNumber)
        `when`(slideShowService.currentSlideShowId).thenReturn(slideShowId)

        // Act
        newCommands.newSlideInSlideshow(slideShowId.toString())

        // Assert
        verify(slideShowService, times(1)).loadSlideShow(slideShowId)
        verify(slideShowService, times(1)).createSlide()
        assertEquals("Created new slide (#$slideNumber) in slideshow ($slideShowId)", outputStreamCaptor.toString().trim())
    }

    @Test
    fun `test newSlideInSlideshow with invalid UUID format`() {
        // Arrange
        val invalidId = "not-a-uuid"

        // Act
        newCommands.newSlideInSlideshow(invalidId)

        // Assert
        verifyNoInteractions(slideShowService)
        assertEquals("Invalid UUID format: $invalidId", outputStreamCaptor.toString().trim())
    }
}
