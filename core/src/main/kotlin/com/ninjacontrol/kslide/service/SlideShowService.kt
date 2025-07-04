@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.service

import com.ninjacontrol.kslide.model.*
import com.ninjacontrol.kslide.repository.SlideShowRepository
import com.ninjacontrol.kslide.util.RenderSlide
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xslf.usermodel.XSLFSlideLayout
import org.apache.poi.xslf.usermodel.XSLFTextBox
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.UUID

interface SlideShowService {
    /**
     * Retrieves the currently active slideshow.
     *
     * @return The active XMLSlideShow object.
     */
    fun getActiveSlideShow(): XMLSlideShow

    /**
     * Gets the UUID of the currently active slideshow.
     *
     * @return The UUID of the active slideshow.
     */
    fun getActiveSlideShowId(): UUID

    /**
     * Retrieves a slideshow by its unique identifier.
     *
     * @param id The UUID of the slideshow to retrieve.
     * @return The XMLSlideShow object corresponding to the given ID.
     */
    fun getSlideShowById(id: UUID): XMLSlideShow

    /**
     * Sets the specified slideshow as the active slideshow.
     *
     * @param id The UUID of the slideshow to set as active.
     */
    fun setActiveSlideShow(id: UUID)

    /**
     * Gets the currently active slide from the active slideshow.
     *
     * @return The active XSLFSlide object.
     */
    fun getActiveSlide(): XSLFSlide

    /**
     * Sets the specified slide as the active slide in the active slideshow.
     *
     * @param slideNumber The number of the slide to set as active.
     */
    fun setActiveSlide(slideNumber: Int)

    /**
     * Retrieves all slides from the active slideshow.
     *
     * @return A list of all XSLFSlide objects in the active slideshow.
     */
    fun getAllSlides(): List<XSLFSlide>

    /**
     * Gets the slide number of the currently active slide.
     *
     * @return The slide number of the active slide.
     */
    fun getActiveSlideNumber(): Int

    /**
     * Creates a new empty slideshow and sets it as the active slideshow.
     *
     * @return The UUID of the newly created slideshow.
     */
    fun createSlideShow(): UUID

    /**
     * Deletes the current slideshow from the repository.
     *
     */
    fun deleteSlideShow()

    /**
     * Creates a new empty slideshow using the master layout defined in the provided template file.
     * Sets the new slideshow to active slideshow.
     *
     * @param template The path to the template file.
     * @return The UUID of the newly created slideshow.
     */
    fun createSlideShow(template: String): UUID

    /**
     * Creates a new slide in the active slideshow and sets it as the active slide.
     *
     * @param title The title of the new slide.
     * @return The slide number of the newly created slide.
     */
    fun createSlide(title: String?): Int

    /**
     * Deletes the specified slide from the active slideshow.
     */
    fun deleteSlide(slideNumber: Int)

    /**
     * Creates a new text box in the active slide.
     *
     * @param x The x-coordinate of the text box.
     * @param y The y-coordinate of the text box.
     * @param width The width of the text box.
     * @param height The height of the text box.
     */
    fun createTextBox(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    )

    fun createParagraph(text: String?)

    fun addTextRunInActiveParagraph(text: String)

    fun addBullet(
        level: Int,
        text: String,
    )

    fun getActiveTextBox(): XSLFTextBox

    fun setActiveTextBox(id: Int)

    fun setTextRunInActiveParagraph(text: String)

    /**
     * Removes the specified slideshow from the repository.
     *
     * @param id The UUID of the slideshow to remove.
     */
    fun removeSlideShow(id: UUID)

    /**
     * Removes the specified slide from the active slideshow.
     *
     * @param number The slide number of the slide to remove.
     */
    fun removeSlide(number: Int)

    /**
     * Saves the current state of the active slideshow to the repository.
     *
     * @return The UUID of the saved slideshow.
     */
    fun saveActiveSlideShow(): UUID

    /**
     * Sets the active directory reading and writing files.
     *
     * @param directory The directory to set as active.
     */
    fun setActiveDirectory(directory: String)

    /**
     * Gets the active directory for reading and writing files.
     */
    fun getActiveDirectory(): String?

