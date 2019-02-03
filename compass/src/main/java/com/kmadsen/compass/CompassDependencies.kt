package com.kmadsen.compass

import android.content.Context
import android.hardware.SensorManager
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.sensors.PositionSensors

class CompassDependencies(
        val compassMainActivity: CompassMainActivity
) {

    val locationPermissions: LocationPermissions by lazy {
        LocationPermissions()
    }

    val locationsController: LocationsController by lazy {
        LocationsController(
                locationPermissions,
                FusedLocationService(compassMainActivity.application)
        )
    }

    val sensorManager: SensorManager = compassMainActivity
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val positionSensors: PositionSensors = PositionSensors(sensorManager)
}
