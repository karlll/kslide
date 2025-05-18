package com.ninjacontrol.kslide.output

class StdoutWriter : Output {
    override fun out(message: String) {
        println(message)
    }
}
