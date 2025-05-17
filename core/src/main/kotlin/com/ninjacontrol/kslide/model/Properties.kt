package com.ninjacontrol.kslide.model

enum class Property(
    val key: String,
    val description: String,
) {
    DEBUG_MODE("debug.mode", "Set debug mode"),
    DEBUG_FLAG_PLACEHOLDER_FRAME("debug.flags.placeholder.frame", "Visual: Add border to placeholders"),
    DEBUG_FLAG_PLACEHOLDER_ID("debug.flags.placeholder.id", "Visual: Show placeholder IDs"),
}

fun Property.fromString(key: String): Property? = Property.entries.find { it.key == key }

enum class DebugMode(
    val key: String,
    val description: String,
) {
    OFF("off", "Debug mode is off"),
    ON("on", "Debug mode is on"),
}
