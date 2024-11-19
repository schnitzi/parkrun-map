package org.computronium.parkrunmap

import java.awt.Color
import java.awt.Font
import kotlin.system.exitProcess

data class Config(
    val parkrunsFile: String,
    val stationsFile: String,
    val backgroundImageFile: String,
    val outputFile: String,
    val maxBikeMinutes: Int,
    val drawGridLines: Boolean,
    val fontSize: Int,
    val indexX: Int,
    val indexY: Int
) {

    val defaultFont = "DejaVu Sans"
    val indexFont = Font(defaultFont, Font.PLAIN, fontSize)
    val indexTitleFont = Font(defaultFont, Font.BOLD, fontSize + 5)
    val indexTextColor: Color = Color.BLACK
    val dotFont = Font(defaultFont, Font.PLAIN, fontSize)
    val dotColor: Color = Color.MAGENTA
    val dotDiameter = fontSize + 6
    val dotTextColor: Color = Color.WHITE

    data class Builder(var parkrunsFile: String? = null,
                       var stationsFile: String? = null,
                       var backgroundImageFile: String? = null,
                       var outputFile: String? = null,
                       var maxBikeMinutes: Int = 20,
                       var drawGridLines: Boolean = false,
                       var fontSize: Int = 20,
                       var indexX: Int = 0,
                       var indexY: Int = 0) {

        fun from(args: Array<String>) = apply {

            var i = 0
            while (i < args.size) {
                when (args[i]) {
                    "-d", "--data" -> {
                        parkrunsFile = args[i + 1]
                        i += 2
                    }
                    "-s", "--stations" -> {
                        stationsFile = args[i + 1]
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
                    "-f", "--font-size" -> {
                        fontSize = args[i + 1].toInt()
                        i += 2
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

            if (outputFile == null || stationsFile == null || backgroundImageFile == null) {
                System.err.println("Required parameter missing")
                exitProcess(-1)
            }
            return this
        }

        fun build(): Config {
            return Config(parkrunsFile!!, stationsFile!!, backgroundImageFile!!, outputFile!!, maxBikeMinutes, drawGridLines, fontSize, indexX, indexY)
        }
    }
}