package com.kmadsen.compass.location.sensor

import com.kmadsen.compass.location.BasicLocation

data class SensorLocation(
    val basicLocation: BasicLocation?,
    val staleSeconds: Double
)
