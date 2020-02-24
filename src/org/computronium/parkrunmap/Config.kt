package org.computronium.parkrunmap

import java.awt.Color
import java.awt.Font
import kotlin.system.exitProcess

data class Config(
    val dataFile: String,
    val backgroundImageFile: String,
    val outputFile: String,
    val maxBikeMinutes: Int,
    val drawGridLines: Boolean,
    val indexX: Int,
    val indexY: Int) {

    val indexFont = Font("Arial", Font.PLAIN, 20)
    val indexTitleFont = Font("Arial", Font.BOLD, 30)
    val indexTextColor: Color = Color.BLACK
    val dotFont = Font("Arial", Font.PLAIN, 20)
    val dotColor: Color = Color.MAGENTA
    val dotDiameter = 35
    val dotTextColor: Color = Color.WHITE

    data class Builder(var dataFile: String? = null,
                       var backgroundImageFile: String? = null,
                       var outputFile: String? = null,
                       var maxBikeMinutes: Int = 20,
                       var drawGridLines: Boolean = false,
                       var indexX: Int = 0,
                       var indexY: Int = 0) {

        fun from(args: Array<String>) = apply {

            var i = 0
            while (i < args.size) {
                when (args[i]) {
                    "-d", "--data" -> {
                        dataFile = args[i + 1]
                        i += 2
                    }
                    "-b", "--background" -> {
                        backgroundImageFile = args[i + 1]
                        i += 2
                    }
                    "-o", "--output" -> {
                        outputFile = args[i + 1]
                        i += 2
                    }
                    "-m", "--max-minutes" -> {
                        maxBikeMinutes = args[i + 1].toInt()
                        i += 2
                    }
                    "-g", "--grid-lines" -> {
                        drawGridLines = true
                        i += 1
                    }
                    "-x", "--index-x" -> {
                        indexX = args[i + 1].toInt()
                        i += 2
                    }
                    "-y", "--index-y" -> {
                        indexY = args[i + 1].toInt()
                        i += 2
                    }
                    else -> {
                        System.err.println("Unknown param: " + args[i])
                        exitProcess(-1)
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
            return Config(dataFile!!, backgroundImageFile!!, outputFile!!, maxBikeMinutes, drawGridLines, indexX, indexY)
        }
    }
}