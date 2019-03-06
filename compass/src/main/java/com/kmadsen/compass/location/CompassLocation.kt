package com.kmadsen.compass.location

data class CompassLocation(
        val timeMillis: Long,
        val latitude: Double,
        val longitude: Double,
        val altitudeMeters: Double?
)
