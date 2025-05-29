package com.ninjacontrol.kslide.mcp.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Template
    @JsonCreator
    constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("filename") val filename: String,
        @JsonProperty("layouts-filename") val layoutsFilename: String,
    )

typealias Templates = List<Template>
