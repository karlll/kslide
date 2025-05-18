package com.ninjacontrol.kslide.output

import java.io.File

class FileWriter(
    private val outputFilename: String,
) : Output {
    private var outputFile: File? = null

    override fun out(message: String) {
        if (outputFile == null) {
            outputFile = File(outputFilename)
            if (!outputFile!!.exists()) {
                outputFile!!.createNewFile()
            }
        }
        outputFile!!.appendText(message + "\n")
    }
}
