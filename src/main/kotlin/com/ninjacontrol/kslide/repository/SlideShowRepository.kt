package com.ninjacontrol.kslide.repository

import com.ninjacontrol.kslide.model.SlideShowState
import java.util.UUID

interface SlideShowRepository {
    fun add(slideShow: SlideShowState)

    fun get(id: UUID): SlideShowState?

    fun remove(id: UUID)
}
