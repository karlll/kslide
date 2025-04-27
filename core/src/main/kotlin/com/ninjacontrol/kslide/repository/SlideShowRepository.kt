package com.ninjacontrol.kslide.repository

import com.ninjacontrol.kslide.model.SlideShowState
import java.util.UUID

/**
 * Repository interface for storing and retrieving slideshows.
 */
interface SlideShowRepository {
    /**
     * Adds or updates a slideshow in the repository.
     *
     * @param slideShow The slideshow to add or update.
     */
    fun add(slideShow: SlideShowState)

    /**
     * Retrieves a slideshow by its unique identifier.
     *
     * @param id The UUID of the slideshow to retrieve.
     * @return The slideshow with the given ID, or null if not found.
     */
    fun get(id: UUID): SlideShowState?

    /**
     * Removes a slideshow from the repository.
     *
     * @param id The UUID of the slideshow to remove.
     */
    fun remove(id: UUID)
}