package com.ninjacontrol.kslide.repository

import com.ninjacontrol.kslide.model.SlideShowState
import com.ninjacontrol.kslide.model.init
import com.ninjacontrol.kslide.model.new
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SlideShowRepositorySQLiteTest {

    private lateinit var repository: SlideShowRepositorySQLite
    private val testDbUrl = "jdbc:sqlite:test-slideshows.db"
    private lateinit var testSlideShow: SlideShowState
    private lateinit var testId: UUID

    @BeforeEach
    fun setUp() {
        // Create repository with test database
        repository = SlideShowRepositorySQLite(testDbUrl)
        repository.initialize()

        // Create a test slide show
        testSlideShow = new()
        testId = testSlideShow.id
    }

    @AfterEach
    fun tearDown() {
        // Delete the test database file
        File("test-slideshows.db").delete()
    }

    @Test
    fun `test initialize creates table`() {
        // This is implicitly tested in setUp, but we can add additional assertions if needed
        // For example, we could check if we can add and retrieve a slide show
        repository.add(testSlideShow)
        val retrieved = repository.get(testId)
        assertNotNull(retrieved)
    }

    @Test
    fun `test add and get slide show`() {
        // Add a slide show
        repository.add(testSlideShow)

        // Retrieve it
        val retrieved = repository.get(testId)

        // Verify it was retrieved correctly
        assertNotNull(retrieved)
        assertEquals(testId, retrieved.id)
        assertEquals(testSlideShow.metadata.filename, retrieved.metadata.filename)
        assertEquals(testSlideShow.metadata.title, retrieved.metadata.title)
        assertEquals(testSlideShow.metadata.author, retrieved.metadata.author)
    }

    @Test
    fun `test get non-existent slide show returns null`() {
        // Generate a random UUID that doesn't exist in the database
        val nonExistentId = UUID.randomUUID()

        // Try to retrieve it
        val retrieved = repository.get(nonExistentId)

        // Verify it returns null
        assertNull(retrieved)
    }

    @Test
    fun `test remove slide show`() {
        // Add a slide show
        repository.add(testSlideShow)

        // Verify it was added
        val retrievedBeforeRemove = repository.get(testId)
        assertNotNull(retrievedBeforeRemove)

        // Remove it
        repository.remove(testId)

        // Verify it was removed
        val retrievedAfterRemove = repository.get(testId)
        assertNull(retrievedAfterRemove)
    }

    @Test
    fun `test update existing slide show`() {
        // Add a slide show
        repository.add(testSlideShow)

        // Create a modified version with the same ID but different content
        val baos = ByteArrayOutputStream()
        testSlideShow.slides.ppt.write(baos)
        val slideShowBytes = baos.toByteArray()

        val modifiedTitle = "Modified Title"
        val modifiedAuthor = "Modified Author"

        val modifiedSlideShow = init(
            id = testId,
            filename = testSlideShow.metadata.filename,
            title = modifiedTitle,
            createdAt = Instant.now(),
            author = modifiedAuthor,
            slideShowBytes = slideShowBytes
        )

        // Add the modified version (which should update the existing one)
        repository.add(modifiedSlideShow)

        // Retrieve it
        val retrieved = repository.get(testId)

        // Verify it was updated
        assertNotNull(retrieved)
        assertEquals(testId, retrieved.id)
        assertEquals(modifiedTitle, retrieved.metadata.title)
        assertEquals(modifiedAuthor, retrieved.metadata.author)
    }
}
