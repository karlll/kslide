package com.ninjacontrol.kslide.mcp

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.ServerParameters
import io.modelcontextprotocol.client.transport.StdioClientTransport
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest

fun main() {
    val stdioParams =
        ServerParameters
            .builder("java")
            .args(
                "-Dspring.ai.mcp.server.stdio=true",
                "-Dspring.main.web-application-type=none",
                "-Dlogging.pattern.console=",
                "-jar",
                "/Users/karl/Project/kslide/mcp/build/libs/kslide-mcp-0.0.1-SNAPSHOT.jar",
                "--templatePath=/Users/karl/Project/kslide/templates",
                "--outputPath=/Users/karl/Project/kslide/output",
            ).build()

    val transport = StdioClientTransport(stdioParams)
    val client = McpClient.sync(transport).build()

    client.initialize()

    val toolsList = client.listTools()
    println("Available Tools = $toolsList")

    val templates =
        client.callTool(
            CallToolRequest("listTemplates", emptyMap()),
        )
    println("List templates: $templates")
    val layouts =
        client.callTool(
            CallToolRequest("listLayouts", mapOf("template" to "Default")),
        )
    println("List layouts: $layouts")
    val createSlideshow =
        client.callTool(
            CallToolRequest("createSlideshow", mapOf("template" to "Default")),
        )
    println("Create slideshow: $createSlideshow")
    val slide =
        client.callTool(
            CallToolRequest(
                "createSlide",
                mapOf(
                    "slideshowId" to "foo",
                    "layoutIndex" to 0,
                    "content" to mapOf(0 to "Hello World", 1 to "This is a test slide"),
                ),
            ),
        )
    println("create slide: $slide")

    client.closeGracefully()
}
