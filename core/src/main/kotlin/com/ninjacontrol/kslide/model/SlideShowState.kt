@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.model

import org.apache.poi.sl.draw.DrawPaint
import org.apache.poi.sl.usermodel.AutoNumberingScheme
import org.apache.poi.sl.usermodel.Placeholder
import org.apache.poi.sl.usermodel.TextParagraph
import org.apache.poi.xslf.usermodel.*
import java.awt.Color
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.time.Instant
import java.util.*

/**
 * A mutable implementation of SlideShow functionality.
 * This class replaces the functional approach with a mutable class-based approach.
 */
class SlideShowState {
    // Internal mutable state
    var id: UUID
    var filename: String?
    var title: String?
    var createdAt: Instant
    var author: String?
    var ppt: XMLSlideShow
    var currentSlide: XSLFSlide? = null
    var currentTextBox: XSLFTextShape? = null
    var currentTextParagraph: XSLFTextParagraph? = null
    var currentTextRun: XSLFTextRun? = null
    var currentLayout: XSLFSlideLayout? = null
    var count: Int = 0

    /**
     * Creates a new empty slide show.
     * @param input Optional input stream to load an existing slide show
     */
    constructor(filename: String?, input: FileInputStream?) {
        id = UUID.randomUUID()
        this.filename = filename
        title = null
        createdAt = Instant.now()
        author = null
        ppt =
            if (input != null) {
                XMLSlideShow(input)
            } else {
                XMLSlideShow()
            }
    }

    /**
     * Creates a slide show from existing data.
     */
    constructor(
        id: UUID,
        filename: String?,
        title: String?,
        createdAt: Instant,
        author: String?,
        slideShowBytes: ByteArray,
    ) {
        this.id = id
        this.filename = filename
        this.title = title
        this.createdAt = createdAt
        this.author = author
        this.ppt = XMLSlideShow(ByteArrayInputStream(slideShowBytes))
    }

    /**
     * Creates a new slide.
     *
     * @param title Optional title for the slide
     * @return The number of the created slide
     */
    fun newSlide(
        title: String?,
        debugFlags: Int = 0,
    ): Int {
        val highlightPlaceholders = debugFlags and 0x1 != 0
        val addPlaceholderId = debugFlags and 0x2 != 0
        currentSlide =
            if (currentLayout == null) {
                ppt.createSlide()
            } else {
                ppt.createSlide(currentLayout)
            }

        if (currentSlide != null) {
            for (placeholder in currentSlide!!.placeholders) {
                if (placeholder is XSLFTextShape) {
                    if (!title.isNullOrEmpty() && placeholder.placeholder == Placeholder.TITLE) {
                        placeholder.text = title
                    }

                    // Apply debug highlighting if enabled
                    if (highlightPlaceholders) {
                        placeholder.lineColor = Color.RED
                        placeholder.lineWidth = 2.0
                    }
                    if (addPlaceholderId) {
                        val text = if (placeholder.placeholder == Placeholder.PICTURE) "Picture" else placeholder.text.trim()
                        placeholder.text = "$text id=${placeholder.shapeId}"
                    }
                }
            }
        }

        count += 1
        return currentSlide?.slideNumber ?: throw IllegalStateException("Failed to create new slide")
    }

    /**
     * Removes a slide by its number.
     *
     * @param slideNumber The number of the slide to remove
     */
    fun removeSlide(slideNumber: Int) {
        val slideToRemove = ppt.slides.find { it.slideNumber == slideNumber }
        val slideIndex = ppt.slides.indexOf(slideToRemove)

        ppt.removeSlide(slideIndex)

        // If the current slide was the one removed, set it to null
        if (currentSlide == slideToRemove) {
            currentSlide = null
        }
    }

    /**
     * Sets the current slide by its number.
     *
     * @param slideNumber The number of the slide to set as current
     */
    fun setCurrentSlide(slideNumber: Int) {
        currentSlide = ppt.slides.find { it.slideNumber == slideNumber } ?: throw IllegalArgumentException("Slide not found")
    }

    /**
     * Creates a new text box on the current slide.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width of the text box
     * @param height Height of the text box
     * @return The ID of the created text box, -1 if creation failed
     */
    fun newTextBox(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ): Int {
        currentTextBox = currentSlide?.createTextBox()
        currentTextBox?.setAnchor(Rectangle(x, y, width, height))
        return currentTextBox?.shapeId ?: -1
    }

    /**
     * Gets text boxes in the current slide.
     *
     * @return List of text boxes in the current slide
     */
    fun getTextBoxes(): List<XSLFTextBox> = currentSlide?.shapes?.filterIsInstance<XSLFTextBox>() ?: emptyList()

