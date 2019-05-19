package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.SystemClock
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.Measure3d
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import kotlin.math.PI

class AzimuthSensor(
        private val androidSensors: AndroidSensors
) {

    private val accelerometer = Measure3d()
    private val magnetometer = Measure3d()
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun attachSensorUpdates(): Completable {
        return Completable.mergeArray(
                attachAccelerometerUpdates(),
                attachMagnetometerUpdates()
        )
    }

    fun observeAzimuth(): Observable<Azimuth> {
        return Observable.interval(0, 10L, TimeUnit.MILLISECONDS)
                .map {
                    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer.values, magnetometer.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val fromNorthRadians = (orientation[0] - PI)
                    val deviceDirectionRadians = (PI - orientation[0].toDouble())

                    Azimuth(fromNorthRadians, deviceDirectionRadians)
                }
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
}

//fun Double.mRound(factor: Double): Double {
//    return Math.round(this / factor) * factor
//}

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

private fun lowPassFilter(currentValue: Float, nextValue: Float, alpha: Float): Float {
    return currentValue + alpha * (nextValue - currentValue)
}