    /**
     * Exports the active slideshow to a file with the specified filename.
     *
     * @param filename The name of the file to export the slideshow to.
     */
    fun exportActiveSlideShow(filename: String)

    /**
     * Retrieves a list of available slide layouts.
     *
     * @return A list of XSLFSlideLayout objects representing the available layouts.
     */
    fun getAvailableLayouts(): List<Pair<Int, XSLFSlideLayout>>

    /**
     * Sets the active layout for the current slide.
     *
     * @param layout The XSLFSlideLayout object to set as active.
     */
    fun setActiveLayout(layout: XSLFSlideLayout)

    fun deleteActiveTextBox()

    fun deleteTextBox(id: Int)

    fun clearActiveTextBox()

    fun clearTextBox(id: Int)

    fun deleteActiveParagraph()

    fun renderSlideToImage(
        slideNumber: Int,
        outputFile: java.io.File,
    )

    fun renderSlidesToImages(
        outputPath: String,
        outputFilePrefix: String,
    )

    fun setProperty(
        key: String,
        value: String,
    )

    fun unsetProperty(key: String)

    fun getProperty(key: String): String?

    fun getProperties(): Map<String, String>

    /**
     * Processes markdown content and adds it to the specified text box placeholder.
     *
     * @param placeholderId The ID of the text box placeholder
     * @param markdownContent The markdown content to process
     */
    fun addMarkdownContent(
        placeholderId: Int,
        markdownContent: String,
    )

    /**
     * Creates formatted text runs with specific styling.
     *
     * @param text The text content
     * @param bold Whether to apply bold formatting
     * @param italic Whether to apply italic formatting
     * @param code Whether to apply code formatting (monospace font with background)
     */
    fun createFormattedTextRun(
        text: String,
        bold: Boolean = false,
        italic: Boolean = false,
        code: Boolean = false,
    )
}

