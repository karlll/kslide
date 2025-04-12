@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.model

import org.apache.poi.sl.draw.DrawPaint
import org.apache.poi.sl.usermodel.AutoNumberingScheme
import org.apache.poi.sl.usermodel.TextParagraph
import org.apache.poi.xslf.usermodel.*
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import java.time.Instant
import java.util.UUID

data class SlideShowMetadata(
    val id: UUID,
    val filename: String?,
    val title: String?,
    val createdAt: Instant,
    val author: String?,
)

data class SlideShowState(
    val metadata: SlideShowMetadata,
    val slides: SlideState,
) {
    val id: UUID get() = metadata.id
}

data class SlideState(
    val ppt: XMLSlideShow,
    val currentSlide: XSLFSlide?,
    val currentTextBox: XSLFTextBox?,
    val currentTextParagraph: XSLFTextParagraph?,
    val currentTextRun: XSLFTextRun?,
    val count: Int = 0,
)

/**
 * Creates a new slide pack.
 *
 * @return A new instance of SlidePackState.
 */
fun new() =
    SlideShowState(
        metadata =
            SlideShowMetadata(
                id = UUID.randomUUID(),
                filename = null,
                title = null,
                createdAt = Instant.now(),
                author = null,
            ),
        slides =
            SlideState(
                ppt = XMLSlideShow(),
                currentSlide = null,
                currentTextBox = null,
                currentTextParagraph = null,
                currentTextRun = null,
            ),
    )

fun init(
    id: UUID,
    filename: String?,
    title: String?,
    createdAt: Instant,
    author: String?,
    slideShowBytes: ByteArray,
) = SlideShowState(
    metadata =
        SlideShowMetadata(
            id,
            filename,
            title,
            createdAt,
            author,
        ),
    slides =
        SlideState(
            ppt = XMLSlideShow(ByteArrayInputStream(slideShowBytes)),
            currentSlide = null,
            currentTextBox = null,
            currentTextParagraph = null,
            currentTextRun = null,
        ),
)

fun newSlide(
    state: SlideShowState,
    title: String?,
) = state.copy(
    slides =
        state.slides.copy(
            currentSlide = state.slides.ppt.createSlide(),
            count = state.slides.count + 1,
        ),
)

fun removeSlide(
    state: SlideShowState,
    slideNumber: Int,
) = state.copy(
    slides =
        state.slides.copy(
            ppt = state.slides.ppt.apply { removeSlide(slideNumber) },
            currentSlide = if (state.slides.currentSlide?.slideNumber == slideNumber) null else state.slides.currentSlide,
        ),
)

fun setCurrentSlide(
    state: SlideShowState,
    slideNumber: Int,
) = state.copy(
    slides =
        state.slides.copy(
            currentSlide =
                state.slides.ppt.slides
                    .find { it.slideNumber == slideNumber },
        ),
)

fun newTextBox(
    state: SlideShowState,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) = state
    .copy(
        slides =
            state.slides.copy(
                currentTextBox = state.slides.currentSlide?.createTextBox(),
            ),
    ).also {
        it.slides.currentTextBox?.setAnchor(Rectangle(x, y, width, height))
    }

fun newTextParagraph(state: SlideShowState) =
    state.copy(
        slides =
            state.slides.copy(
                currentTextParagraph = state.slides.currentTextBox?.addNewTextParagraph(),
            ),
    )

fun newTextRun(
    state: SlideShowState,
    text: String,
) = state
    .copy(
        slides =
            state.slides.copy(
                currentTextRun = state.slides.currentTextParagraph?.addNewTextRun(),
            ),
    ).also {
        it.slides.currentTextRun?.setText(text)
    }

fun newHyperlink(
    state: SlideShowState,
    text: String,
    url: String,
) = state
    .copy(
        slides =
            state.slides.copy(
                currentTextRun = state.slides.currentTextParagraph?.addNewTextRun(),
            ),
    ).also {
        it.slides.currentTextRun?.setText(text)
        val link = it.slides.currentTextRun?.createHyperlink()
        link?.address = url
    }

// Paragraph layout
fun setIndent(
    state: SlideShowState,
    indent: Double,
) {
    state.slides.currentTextParagraph?.setIndent(indent)
}

fun setIndentLevel(
    state: SlideShowState,
    level: Int,
) {
    state.slides.currentTextParagraph?.indentLevel = level
}

fun addLinebreak(state: SlideShowState) {
    state.slides.currentTextParagraph?.addLineBreak()
}

