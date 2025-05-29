package com.ninjacontrol.kslide.mcp.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Layout
    @JsonCreator
    constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("index") val index: Int,
    )

typealias Layouts = List<Layout>
