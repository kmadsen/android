package com.kmadsen.compass.sensors

import android.hardware.Sensor
import android.os.Build

class SensorConfig {

    private val supportedSensors: MutableSet<Int> by lazy {
        val supportedSensors = mutableSetOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_PRESSURE
        )
        supportedSensors.add(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
        supportedSensors.add(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
        if (Build.VERSION.SDK_INT >= 26) {
            supportedSensors.add(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
        }
        supportedSensors
    }

    fun supports(type: Int): Boolean = supportedSensors.contains(type)

}
