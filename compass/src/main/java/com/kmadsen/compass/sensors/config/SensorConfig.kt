package com.kmadsen.compass.sensors.config

import android.hardware.Sensor

data class SensorConfig(
    val sensor: Sensor,
    val preference: SensorConfigPreference,
    val minEventsPerSecond: Int?,
    val maxEventsPerSecond: Int?
)