    fun getCurrentTextBox(): XSLFTextBox? = currentTextBox as? XSLFTextBox

    fun setCurrentTextBox(id: Int) {
        currentTextBox = currentSlide?.placeholders?.find { it.shapeId == id }
        if (currentTextBox == null) {
            throw IllegalArgumentException("Text box with ID $id not found")
        }
    }

    fun deleteTextBox(id: Int) {
        val textBoxToDelete = currentSlide?.placeholders?.find { it.shapeId == id }
        if (textBoxToDelete != null) {
            if (currentTextBox == textBoxToDelete) {
                currentTextBox = null
            }
            currentSlide?.removeShape(textBoxToDelete)
        } else {
            throw IllegalArgumentException("Text box with ID $id not found")
        }
    }

    fun clearTextBox() {
        currentTextBox?.clearText()
    }

    fun clearTextBox(id: Int) {
        val textBoxToClear = currentSlide?.placeholders?.find { it.shapeId == id }
        if (textBoxToClear != null) {
            textBoxToClear.clearText()
        } else {
            throw IllegalArgumentException("Text box with ID $id not found")
        }
    }

    /**
     * Creates a new text paragraph in the current text box.
     */
    fun newTextParagraph() {
        currentTextParagraph = currentTextBox?.addNewTextParagraph()
    }

    /**
     * Sets the text for the current text paragraph.
     * Overwrites any existing text runs in the paragraph.
     */
    fun setTextInParagraph(text: String) {
        currentTextParagraph?.textRuns?.clear() // clear all existing text runs
        currentTextRun = currentTextParagraph?.addNewTextRun()
        currentTextRun?.setText(text)
    }

    fun deleteCurrentTextParagraph() {
        currentTextBox?.removeTextParagraph(currentTextParagraph)
        currentTextParagraph = null
    }

    /**
     * Creates a new text run in the current paragraph.
     *
     * @param text The text to add
     */
    fun addTextInParagraph(text: String) {
        currentTextRun = currentTextParagraph?.addNewTextRun()
        currentTextRun?.setText(text)
    }

    /**
     * Creates a new hyperlink in the current paragraph.
     *
     * @param text The text to display
     * @param url The URL to link to
     */
    fun newHyperlink(
        text: String,
        url: String,
    ) {
        currentTextRun = currentTextParagraph?.addNewTextRun()
        currentTextRun?.setText(text)
        val link = currentTextRun?.createHyperlink()
        link?.address = url
    }

    // Paragraph layout methods

    /**
     * Sets the indent for the current paragraph.
     *
     * @param indent The indent value
     */
    fun setIndent(indent: Double) {
        currentTextParagraph?.setIndent(indent)
    }

    /**
     * Sets the indent level for the current paragraph.
     *
     * @param level The indent level
     */
    fun setIndentLevel(level: Int) {
        currentTextParagraph?.indentLevel = level
    }

    /**
     * Adds a line break to the current paragraph.
     */
    fun addLinebreak() {
        currentTextParagraph?.addLineBreak()
    }

    /**
     * Sets the text alignment for the current paragraph.
     *
     * @param alignment The alignment ("left", "center", "right", "justify", "distribute")
     */
    fun setTextAlignment(alignment: String) {
        currentTextParagraph?.textAlign =
            when (alignment) {
                "left" -> TextParagraph.TextAlign.LEFT
                "center" -> TextParagraph.TextAlign.CENTER
                "right" -> TextParagraph.TextAlign.RIGHT
                "justify" -> TextParagraph.TextAlign.JUSTIFY
                "distribute" -> TextParagraph.TextAlign.DIST
                else -> TextParagraph.TextAlign.LEFT
            }
    }

    /**
     * Sets the font alignment for the current paragraph.
     *
     * @param alignment The alignment ("auto", "baseline", "center", "top", "bottom")
     */
    fun setFontAlignment(alignment: String) {
        currentTextParagraph?.fontAlign =
            when (alignment) {
                "auto" -> TextParagraph.FontAlign.AUTO
                "baseline" -> TextParagraph.FontAlign.BASELINE
                "center" -> TextParagraph.FontAlign.CENTER
                "top" -> TextParagraph.FontAlign.TOP
                "bottom" -> TextParagraph.FontAlign.BOTTOM
                else -> TextParagraph.FontAlign.AUTO
            }
    }

    /**
     * Sets whether the current paragraph has a bullet.
     *
     * @param bullet Whether to show a bullet
     */
    fun setBullet(bullet: Boolean) {
        currentTextParagraph?.isBullet = bullet
    }

    /**
     * Sets the bullet font for the current paragraph.
     *
     * @param fontName The font name
     */
    fun setBulletFont(fontName: String) {
        currentTextParagraph?.bulletFont = fontName
    }

