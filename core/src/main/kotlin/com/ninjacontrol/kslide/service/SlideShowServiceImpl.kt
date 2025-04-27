@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.service

import com.ninjacontrol.kslide.model.SlideShowState
import com.ninjacontrol.kslide.repository.SlideShowRepository
import com.ninjacontrol.kslide.util.RenderSlide
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xslf.usermodel.XSLFSlideLayout
import org.apache.poi.xslf.usermodel.XSLFTextBox
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

/**
 * Implementation of the SlideShowService interface.
 */
class SlideShowServiceImpl(
    private val slideShowRepository: SlideShowRepository,
) : SlideShowService {
    // Cache for the active slideshow state
    private var activeSlideShowState: SlideShowState? = null
    private var activeDirectory: String? = null

    override fun getActiveSlideShow(): XMLSlideShow {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.ppt
    }

    override fun getActiveSlideShowId(): UUID {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.id
    }

    override fun getSlideShowById(id: UUID): XMLSlideShow {
        // Check if the requested slideshow is already active
        if (activeSlideShowState?.id == id) {
            return activeSlideShowState!!.ppt
        }

        // Otherwise, load it from the repository
        val state = slideShowRepository.get(id) ?: throw IllegalArgumentException("No slideshow found with id $id")
        return state.ppt
    }

    override fun setActiveSlideShow(id: UUID) {
        // If already active, do nothing
        if (activeSlideShowState?.id == id) {
            return
        }

        // Load from repository
        val state = slideShowRepository.get(id) ?: throw IllegalArgumentException("No slideshow found with id $id")
        activeSlideShowState = state
    }

    override fun getActiveSlide(): XSLFSlide {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.getCurrentSlide()
    }

    override fun setActiveSlide(slideNumber: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.setCurrentSlide(slideNumber)
    }

    override fun getAllSlides(): List<XSLFSlide> {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.ppt.slides
    }

    override fun getActiveSlideNumber(): Int {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.currentSlideIndex
    }

    override fun createSlideShow(): UUID {
        val state = SlideShowState()
        activeSlideShowState = state
        slideShowRepository.add(state)
        return state.id
    }

    override fun deleteSlideShow() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        slideShowRepository.remove(state.id)
        activeSlideShowState = null
    }

    override fun createSlideShow(template: String): UUID {
        val state = SlideShowState(template)
        activeSlideShowState = state
        slideShowRepository.add(state)
        return state.id
    }

    override fun createSlide(title: String?): Int {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        val slideNumber = state.createSlide(title)

        // Save the updated state
        slideShowRepository.add(state)
        return slideNumber
    }

    override fun deleteSlide(slideNumber: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.removeSlide(slideNumber)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun createTextBox(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.createTextBox(x, y, width, height)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun createParagraph(text: String?) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.newTextParagraph(text)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun addTextRun(text: String) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.newTextRun(text)
    }

    override fun addBullet(
        level: Int,
        text: String,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.newTextParagraph()
        state.newTextRun(text)
        state.setBullet(true)
        state.setIndentLevel(level)
    }

    override fun getActiveTextBox(): XSLFTextBox {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.currentTextBox as? XSLFTextBox ?: throw IllegalStateException("No active text box")
    }

    override fun setActiveTextBox(id: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.setCurrentTextBox(id)
    }

    override fun removeSlideShow(id: UUID) {
        // If removing active slideshow, clear cache
        if (activeSlideShowState?.id == id) {
            activeSlideShowState = null
        }

        slideShowRepository.remove(id)
    }

    override fun removeSlide(number: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.removeSlide(number)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun saveActiveSlideShow(): UUID {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        slideShowRepository.add(state)
        return state.id
    }

    override fun setActiveDirectory(directory: String) {
        activeDirectory = directory
    }

    override fun getActiveDirectory(): String? = activeDirectory

    override fun exportActiveSlideShow(filename: String) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        val fileOut = FileOutputStream(filename)
        state.ppt.write(fileOut)
        fileOut.close()
    }

    override fun getAvailableLayouts(): List<Pair<Int, XSLFSlideLayout>> {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        val layouts = mutableListOf<Pair<Int, XSLFSlideLayout>>()

        state.ppt.slideMasters.forEachIndexed { masterIndex, master ->
            master.slideLayouts.forEachIndexed { layoutIndex, layout ->
                layouts.add(Pair(masterIndex * 100 + layoutIndex, layout))
            }
        }

        return layouts
    }

    override fun setActiveLayout(layout: XSLFSlideLayout) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        // Note: In newer versions of Apache POI, slideLayout is a val and cannot be reassigned
        // We would need to create a new slide with the desired layout and copy the content
        // For now, we'll just save the state without changing the layout

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun deleteActiveTextBox() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.deleteCurrentTextBox()

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun deleteTextBox(id: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.deleteTextBox(id)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun deleteActiveParagraph() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.deleteCurrentParagraph()

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun renderSlideToImage(
        slideNumber: Int,
        outputFile: File,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        RenderSlide.renderSlideToPNG(state.ppt, slideNumber, outputFile)
    }
}
