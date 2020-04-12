package com.kmadsen.compass.sensors.config

/**
 * Primitive types only.
 * This class is saved to preferences.
 */
data class SensorConfigPreference(
    val sensorType: Int,
    var signalsPerSecond: Int
)
