@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.ninjacontrol.kslide.util

import com.ninjacontrol.kslide.service.SlideShowService
import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import com.vladsch.flexmark.util.data.MutableDataSet

/**
 * Processes markdown content and applies appropriate formatting to PowerPoint slides
 * using Apache POI through the SlideShowService.
 */
class MarkdownProcessor(private val slideShowService: SlideShowService) {
    private val options = MutableDataSet()
    private val parser = Parser.builder(options).build()
    private var currentBulletLevel = 0

    /**
     * Processes markdown content and adds it to the specified text box placeholder.
     *
     * @param placeholderId The ID of the text box placeholder
     * @param markdownContent The markdown content to process
     */
    fun processMarkdownToSlide(
        placeholderId: Int,
        markdownContent: String,
    ) {
        slideShowService.setActiveTextBox(placeholderId)
        slideShowService.clearActiveTextBox()

        val document = parser.parse(markdownContent)
        val visitor = SlideContentVisitor()
        visitor.visit(document)
    }

    private inner class SlideContentVisitor {
        private var isInBulletItem = false
        private var pendingFormats = mutableSetOf<TextFormat>()
        
        private val visitor = NodeVisitor(
            VisitHandler(Paragraph::class.java, this::visitParagraph),
            VisitHandler(BulletList::class.java, this::visitBulletList),
            VisitHandler(BulletListItem::class.java, this::visitBulletListItem),
            VisitHandler(Emphasis::class.java, this::visitEmphasis),
            VisitHandler(StrongEmphasis::class.java, this::visitStrongEmphasis),
            VisitHandler(Code::class.java, this::visitInlineCode),
            VisitHandler(Text::class.java, this::visitText)
        )
        
        fun visit(node: Node) {
            visitor.visit(node)
        }

        fun visitParagraph(paragraph: Paragraph) {
            if (!isInBulletItem) {
                slideShowService.createParagraph(null)
            }
            visitor.visitChildren(paragraph)
        }

        fun visitBulletList(bulletList: BulletList) {
            visitor.visitChildren(bulletList)
        }

        fun visitBulletListItem(listItem: BulletListItem) {
            currentBulletLevel = calculateBulletLevel(listItem)
            isInBulletItem = true

            // Create bullet with empty text first
            slideShowService.addBullet(currentBulletLevel, "")

            // Process the content of the bullet item
            visitor.visitChildren(listItem)

            isInBulletItem = false
        }

        fun visitEmphasis(emphasis: Emphasis) {
            pendingFormats.add(TextFormat.ITALIC)
            visitor.visitChildren(emphasis)
            pendingFormats.remove(TextFormat.ITALIC)
        }

        fun visitStrongEmphasis(strongEmphasis: StrongEmphasis) {
            pendingFormats.add(TextFormat.BOLD)
            visitor.visitChildren(strongEmphasis)
            pendingFormats.remove(TextFormat.BOLD)
        }

        fun visitInlineCode(code: Code) {
            pendingFormats.add(TextFormat.CODE)
            visitor.visitChildren(code)
            pendingFormats.remove(TextFormat.CODE)
        }

        fun visitText(text: Text) {
            val textContent = text.chars.toString()
            if (textContent.isNotBlank()) {
                slideShowService.createFormattedTextRun(
                    textContent,
                    bold = pendingFormats.contains(TextFormat.BOLD),
                    italic = pendingFormats.contains(TextFormat.ITALIC),
                    code = pendingFormats.contains(TextFormat.CODE),
                )
            }
        }
    }

    /**
     * Calculates the bullet level based on the nesting depth of the list item.
     */
    private fun calculateBulletLevel(listItem: BulletListItem): Int {
        var level = 0
        var parent = listItem.parent
        while (parent != null) {
            if (parent is BulletList) {
                level++
            }
            parent = parent.parent
        }
        return level.coerceIn(0, 9) // POI supports up to 9 bullet levels
    }

    /**
     * Enum representing different text formatting options.
     */
    private enum class TextFormat {
        BOLD,
        ITALIC,
        CODE,
    }
}