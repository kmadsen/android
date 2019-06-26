package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.SystemClock
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.time.toMillisecondPeriod
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.PI

class AzimuthSensor(
        private val androidSensors: AndroidSensors,
        private val locationRepository: LocationRepository
) {

    private val accelerometer = Measure3d()
    private val magnetometer = Measure3d()
    private val turnCalculator = TurnCalculator(androidSensors)
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun observeAzimuth(): Observable<Azimuth> {
        return locationRepository.observeAzimuth()
            .startWith(Azimuth(0L, null))
            .mergeWith(attachSensorUpdates())
    }

    private fun attachSensorUpdates(): Completable {
        return Completable.mergeArray(
            attachAccelerometerUpdates(),
            attachMagnetometerUpdates(),
            attachTurnCalculator(),
            attachAzimuthUpdates()
        )
    }

    private fun attachTurnCalculator(): Completable {
        return turnCalculator.observeTurn()
            .doOnNext { turnDegrees ->
                val azimuth = Azimuth(
                    SystemClock.elapsedRealtime(),
                    turnDegrees
                )
                locationRepository.updateAzimuth(azimuth) }
            .ignoreElements()
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
                Azimuth(
                    SystemClock.elapsedRealtime(),
                    orientation[0].toNormalizedDegrees()
                )
            }
//                    .doOnNext { locationRepository.updateAzimuth(it) }
            .ignoreElements()
    }
}

fun Float.toNormalizedDegrees(): Double {
    return (this * 180.0 / PI + 360.0) % 360.0
}

fun Measure3d.lowPassFilter(nextEstimate: SensorEvent): Measure3d {
    val nanosEstimateDelta = (nextEstimate.timestamp - measuredAtNanos)
    val delayEstimateNanos = TimeUnit.MILLISECONDS.toNanos(500).toDouble()
    val alpha = Math.min(0.9, (nanosEstimateDelta / delayEstimateNanos)).toFloat()
    x = lowPassFilter(x, nextEstimate.values[0], alpha)
    y = lowPassFilter(y, nextEstimate.values[1], alpha)
    z = lowPassFilter(z, nextEstimate.values[2], alpha)
    measuredAtNanos = nextEstimate.timestamp
    recordedAtNanos = SystemClock.elapsedRealtimeNanos()
    accuracy = nextEstimate.accuracy
    return this
}

fun lowPassFilter(currentValue: Float, nextValue: Float, alpha: Float): Float {
    return currentValue + alpha * (nextValue - currentValue)
}
