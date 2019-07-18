package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorManager
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class AltitudeSensor(
    val androidSensors: AndroidSensors,
    val locationRepository: LocationRepository
) {

    fun observeAltitude(): Observable<Measure1d> {
        return androidSensors.observeRawSensor(Sensor.TYPE_PRESSURE)
            .map {
                val altitudeMeters = SensorManager
                    .getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, it.values[0])
                Measure1d(TimeUnit.NANOSECONDS.toMillis(it.timestamp), altitudeMeters)
            }.toObservable()
            .doAfterNext {
                locationRepository.updateAltitudeMeters(it)
            }
    }
}
