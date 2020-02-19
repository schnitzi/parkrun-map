package org.computronium.parkrunmap

import kotlin.system.exitProcess

data class Config(val dataFile: String,
             val backgroundImageFile: String,
             val outputFile: String,
             val maxBikeMinutes: Int,
             val drawGridLines: Boolean) {


    data class Builder(var dataFile: String? = null,
                       var backgroundImageFile: String? = null,
                       var outputFile: String? = null,
                       var maxBikeMinutes: Int = 20,
                       var drawGridLines: Boolean = false) {

        fun from(args: Array<String>) = apply {

            var i = 0
            while (i < args.size) {
                when (args[i]) {
                    "-d" -> {
                        dataFile = args[i + 1]
                        i += 2
                    }
                    "-b" -> {
                        backgroundImageFile = args[i + 1]
                        i += 2
                    }
                    "-o" -> {
                        outputFile = args[i + 1]
                        i += 2
                    }
                    "-m" -> {
                        maxBikeMinutes = args[i + 1].toInt()
                        i += 2
                    }
                    "-g" -> {
                        drawGridLines = true
                    }
                }
            }

            if (outputFile == null || dataFile == null || backgroundImageFile == null) {
                System.err.println("Required parameter missing")
                exitProcess(-1)
            }
            return this
        }

        fun build(): Config {
            return Config(dataFile!!, backgroundImageFile!!, outputFile!!, maxBikeMinutes, drawGridLines)
        }
    }
}