package com.ninjacontrol.kslide.output

class Writer : Output {
    private val writers = mutableListOf<Output>()

    fun addWriter(writer: Output) {
        writers.add(writer)
    }

    override fun out(message: String) {
        writers.forEach { it.out(message) }
    }
}
