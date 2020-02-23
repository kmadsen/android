package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorManager
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.rx.RxAndroidSensors
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class AltitudeSensor(
    val rxAndroidSensors: RxAndroidSensors,
    val locationRepository: LocationRepository
) {

    fun observeAltitude(): Observable<Measure1d> {
        return rxAndroidSensors.observeRawSensor(Sensor.TYPE_PRESSURE)
            .map {
                val altitudeMeters = getAltitude(it.values[0])
                Measure1d(TimeUnit.NANOSECONDS.toMillis(it.timestamp), altitudeMeters)
            }.toObservable()
            .doAfterNext {
                locationRepository.updateAltitudeMeters(it)
            }
    }

    private fun getAltitude(pressure: Float): Float {
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)
    }
}
