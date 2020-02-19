package org.computronium.parkrunmap

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.imageio.ImageIO


const val DOT_DIAMETER = 35

fun main() {

    val img = ImageIO.read(File("resources/source.png"))
    val g = img.createGraphics()

    //drawGridLines(g, img)

    val parkruns = readParkruns()

    drawStations(parkruns.keys, g)

    drawIndex(parkruns, g)

    g.dispose()

    ImageIO.write(img, "png", File("GreaterAngliaParkruns.png"))
}

private fun readParkruns() : Map<Station, List<Parkrun>> {

    val parkrunsByStation = mutableMapOf<Station, MutableList<Parkrun>>()
    val stationsByName = mutableMapOf<String, Station>()

    val csvReader = BufferedReader(FileReader("resources/parkruns.csv"))

    csvReader.readLine()    // skip the header line

    var row :String?

    while (csvReader.readLine().also { row = it?.trim() } != null) {

        println("row = $row")

        val data: List<String> = row!!.split("\t")

        val bikeMinutes = data[6].toInt()

        if (bikeMinutes <= 20) {
            val stationName = data[2]
            var station = stationsByName[stationName]
            if (station == null) {
                station = Station(stationName, data[3].toInt(), data[4].toInt(), data[5].toInt())
                stationsByName[stationName] = station
            }

            var parkrunsAtStation = parkrunsByStation[station]
            if (parkrunsAtStation == null) {
                parkrunsAtStation = mutableListOf()
                parkrunsByStation[station] = parkrunsAtStation
            }

            parkrunsAtStation.add(Parkrun(data[0], data[1], station, data[6].toInt()))
        }
    }
    return parkrunsByStation
}

private fun drawStations(stations: Set<Station>, g: Graphics2D) {

    g.font = Font("Arial", Font.PLAIN, 20)
    val fm = g.fontMetrics
    for (station in stations) {
        g.color = Color.MAGENTA
        g.fillOval(station.mapX!! - DOT_DIAMETER/2, station.mapY!! - DOT_DIAMETER/2, DOT_DIAMETER, DOT_DIAMETER)
        g.color = Color.WHITE
        val label = station.mapLabel.toString()
        g.drawString(label, station.mapX!! - fm.stringWidth(label)/2 + 1, station.mapY!! + fm.height/2 - 3)
    }
}

private fun drawIndex(parkruns: Map<Station, List<Parkrun>>, g: Graphics2D) {

    val x = 1700
    var y = 40

    g.color = Color.BLACK

    g.font = Font("Arial", Font.BOLD, 30)

    g.drawString("Station to parkrun", x-30, y)
    y += 30
    g.drawString("in bike minutes", x-30, y)
    y += 30
    g.font = Font("Arial", Font.PLAIN, 20)

    val fm = g.fontMetrics
    for (station in parkruns.keys.sortedBy { it.mapLabel }) {
        g.color = Color.MAGENTA
        g.fillOval(x - DOT_DIAMETER/2, y - DOT_DIAMETER/2, DOT_DIAMETER, DOT_DIAMETER)
        g.color = Color.WHITE
        val label = station.mapLabel.toString()
        g.drawString(label, x - fm.stringWidth(label)/2 + 1, y + fm.height/2 - 3)

        g.color = Color.BLACK

        for (parkrun in parkruns[station] ?: error("whaaa")) {
            g.drawString(parkrun.name + " " + parkrun.bikeMinutes, x + 30, y + fm.height/2 - 3)
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

private data class Station(val name: String, var mapX: Int?, var mapY: Int?, val mapLabel: Int?)

private data class Parkrun(val name: String, val location: String, val station: Station, val bikeMinutes: Int)
