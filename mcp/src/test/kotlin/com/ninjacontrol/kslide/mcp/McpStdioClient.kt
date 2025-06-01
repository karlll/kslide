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
            ).build()

    val transport = StdioClientTransport(stdioParams)
    val client = McpClient.sync(transport).build()

    client.initialize()

    // List available tools
    val toolsList = client.listTools()
    println("Available Tools = $toolsList")

    // Example: Call a tool (replace with actual tool name and params as needed)
    val result =
        client.callTool(
            CallToolRequest("listTemplates", emptyMap()),
        )
    println("Tool Result: $result")

    client.closeGracefully()
}
