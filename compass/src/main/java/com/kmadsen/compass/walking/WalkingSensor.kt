package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.SystemClock
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.Measure3d
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.PI

class WalkingSensor(
        private val androidSensors: AndroidSensors,
        private val locationRepository: LocationRepository
) {

    private val accelerometer = Measure3d()
    private val magnetometer = Measure3d()
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun observeWalkingState(): Observable<WalkingState> {
        return locationRepository.observeAzimuth().mergeWith(attachSensorUpdates())
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
        return androidSensors.observeRawSensor(Sensor.TYPE_MAGNETIC_FIELD)
                .doOnNext { magnetometer.lowPassFilter(it) }
                .ignoreElements()
    }

    private fun attachAzimuthUpdates(): Completable {
        return Observable.interval(0, toMillisecondPeriod(30), TimeUnit.MILLISECONDS)
                .map {
                    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer.values, magnetometer.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val northDirectionRadians = (2.0 * PI - orientation[0]) % (2.0 * PI)
                    val deviceDirectionRadians = (orientation[0] + 2.0 * PI) % (2.0 * PI)
                    Azimuth(
                            SystemClock.elapsedRealtime(),
                            northDirectionRadians,
                            deviceDirectionRadians
                    )
                }
                .doOnNext { locationRepository.updateAzimuth(it) }
                .ignoreElements()
    }
}

fun toMillisecondPeriod(framesPerSecond: Long): Long = TimeUnit.SECONDS.toMillis(1) / framesPerSecond

fun Measure3d.lowPassFilter(nextEstimate: SensorEvent): Measure3d {
    val nanosEstimateDelta = (nextEstimate.timestamp - measuredAtNanos)
    val delayEstimateNanos = TimeUnit.MILLISECONDS.toNanos(500).toDouble()
    val alpha = Math.min(0.9, (nanosEstimateDelta / delayEstimateNanos)).toFloat()
    x = lowPassFilter(x, nextEstimate.values[0], alpha)
    y = lowPassFilter(y, nextEstimate.values[1], alpha)
    z = lowPassFilter(z, nextEstimate.values[2], alpha)
    measuredAtNanos = nextEstimate.timestamp
    accuracy = nextEstimate.accuracy
    return this
}

private fun lowPassFilter(currentValue: Float, nextValue: Float, alpha: Float): Float {
    return currentValue + alpha * (nextValue - currentValue)
}
