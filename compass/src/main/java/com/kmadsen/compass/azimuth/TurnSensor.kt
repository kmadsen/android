package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorEvent
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kylemadsen.core.time.DeviceClock
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlin.math.PI


class TurnSensor(
    private val androidSensors: AndroidSensors,
    private val locationRepository: LocationRepository
) {
    fun observeTurn(startDegrees: Double = 0.0): Observable<Measure1d> {
        var turnDegrees = startDegrees.toNormalizedRadians()
        var lastTurnSample = 0.0
        var lastTimeNanos = 0L

        return androidSensors.observeRawSensor(Sensor.TYPE_GRAVITY)
            .withLatestFrom(androidSensors.observeRawSensor(Sensor.TYPE_GYROSCOPE),
                BiFunction { gravityEvent: SensorEvent, gyroEvent: SensorEvent ->
                    val gravity = Vector3d(
                        gravityEvent.values[0].toDouble(),
                        gravityEvent.values[1].toDouble(),
                        gravityEvent.values[2].toDouble())
                    val gyro = Vector3d(
                        gyroEvent.values[0].toDouble(),
                        gyroEvent.values[1].toDouble(),
                        gyroEvent.values[2].toDouble())
                    val rotated = rotateToGravityDown(gravity, gyro)
                    if (lastTimeNanos != 0L) {
                        val deltaNanos = gyroEvent.timestamp - lastTimeNanos
                        val deltaSeconds = deltaNanos / 1000000000.0
                        val integral = 0.5 * deltaSeconds * (rotated.z + lastTurnSample)
                        turnDegrees += integral
                    }
                    lastTimeNanos = gyroEvent.timestamp
                    lastTurnSample = rotated.z
                    turnDegrees
            })
            .map {
                val value = it.toNormalizedDegrees()
                Measure1d(DeviceClock.elapsedMillis(), value.toFloat())
            }
            .doOnNext {
                locationRepository.updateTurnDegrees(it)
            }
            .toObservable()
    }
}

fun Double.toNormalizedRadians(): Double {
    val twoPi = PI * 2.0
    return (this * PI / 180.0 + twoPi) % twoPi
}

fun Double.toNormalizedDegrees(): Double {
    return (this * 180.0 / PI + 360.0) % 360.0
}

private fun rotateToGravityDown(gravity: Vector3d, gyro: Vector3d): Vector3d {
    gravity.normalize()

    val q: Quaternion = if (gravity.x == 0.0 && gravity.y == 0.0 && gravity.z > 0.0) {
        Quaternion(1.0, 0.0, 0.0, 0.0)
    } else {
        Quaternion(-gravity.y, gravity.x, 0.0, 1.0 - gravity.z)
            .normalize()
    }

    val quaternionVector = q.vector3d()
    val t = cross(quaternionVector, gyro)
    t.multiply(2.0)
    gyro.add(cross(quaternionVector, t))
    t.multiply(q.w)

    gyro.add(t)

    return gyro
}
