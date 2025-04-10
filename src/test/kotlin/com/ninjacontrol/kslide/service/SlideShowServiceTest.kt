package com.ninjacontrol.kslide.service

import com.ninjacontrol.kslide.model.SlideShowState
import com.ninjacontrol.kslide.model.new
import com.ninjacontrol.kslide.repository.SlideShowRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SlideShowServiceTest {

    private lateinit var service: SlideShowService
    private lateinit var mockRepository: MockSlideShowRepository
    private lateinit var testSlideShow: SlideShowState
    private lateinit var testId: UUID

    @BeforeEach
    fun setUp() {
        // Create a mock repository
        mockRepository = MockSlideShowRepository()

        // Create the service with the mock repository
        service = SlideShowService(mockRepository)

        // Create a test slide show
        testSlideShow = new()
        testId = testSlideShow.id

        // Add the test slide show to the repository
        mockRepository.add(testSlideShow)
    }

    @Test
    fun `test createSlideShow creates and sets current slideshow`() {
        // Create a new slideshow
        val id = service.createSlideShow()

        // Verify that the slideshow was added to the repository
        val slideShow = mockRepository.get(id)
        assertNotNull(slideShow)

        // Verify that the current slideshow ID was set
        assertEquals(id, service.currentSlideShowId)
    }

    @Test
    fun `test createSlide adds slide to current slideshow`() {
        // Set the current slideshow
        service.loadSlideShow(testId)

        // Create a slide
        val slideCount = service.createSlide()

        // Verify that the slide was added to the slideshow
        val slideShow = mockRepository.get(testId)
        assertNotNull(slideShow)
        assertEquals(1, slideCount)
        assertEquals(1, slideShow.slides.count)
    }

    @Test
    fun `test createSlide throws exception when no current slideshow`() {
        // Ensure there's no current slideshow
        service.removeSlideShow(testId)

        // Attempt to create a slide should throw an exception
        assertThrows<IllegalStateException> {
            service.createSlide()
        }
    }

    @Test
    fun `test createSlide throws exception when current slideshow not found`() {
        // Create a test slideshow and set it as current
        val tempId = service.createSlideShow()

        // Remove it from the repository but keep the ID as current
        mockRepository.remove(tempId)

        // Attempt to create a slide should throw an exception
        assertThrows<IllegalArgumentException> {
            service.createSlide()
        }
    }

    @Test
    fun `test removeSlideShow removes slideshow from repository`() {
        // Set the current slideshow
        service.loadSlideShow(testId)

        // Remove the slideshow
        service.removeSlideShow(testId)

        // Verify that the slideshow was removed from the repository
        val slideShow = mockRepository.get(testId)
        assertNull(slideShow)

        // Verify that the current slideshow ID was cleared
        assertNull(service.currentSlideShowId)
    }

    @Test
    fun `test removeSlideShow does not affect current slideshow if different`() {
        // Create another slideshow
        val anotherId = service.createSlideShow()

        // Set the test slideshow as current
        service.loadSlideShow(testId)

        // Remove the other slideshow
        service.removeSlideShow(anotherId)

        // Verify that the current slideshow ID is still set to the test slideshow
        assertEquals(testId, service.currentSlideShowId)
    }

    @Test
    fun `test loadSlideShow sets current slideshow`() {
        // Load the test slideshow
        val id = service.loadSlideShow(testId)

        // Verify that the current slideshow ID was set
        assertEquals(testId, service.currentSlideShowId)
        assertEquals(testId, id)
    }

    @Test
    fun `test loadSlideShow throws exception when slideshow not found`() {
        // Attempt to load a non-existent slideshow
        val nonExistentId = UUID.randomUUID()

        // Should throw an exception
        assertThrows<IllegalArgumentException> {
            service.loadSlideShow(nonExistentId)
        }
    }

    // Mock implementation of SlideShowRepository for testing
    private class MockSlideShowRepository : SlideShowRepository {
        private val slideshows = mutableMapOf<UUID, SlideShowState>()

        override fun add(slideShow: SlideShowState) {
            slideshows[slideShow.id] = slideShow
        }

        override fun get(id: UUID): SlideShowState? {
            return slideshows[id]
        }

        override fun remove(id: UUID) {
            slideshows.remove(id)
        }
    }
}
