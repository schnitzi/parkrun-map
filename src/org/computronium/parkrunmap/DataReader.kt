package org.computronium.parkrunmap

import java.io.BufferedReader
import java.io.FileReader
import java.util.*


fun readParkruns(config: Config): SortedMap<Station, List<Parkrun>> {

    val parkrunsByStation = mutableMapOf<Station, MutableList<Parkrun>>()
    val stationsByName = mutableMapOf<String, Station>()

    val csvReader = BufferedReader(FileReader(config.dataFile))

    csvReader.readLine()    // skip the header line

    var row :String?

    while (csvReader.readLine().also { row = it?.trim() } != null) {

        println("row = $row")

        val data: List<String> = row!!.split("\t")

        val bikeMinutes = data[6].toInt()

        if (bikeMinutes <= config.maxBikeMinutes) {
            val stationName = data[2]
            var station = stationsByName[stationName]
            if (station == null) {
                station = Station(stationName, data[3].toInt(), data[4].toInt(), data[5].toFloat())
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
    return parkrunsByStation.toSortedMap(compareBy { it.sortOrder })
}
