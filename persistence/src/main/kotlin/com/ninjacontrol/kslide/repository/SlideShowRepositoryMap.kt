package com.ninjacontrol.kslide.repository

import com.ninjacontrol.kslide.model.SlideShowState
import java.util.UUID

/**
 * In-memory implementation of the SlideShowRepository interface.
 */
class SlideShowRepositoryMap : SlideShowRepository {
    private val slideShows = mutableMapOf<UUID, SlideShowState>()

    override fun add(slideShow: SlideShowState) {
        slideShows[slideShow.id] = slideShow
    }

    override fun get(id: UUID): SlideShowState? = slideShows[id]

    override fun remove(id: UUID) {
        slideShows.remove(id)
    }
}