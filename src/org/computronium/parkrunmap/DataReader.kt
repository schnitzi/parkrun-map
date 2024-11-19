package org.computronium.parkrunmap

import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import kotlin.system.exitProcess


fun readStations(config: Config): Map<String, Station> {

    val stationsByName = mutableMapOf<String, Station>()

    val csvReader = BufferedReader(FileReader(config.stationsFile))

    csvReader.readLine()    // skip the header line

    var row :String?

    while (csvReader.readLine().also { row = it?.trim() } != null) {

        println("station row = $row")

        val data: List<String> = row!!.split("\t")

        val stationName = data[0]

        stationsByName[stationName] = Station(stationName, data[1].toInt(), data[2].toInt())
    }
    return stationsByName
}

fun readParkruns(config: Config, stations: Map<String, Station>): SortedMap<Station, List<Parkrun>> {

    val parkrunsByStation = mutableMapOf<Station, MutableList<Parkrun>>()

    val csvReader = BufferedReader(FileReader(config.parkrunsFile))

    csvReader.readLine()    // skip the header line

    var row :String?

    val abbreviations = mutableSetOf<String>()

    while (csvReader.readLine().also { row = it?.trim() } != null) {

        println("row = $row")

        val data: List<String> = row!!.split("\t")

        val abbreviation = data[4]
        if (abbreviations.contains(abbreviation)) {
            println("Abbreviation already used: $abbreviation")
            exitProcess(-1)
        }
        abbreviations += abbreviation

        val bikeMinutes = data[3].toInt()

        if (bikeMinutes <= config.maxBikeMinutes) {
            val stationName = data[2]
            val station = stations[stationName] ?: throw IllegalArgumentException("No station with name '${stationName}' found.")

            var parkrunsAtStation = parkrunsByStation[station]
            if (parkrunsAtStation == null) {
                parkrunsAtStation = mutableListOf()
                parkrunsByStation[station] = parkrunsAtStation
            }

            parkrunsAtStation.add(Parkrun(data[0], data[1], station, bikeMinutes, data[4]))
        }
    }
    return parkrunsByStation.toSortedMap(compareBy { it.name })
}