class SlideShowServiceImpl(
    private val slideShowRepository: SlideShowRepository,
) : SlideShowService {
    // Cache for the active slideshow state
    private var activeSlideShowState: SlideShowState? = null
    private var activeDirectory: String? = null
    private val properties = HashMap<String, String>()

    override fun getActiveSlideShow(): XMLSlideShow = activeSlideShowState?.ppt ?: throw IllegalStateException("No active slideshow exists")

    override fun getActiveSlideShowId(): UUID = activeSlideShowState?.id ?: throw IllegalStateException("No active slideshow exists")

    override fun getSlideShowById(id: UUID): XMLSlideShow {
        // Check cache first
        if (activeSlideShowState?.id == id) {
            return activeSlideShowState!!.ppt
        }

        // If not in cache, get from repository
        val slideShowState =
            slideShowRepository.get(id) ?: throw IllegalArgumentException("SlideShow with id $id not found")
        activeSlideShowState = slideShowState // Update cache
        return slideShowState.ppt
    }

    override fun setActiveSlideShow(id: UUID) {
        // Check if already active
        if (activeSlideShowState?.id == id) {
            return
        }

        // Get from repository and update cache
        val slideShowState =
            slideShowRepository.get(id) ?: throw IllegalArgumentException("SlideShow with id $id not found")
        activeSlideShowState = slideShowState
    }

    override fun getAvailableLayouts(): List<Pair<Int, XSLFSlideLayout>> {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        val allLayouts = mutableListOf<Pair<Int, XSLFSlideLayout>>()

        state.ppt.slideMasters.forEachIndexed { idx, master ->
            master.slideLayouts.forEach { layout ->
                allLayouts.add(idx to layout)
            }
        }

        return allLayouts
    }

    override fun setActiveLayout(layout: XSLFSlideLayout) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.currentLayout = layout
    }

    override fun getActiveSlide(): XSLFSlide {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.currentSlide ?: throw IllegalStateException("No active slide")
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
        val currentSlide = state.currentSlide ?: throw IllegalStateException("No active slide")
        return currentSlide.slideNumber
    }

    override fun createSlideShow(): UUID {
        val newState = SlideShowState(filename = null, input = null)
        activeSlideShowState = newState
        slideShowRepository.add(newState)
        return newState.id
    }

    override fun createSlideShow(template: String): UUID {
        val newState = SlideShowState(filename = template, input = FileInputStream(template))
        activeSlideShowState = newState
        slideShowRepository.add(newState)
        return newState.id
    }

    override fun deleteSlideShow() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        slideShowRepository.remove(state.id)
        activeSlideShowState = null
    }

    override fun createSlide(title: String?): Int {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        val debugFlags =
            if (getProperty(Property.DEBUG_MODE.key) == DebugMode.ON.key) {
                val frame = if (getProperty(Property.DEBUG_FLAG_PLACEHOLDER_FRAME.key) != null) 1 else 0
                val id = if (getProperty(Property.DEBUG_FLAG_PLACEHOLDER_ID.key) != null) 1 else 0
                (id shl 1) + frame
            } else {
                0
            }
        val slideNumber = state.newSlide(title, debugFlags)

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
        state.newTextBox(x, y, width, height)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun deleteTextBox(id: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.deleteTextBox(id)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun clearActiveTextBox() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.clearTextBox()

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun clearTextBox(id: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.clearTextBox(id)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun deleteActiveTextBox() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.deleteTextBox(getActiveTextBox().shapeId)

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun setActiveTextBox(id: Int) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.setCurrentTextBox(id)
    }

    override fun createParagraph(text: String?) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.newTextParagraph()
        if (!text.isNullOrBlank()) {
            state.addTextInParagraph(text)
        }
    }

    override fun deleteActiveParagraph() {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.deleteCurrentTextParagraph()

        // Save the updated state
        slideShowRepository.add(state)
    }

    override fun addTextRunInActiveParagraph(text: String) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.addTextInParagraph(text)
    }

    override fun setTextRunInActiveParagraph(text: String) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.setTextInParagraph(text)
    }

    override fun addBullet(
        level: Int,
        text: String,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        state.newTextParagraph()
        state.addTextInParagraph(text)
        state.setBullet(true)
        state.setIndentLevel(level)
    }

    override fun getActiveTextBox(): XSLFTextBox {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        return state.currentTextBox as? XSLFTextBox ?: throw IllegalStateException("No active text box")
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

    override fun exportActiveSlideShow(filename: String) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        val fileOut = FileOutputStream(filename)
        state.ppt.write(fileOut)
        fileOut.close()
    }

    override fun setActiveDirectory(directory: String) {
        activeDirectory = directory
    }

    override fun getActiveDirectory(): String? = activeDirectory

    override fun renderSlideToImage(
        slideNumber: Int,
        outputFile: java.io.File,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        RenderSlide.renderSlideToPNG(state.ppt, slideNumber, outputFile)
    }

    override fun renderSlidesToImages(
        outputPath: String,
        outputFilePrefix: String,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")
        for (i in 0 until state.ppt.slides.size) {
            val outputFile = java.io.File(Paths.get(outputPath, "$outputFilePrefix-slide-$i.png").toString())
            RenderSlide.renderSlideToPNG(state.ppt, i, outputFile)
        }
    }

    override fun setProperty(
        key: String,
        value: String,
    ) {
        properties[key] = value
    }

    override fun unsetProperty(key: String) {
        properties.remove(key)
    }

    override fun getProperty(key: String): String? = properties[key]

    override fun getProperties(): Map<String, String> = properties

    override fun addMarkdownContent(
        placeholderId: Int,
        markdownContent: String,
    ) {
        val markdownProcessor =
            com.ninjacontrol.kslide.util
                .MarkdownProcessor(this)
        markdownProcessor.processMarkdownToSlide(placeholderId, markdownContent)
    }

    override fun createFormattedTextRun(
        text: String,
        bold: Boolean,
        italic: Boolean,
        code: Boolean,
    ) {
        val state = activeSlideShowState ?: throw IllegalStateException("No active slideshow")

        // Add text to the current paragraph
        if (state.currentTextParagraph == null) {
            createParagraph(null)
        }

        state.currentTextRun = state.currentTextParagraph?.addNewTextRun()

        if (bold) {
            state.setBold(true)
        }
        if (italic) {
            state.setItalic(true)
        }
        if (code) {
            state.setFontFamily("Consolas")
            state.setFontBackgroundColor("#F5F5F5")
            state.setFontColor("#333333")
        }

        state.currentTextRun?.setText(text)
    }
}
