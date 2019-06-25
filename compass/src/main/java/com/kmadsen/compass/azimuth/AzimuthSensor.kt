package com.kmadsen.compass.azimuth

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.SystemClock
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.time.toMillisecondPeriod
import com.kylemadsen.core.FileLogger
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
    private val gyroscope = Measure3d()
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    fun observeAzimuth(context: Context): Observable<Azimuth> {
        return locationRepository.observeAzimuth()
            .startWith(Azimuth(0L, null))
            .mergeWith(attachSensorUpdates(context))
    }

    private fun attachSensorUpdates(context: Context): Completable {
        return Completable.mergeArray(
            attachAccelerometerUpdates(),
            attachMagnetometerUpdates(),
            attachGyroscopeUpdates(),
            attachAzimuthUpdates(context)
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

    private fun attachGyroscopeUpdates(): Completable {
        return androidSensors.observeRawSensor(Sensor.TYPE_GYROSCOPE)
            .doOnNext { gyroscope.lowPassFilter(it) }
            .ignoreElements()
    }

    private fun attachAzimuthUpdates(context: Context): Completable {
        return FileLogger(context)
            .observeWritableFile("azimuth_sensor")
            .flatMapCompletable { writableFile ->
                Observable.interval(0, toMillisecondPeriod(30), TimeUnit.MILLISECONDS)
                    .doOnSubscribe {
                        writableFile.writeLine("name=AzimuthSensor vendor=Kyle current_time_ms=${System.currentTimeMillis()}")
                        writableFile.writeLine("m_t m_x m_y m_z a_t a_x a_y a_z g_t g_x g_y g_z direction_degrees")
                    }
                    .map {
                        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer.values, magnetometer.values)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        val azimuth = Azimuth(
                            SystemClock.elapsedRealtime(),
                            orientation[0].toNormalizedDegrees()
                        )
                        val magnetometerNorm = magnetometer.normalized()
                        val accelerometerNorm = accelerometer.normalized()
                        val gyroscopeNorm = gyroscope.normalized()
                        val sensorLine = "${azimuth.recordedAtMilliseconds}" +
                            " ${magnetometer.measuredAtNanos}" +
                            " ${magnetometerNorm[0]}" +
                            " ${magnetometerNorm[1]}" +
                            " ${magnetometerNorm[2]}" +
                            " ${accelerometer.measuredAtNanos}" +
                            " ${accelerometerNorm[0]}" +
                            " ${accelerometerNorm[1]}" +
                            " ${accelerometerNorm[2]}" +
                            " ${gyroscope.measuredAtNanos}" +
                            " ${gyroscopeNorm[0]}" +
                            " ${gyroscopeNorm[1]}" +
                            " ${gyroscopeNorm[2]}" +
                            " ${azimuth.deviceDirectionDegrees}"
                        writableFile.writeLine(sensorLine)

                        return@map azimuth
                    }
                    .doOnNext { locationRepository.updateAzimuth(it) }
                    .ignoreElements()
            }
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