fun setTextAlignment(
    state: SlideShowState,
    alignment: String,
) {
    state.slides.currentTextParagraph?.textAlign =
        when (alignment) {
            "left" -> TextParagraph.TextAlign.LEFT
            "center" -> TextParagraph.TextAlign.CENTER
            "right" -> TextParagraph.TextAlign.RIGHT
            "justify" -> TextParagraph.TextAlign.JUSTIFY
            "distribute" -> TextParagraph.TextAlign.DIST
            else -> TextParagraph.TextAlign.LEFT
        }
}

fun setFontAlignment(
    state: SlideShowState,
    alignment: String,
) {
    state.slides.currentTextParagraph?.fontAlign =
        when (alignment) {
            "auto" -> TextParagraph.FontAlign.AUTO
            "baseline" -> TextParagraph.FontAlign.BASELINE
            "center" -> TextParagraph.FontAlign.CENTER
            "top" -> TextParagraph.FontAlign.TOP
            "bottom" -> TextParagraph.FontAlign.BOTTOM
            else -> TextParagraph.FontAlign.AUTO
        }
}

fun setBullet(
    state: SlideShowState,
    bullet: Boolean,
) {
    state.slides.currentTextParagraph?.isBullet = bullet
}

fun setBulletFont(
    state: SlideShowState,
    fontName: String,
) {
    state.slides.currentTextParagraph?.bulletFont = fontName
}

fun setBulletColor(
    state: SlideShowState,
    color: String,
) {
    state.slides.currentTextParagraph?.bulletFontColor = DrawPaint.createSolidPaint(java.awt.Color.decode(color))
}

fun setBulletSize(
    state: SlideShowState,
    size: Double,
) {
    state.slides.currentTextParagraph?.bulletFontSize = size
}

fun setBulletCharacter(
    state: SlideShowState,
    bulletChar: String,
) {
    state.slides.currentTextParagraph?.bulletCharacter = bulletChar
}

fun setBulletNumbering(
    state: SlideShowState,
    start: Int,
) {
    state.slides.currentTextParagraph?.setBulletAutoNumber(AutoNumberingScheme.arabicPeriod, start)
}

fun setLeftMargin(
    state: SlideShowState,
    leftMargin: Double,
) {
    state.slides.currentTextParagraph?.leftMargin = leftMargin
}

fun setRightMargin(
    state: SlideShowState,
    rightMargin: Double,
) {
    state.slides.currentTextParagraph?.rightMargin = rightMargin
}

fun setLineSpacing(
    state: SlideShowState,
    lineSpacing: Double,
) {
    state.slides.currentTextParagraph?.lineSpacing = lineSpacing
}

fun setSpacingBefore(
    state: SlideShowState,
    spacingBefore: Double,
) {
    state.slides.currentTextParagraph?.spaceBefore = spacingBefore
}

fun setSpacingAfter(
    state: SlideShowState,
    spacingAfter: Double,
) {
    state.slides.currentTextParagraph?.spaceAfter = spacingAfter
}

// Text font, color, etc.
fun setFontFamily(
    state: SlideShowState,
    fontFamily: String,
) {
    state.slides.currentTextRun?.setFontFamily(fontFamily)
}

fun setFontSize(
    state: SlideShowState,
    fontSize: Double,
) {
    state.slides.currentTextRun?.fontSize = fontSize
}

fun setFontColor(
    state: SlideShowState,
    color: String,
) {
    state.slides.currentTextRun?.setFontColor(java.awt.Color.decode(color))
}

fun setFontBackgroundColor(
    state: SlideShowState,
    color: String,
) {
    state.slides.currentTextRun?.setHighlightColor(java.awt.Color.decode(color))
}

fun setCharacterSpacing(
    state: SlideShowState,
    spacing: Double,
) {
    state.slides.currentTextRun?.setCharacterSpacing(spacing)
}

fun setStrikethrough(
    state: SlideShowState,
    strikethrough: Boolean,
) {
    state.slides.currentTextRun?.isStrikethrough = strikethrough
}

fun setUnderline(
    state: SlideShowState,
    underline: Boolean,
) {
    state.slides.currentTextRun?.isUnderlined = underline
}

fun setSuperScript(
    state: SlideShowState,
    superScript: Boolean,
) {
    state.slides.currentTextRun?.isSuperscript = superScript
}

fun setSubScript(
    state: SlideShowState,
    subScript: Boolean,
) {
    state.slides.currentTextRun?.isSubscript = subScript
}

fun setBold(
    state: SlideShowState,
    bold: Boolean,
) {
    state.slides.currentTextRun?.isBold = bold
}

fun setItalic(
    state: SlideShowState,
    italic: Boolean,
) {
    state.slides.currentTextRun?.isItalic = italic
}
