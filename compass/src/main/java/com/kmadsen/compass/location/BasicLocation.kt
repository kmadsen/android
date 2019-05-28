package com.kmadsen.compass.location

data class BasicLocation(
        val timeMillis: Long,
        val latitude: Double,
        val longitude: Double,
        val horizontalAccuracyMeters: Float?,
        val altitudeMeters: Double?,
        val bearingDegrees: Float?,
        val speedMetersPerSecond: Float?
)