    /**
     * Sets the bullet color for the current paragraph.
     *
     * @param color The color in hex format (e.g., "#FF0000")
     */
    fun setBulletColor(color: String) {
        currentTextParagraph?.bulletFontColor = DrawPaint.createSolidPaint(java.awt.Color.decode(color))
    }

    /**
     * Sets the bullet size for the current paragraph.
     *
     * @param size The size
     */
    fun setBulletSize(size: Double) {
        currentTextParagraph?.bulletFontSize = size
    }

    /**
     * Sets the bullet character for the current paragraph.
     *
     * @param bulletChar The bullet character
     */
    fun setBulletCharacter(bulletChar: String) {
        currentTextParagraph?.bulletCharacter = bulletChar
    }

    /**
     * Sets bullet numbering for the current paragraph.
     *
     * @param start The starting number
     */
    fun setBulletNumbering(start: Int) {
        currentTextParagraph?.setBulletAutoNumber(AutoNumberingScheme.arabicPeriod, start)
    }

    /**
     * Sets the left margin for the current paragraph.
     *
     * @param leftMargin The left margin
     */
    fun setLeftMargin(leftMargin: Double) {
        currentTextParagraph?.leftMargin = leftMargin
    }

    /**
     * Sets the right margin for the current paragraph.
     *
     * @param rightMargin The right margin
     */
    fun setRightMargin(rightMargin: Double) {
        currentTextParagraph?.rightMargin = rightMargin
    }

    /**
     * Sets the line spacing for the current paragraph.
     *
     * @param lineSpacing The line spacing
     */
    fun setLineSpacing(lineSpacing: Double) {
        currentTextParagraph?.lineSpacing = lineSpacing
    }

    /**
     * Sets the spacing before the current paragraph.
     *
     * @param spacingBefore The spacing before
     */
    fun setSpacingBefore(spacingBefore: Double) {
        currentTextParagraph?.spaceBefore = spacingBefore
    }

    /**
     * Sets the spacing after the current paragraph.
     *
     * @param spacingAfter The spacing after
     */
    fun setSpacingAfter(spacingAfter: Double) {
        currentTextParagraph?.spaceAfter = spacingAfter
    }

    // Text formatting methods

    /**
     * Sets the font family for the current text run.
     *
     * @param fontFamily The font family
     */
    fun setFontFamily(fontFamily: String) {
        currentTextRun?.setFontFamily(fontFamily)
    }

    /**
     * Sets the font size for the current text run.
     *
     * @param fontSize The font size
     */
    fun setFontSize(fontSize: Double) {
        currentTextRun?.fontSize = fontSize
    }

    /**
     * Sets the font color for the current text run.
     *
     * @param color The color in hex format (e.g., "#FF0000")
     */
    fun setFontColor(color: String) {
        currentTextRun?.setFontColor(java.awt.Color.decode(color))
    }

    /**
     * Sets the font background color for the current text run.
     *
     * @param color The color in hex format (e.g., "#FF0000")
     */
    fun setFontBackgroundColor(color: String) {
        currentTextRun?.setHighlightColor(java.awt.Color.decode(color))
    }

    /**
     * Sets the character spacing for the current text run.
     *
     * @param spacing The character spacing
     */
    fun setCharacterSpacing(spacing: Double) {
        currentTextRun?.setCharacterSpacing(spacing)
    }

    /**
     * Sets whether the current text run has strikethrough.
     *
     * @param strikethrough Whether to apply strikethrough
     */
    fun setStrikethrough(strikethrough: Boolean) {
        currentTextRun?.isStrikethrough = strikethrough
    }

    /**
     * Sets whether the current text run is underlined.
     *
     * @param underline Whether to apply underline
     */
    fun setUnderline(underline: Boolean) {
        currentTextRun?.isUnderlined = underline
    }

    /**
     * Sets whether the current text run is superscript.
     *
     * @param superScript Whether to apply superscript
     */
    fun setSuperScript(superScript: Boolean) {
        currentTextRun?.isSuperscript = superScript
    }

    /**
     * Sets whether the current text run is subscript.
     *
     * @param subScript Whether to apply subscript
     */
    fun setSubScript(subScript: Boolean) {
        currentTextRun?.isSubscript = subScript
    }

    /**
     * Sets whether the current text run is bold.
     *
     * @param bold Whether to apply bold
     */
    fun setBold(bold: Boolean) {
        currentTextRun?.isBold = bold
    }

    /**
     * Sets whether the current text run is italic.
     *
     * @param italic Whether to apply italic
     */
    fun setItalic(italic: Boolean) {
        currentTextRun?.isItalic = italic
    }
}
