package org.computronium.parkrunmap

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun main(args: Array<String>) {

    val config = Config.Builder().from(args).build()

    val background = ImageIO.read(File(config.backgroundImageFile))
    val g = background.createGraphics()

    if (config.drawGridLines) {
        drawGridLines(g, background)
    }

    val parkruns = readParkruns(config)

    drawStations(config, parkruns.keys, g)

    drawIndex(config, parkruns, g)

    g.dispose()

    val outputFileExtension = config.outputFile.substring(config.outputFile.lastIndexOf('.')+1)
    ImageIO.write(background, outputFileExtension, File(config.outputFile))
}

private fun drawStations(config: Config, stations: Set<Station>, g: Graphics2D) {

    g.font = config.dotFont
    for ((index, station) in stations.withIndex()) {
        drawDot(config, station.mapX!!, station.mapY!!, (index+1).toString(), g)
    }
}

private fun drawDot(config: Config, x: Int, y: Int, label: String, g: Graphics2D) {
    g.color = config.dotColor
    g.fillOval(x - config.dotDiameter/2, y - config.dotDiameter/2, config.dotDiameter, config.dotDiameter)
    g.color = config.dotTextColor
    val fm = g.fontMetrics
    g.drawString(label, x - fm.stringWidth(label)/2 + 1, y + fm.height/2 - 3)
}

private fun drawIndex(config: Config, parkruns: Map<Station, List<Parkrun>>, g: Graphics2D) {

    g.color = config.indexTextColor
    g.font = config.indexTitleFont

    val x = config.indexX
    var y = config.indexY

    g.drawString("Station to parkrun", x, y)
    y += 30
    g.drawString("in bike minutes", x, y)
    y += 30
    g.font = config.indexFont

    val fm = g.fontMetrics
    for ((index, station) in parkruns.keys.withIndex()) {

        drawDot(config, x+30, y, (index+1).toString(), g)

        g.color = config.indexTextColor
        for (parkrun in parkruns[station] ?: error("whaaa")) {
            g.drawString(parkrun.name + " " + parkrun.bikeMinutes, x + 60, y + fm.height/2 - 3)
            y += fm.height + 5
        }

        y += 15
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

data class Station(val name: String, var mapX: Int?, var mapY: Int?, val sortOrder: Float?)

data class Parkrun(val name: String, val location: String, val station: Station, val bikeMinutes: Int)
