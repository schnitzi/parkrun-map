package org.computronium.parkrunmap

import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO


fun main(args: Array<String>) {

    val config = Config.Builder().from(args).build()

    val background = ImageIO.read(File(config.backgroundImageFile))
    val g = background.createGraphics()
//    g.scale(0.25, 0.25)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    if (config.drawGridLines) {
        drawGridLines(g, background)
    }

    val stations = readStations(config)

    val parkrunsLookup = readParkruns(config, stations)
    drawParkruns(config, parkrunsLookup, g)

    val parkruns = parkrunsLookup.values.flatten().sortedBy { it.abbreviation }
    drawIndex(config, parkruns, g)

    g.dispose()

    val outputFileExtension = config.outputFile.substring(config.outputFile.lastIndexOf('.')+1)
    ImageIO.write(background, outputFileExtension, File(config.outputFile))
}

private fun drawParkruns(config: Config, parkrunsLookup: SortedMap<Station, List<Parkrun>>, g: Graphics2D) {

    g.font = config.dotFont
    for ((station, parkruns) in parkrunsLookup.entries) {
        var x = station.mapX
        for (parkrun in parkruns) {
            drawDot(config, x, station.mapY, parkrun, g)
            x += config.fontSize + 5
        }
    }
}

fun makeColorMoreTransparent(color: Color, b: Int): Color {
    require(b in 0..30) { "b must be in the range 0 to 30" }

    // Calculate the new alpha value: 255 means fully opaque, lower values mean more transparent.
    val alpha = (255 - (b / 30.0 * 255)).coerceIn(0.0, 255.0).toInt()

    return Color(color.red, color.green, color.blue, alpha)
}

private fun drawDot(config: Config, x: Int, y: Int, parkrun: Parkrun, g: Graphics2D) {
    g.color = Color.BLACK
    g.fillOval(x - config.dotDiameter/2 + 1, y - config.dotDiameter/2 + 1, config.dotDiameter, config.dotDiameter)
    g.color = makeColorMoreTransparent(config.dotColor, parkrun.bikeMinutes)
    g.fillOval(x - config.dotDiameter/2, y - config.dotDiameter/2, config.dotDiameter, config.dotDiameter)
    g.color = config.dotTextColor

    val fm = g.fontMetrics
    g.drawString(parkrun.abbreviation, x - fm.stringWidth(parkrun.abbreviation)/2, y + fm.height/2 - 2)
}

private fun drawIndex(config: Config, parkruns: List<Parkrun>, g: Graphics2D) {

    g.color = config.indexTextColor
    g.font = config.indexTitleFont

    var x = config.indexX
    var y = config.indexY

    val titleLineHeight = config.indexTitleFont.size + 10
    g.drawString("Station to parkrun", x + 30, y)
    y += titleLineHeight
    g.drawString("in bike minutes", x + 30, y)
    y += titleLineHeight
    g.font = config.indexFont

    val fm = g.fontMetrics
    var count = 0
    for (parkrun in parkruns) {

        drawDot(config, x+30, y, parkrun, g)

        g.color = config.indexTextColor
        g.drawString(parkrun.name + " " + parkrun.bikeMinutes, x + 45, y + fm.height/2 - 3)

        y += 19

        count += 1
        if (count > 35) {
            x += 170
            y = config.indexY + 2 * titleLineHeight
            count = 0
        }
    }
}

private fun drawGridLines(g: Graphics2D, img: BufferedImage) {
    g.color = Color.BLACK

    for (x in 0 until img.width step 100) {
        g.drawLine(x, 0, x, img.height)
        g.drawString("$x", x + 5, 30)
    }
    for (y in 0 until img.height step 100) {
        g.drawLine(0, y, img.width, y)
        g.drawString("$y", 20, y + 20)
    }
}

data class Station(val name: String, var mapX: Int, var mapY: Int)

data class Parkrun(val name: String, val location: String, val station: Station, val bikeMinutes: Int, val abbreviation: String)


object ListFonts {
    @JvmStatic
    fun main(args: Array<String>) {
        val fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
        for (fontName in fontNames) {
            println(fontName)
        }
    }
}