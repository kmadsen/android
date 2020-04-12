package com.kmadsen.compass.sensors.rx

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.SystemClock
import com.kmadsen.compass.sensors.data.Measure1d
import com.kmadsen.compass.sensors.data.Measure3d
import com.kmadsen.compass.location.LocationRepository
import com.kylemadsen.core.time.toMillisecondPeriod
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.min

class RxAzimuthSensor(
    private val androidSensors: RxAndroidSensors,
    private val locationRepository: LocationRepository
) {

    private val accelerometer = Measure3d()
    private val magnetometer = Measure3d()
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun observeAzimuth(): Observable<Measure1d> {
        return locationRepository.observeAzimuth()
            .mergeWith(attachSensorUpdates())
    }

    private fun attachSensorUpdates(): Completable {
        return Completable.mergeArray(
            attachAccelerometerUpdates(),
            attachMagnetometerUpdates(),
            attachAzimuthUpdates()
        )
    }

    private fun attachAccelerometerUpdates(): Completable {
        return androidSensors.observeRawSensor(Sensor.TYPE_ACCELEROMETER)
                .doOnNext { accelerometer.lowPassFilter(it) }
                .ignoreElements()
    }

    private fun attachMagnetometerUpdates(): Completable {
        return androidSensors.observeRawSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
                .doOnNext { magnetometer.lowPassFilter(it) }
                .ignoreElements()
    }

    private fun attachAzimuthUpdates(): Completable {
        return Observable.interval(0, toMillisecondPeriod(30), TimeUnit.MILLISECONDS)
            .map {
                val isValid = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer.values, magnetometer.values)
                val deviceDirectionDegrees = if (isValid) {
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    orientation[0].toNormalizedDegrees()
                } else null
                Measure1d(
                    SystemClock.elapsedRealtime(),
                    deviceDirectionDegrees?.toFloat()
                )
            }
            .doOnNext { locationRepository.updateAzimuth(it) }
            .ignoreElements()
    }
}

fun Float.toNormalizedDegrees(): Double {
    return (this * 180.0 / PI + 360.0) % 360.0
}

fun Measure3d.lowPassFilter(nextEstimate: SensorEvent): Measure3d {
    val nanosEstimateDelta = (nextEstimate.timestamp - measuredAtNanos)
    val delayEstimateNanos = TimeUnit.MILLISECONDS.toNanos(500).toDouble()
    val alpha = min(0.9, (nanosEstimateDelta / delayEstimateNanos)).toFloat()
    x = lowPassFilter(
        x,
        nextEstimate.values[0],
        alpha
    )
    y = lowPassFilter(
        y,
        nextEstimate.values[1],
        alpha
    )
    z = lowPassFilter(
        z,
        nextEstimate.values[2],
        alpha
    )
    measuredAtNanos = nextEstimate.timestamp
    recordedAtNanos = SystemClock.elapsedRealtimeNanos()
    accuracy = nextEstimate.accuracy
    return this
}

fun lowPassFilter(currentValue: Float, nextValue: Float, alpha: Float): Float {
    return currentValue + alpha * (nextValue - currentValue)
}
