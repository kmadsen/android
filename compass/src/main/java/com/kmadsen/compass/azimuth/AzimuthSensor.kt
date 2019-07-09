package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.SystemClock
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.time.toMillisecondPeriod
import com.kylemadsen.core.logger.L
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.min

class AzimuthSensor(
        private val androidSensors: AndroidSensors,
        private val locationRepository: LocationRepository
) {

    private val accelerometer = Measure3d()
    private val magnetometer = Measure3d()
    private val turnCalculator = TurnSensor(androidSensors)
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
//            .doOnNext { turnDegrees ->
//                val azimuth = Azimuth(
//                    SystemClock.elapsedRealtime(),
//                    turnDegrees
//                )
//                locationRepository.updateAzimuth(azimuth)
//            }
            .ignoreElements()
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
                val accelerometerVector3d = Vector3d(accelerometer.values)
                val accelerometerLength = accelerometerVector3d.length()
                L.i("azimuth acc %s", accelerometerVector3d.values.joinToString())
                L.i("azimuth acc length %s", accelerometerLength)
                L.i("azimuth acc norm %s", accelerometerVector3d.normalize().values.joinToString())
                val magnetometerVector3d = Vector3d(magnetometer.values)
                L.i("azimuth mag %s", magnetometerVector3d.values.joinToString())
                val magnetometerLength = magnetometerVector3d.length()
                L.i("azimuth mag length %s", magnetometerLength)
                L.i("azimuth mag norm %s", magnetometerVector3d.normalize().values.joinToString())
                SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer.values, magnetometer.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val deviceDirectionDegrees = orientation[0].toNormalizedDegrees()
//                val deviceDirectionDegrees = if (magnetometerLength < 50f && magnetometerLength > 40f) {
//                    orientation[0].toNormalizedDegrees()
//                } else null
                Azimuth(
                    SystemClock.elapsedRealtime(),
                    deviceDirectionDegrees
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
    val transX = (nextEstimate.values[0] + 40.4648245f)
    val transY = (nextEstimate.values[1] + 8.752111499999998f)
    val transZ = (nextEstimate.values[2] + 69.4366635f)

    x = lowPassFilter(x, transX, alpha)
    y = lowPassFilter(y, transY, alpha)
    z = lowPassFilter(z, transZ, alpha)
    measuredAtNanos = nextEstimate.timestamp
    recordedAtNanos = SystemClock.elapsedRealtimeNanos()
    accuracy = nextEstimate.accuracy
    return this
}

fun lowPassFilter(currentValue: Float, nextValue: Float, alpha: Float): Float {
    return currentValue + alpha * (nextValue - currentValue)
}
