package com.ninjacontrol.kslide.util

import com.ninjacontrol.kslide.service.SlideShowService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MarkdownProcessorTest {
    private lateinit var slideShowService: SlideShowService
    private lateinit var markdownProcessor: MarkdownProcessor

    @BeforeEach
    fun setUp() {
        slideShowService = mockk(relaxed = true)
        markdownProcessor = MarkdownProcessor(slideShowService)
    }

    @Test
    fun `should process simple text without formatting`() {
        val placeholderId = 1
        val markdown = "Simple text"

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verifySequence {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.createParagraph(null)
            slideShowService.createFormattedTextRun("Simple text", false, false, false)
        }
    }

    @Test
    fun `should process bold text`() {
        val placeholderId = 1
        val markdown = "This is **bold** text"

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verifySequence {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.createParagraph(null)
            slideShowService.createFormattedTextRun("This is ", false, false, false)
            slideShowService.createFormattedTextRun("bold", true, false, false)
            slideShowService.createFormattedTextRun(" text", false, false, false)
        }
    }

    @Test
    fun `should process italic text`() {
        val placeholderId = 1
        val markdown = "This is *italic* text"

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verifySequence {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.createParagraph(null)
            slideShowService.createFormattedTextRun("This is ", false, false, false)
            slideShowService.createFormattedTextRun("italic", false, true, false)
            slideShowService.createFormattedTextRun(" text", false, false, false)
        }
    }

    @Test
    fun `should process inline code`() {
        val placeholderId = 1
        val markdown = "This is `code` text"

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verifySequence {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.createParagraph(null)
            slideShowService.createFormattedTextRun("This is ", false, false, false)
            slideShowService.createFormattedTextRun("code", false, false, true)
            slideShowService.createFormattedTextRun(" text", false, false, false)
        }
    }

    @Test
    fun `should process mixed formatting`() {
        val placeholderId = 1
        val markdown = "Text with **bold**, *italic*, and `code`"

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verify {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.createParagraph(null)
            slideShowService.createFormattedTextRun("Text with ", false, false, false)
            slideShowService.createFormattedTextRun("bold", true, false, false)
            slideShowService.createFormattedTextRun(", ", false, false, false)
            slideShowService.createFormattedTextRun("italic", false, true, false)
            slideShowService.createFormattedTextRun(", and ", false, false, false)
            slideShowService.createFormattedTextRun("code", false, false, true)
        }
    }

    @Test
    fun `should process bullet list`() {
        val placeholderId = 1
        val markdown = """
            - First bullet
            - Second bullet
        """.trimIndent()

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verify {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.addBullet(1, "")
            slideShowService.createFormattedTextRun("First bullet", false, false, false)
            slideShowService.addBullet(1, "")
            slideShowService.createFormattedTextRun("Second bullet", false, false, false)
        }
    }

    @Test
    fun `should process nested bullet list`() {
        val placeholderId = 1
        val markdown = """
            - First level
              - Second level
                - Third level
        """.trimIndent()

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verify {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.addBullet(1, "")
            slideShowService.createFormattedTextRun("First level", false, false, false)
            slideShowService.addBullet(2, "")
            slideShowService.createFormattedTextRun("Second level", false, false, false)
            slideShowService.addBullet(3, "")
            slideShowService.createFormattedTextRun("Third level", false, false, false)
        }
    }

    @Test
    fun `should process bullet with formatting`() {
        val placeholderId = 1
        val markdown = "- This is **bold** bullet with *italic* and `code`"

        markdownProcessor.processMarkdownToSlide(placeholderId, markdown)

        verify {
            slideShowService.setActiveTextBox(placeholderId)
            slideShowService.clearActiveTextBox()
            slideShowService.addBullet(1, "")
            slideShowService.createFormattedTextRun("This is ", false, false, false)
            slideShowService.createFormattedTextRun("bold", true, false, false)
            slideShowService.createFormattedTextRun(" bullet with ", false, false, false)
            slideShowService.createFormattedTextRun("italic", false, true, false)
            slideShowService.createFormattedTextRun(" and ", false, false, false)
            slideShowService.createFormattedTextRun("code", false, false, true)
        }
    }
}