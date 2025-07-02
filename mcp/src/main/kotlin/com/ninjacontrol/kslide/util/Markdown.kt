package com.ninjacontrol.kslide.util

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet

/**
 * Legacy markdown parsing utility for HTML output.
 * 
 * For PowerPoint slide generation with formatting, use the MarkdownProcessor
 * class in the core module instead, which integrates with Apache POI.
 * 
 * @see com.ninjacontrol.kslide.util.MarkdownProcessor
 */
fun parseMarkdown(markdown: String): String {
    val options = MutableDataSet()

    // uncomment to set optional extensions
    // options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

    // uncomment to convert soft-breaks to hard breaks
    // options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
    val parser: Parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()

    // You can re-use parser and renderer instances
    val document: Node = parser.parse(markdown)
    val html = renderer.render(document)
    return html
}

/**
 * Example usage demonstrating markdown to HTML conversion.
 */
fun demonstrateMarkdown() {
    val markdown = "This is *Sparta* with **bold** text and `code`"
    val html = parseMarkdown(markdown)
    println("Markdown: $markdown")
    println("HTML: $html")
}
