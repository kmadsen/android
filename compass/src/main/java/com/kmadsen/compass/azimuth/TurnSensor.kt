package com.kmadsen.compass.azimuth

import android.hardware.Sensor
import android.hardware.SensorEvent
import com.kmadsen.compass.sensors.AndroidSensors
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlin.math.PI


class TurnSensor(
    private val androidSensors: AndroidSensors
) {

    var turnDegrees = 0.0
    var lastTurnSample = 0.0
    var lastTimeNanos = 0L

    fun observeTurn(): Observable<Double> {
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
                // to normalized degrees
                (it * 180.0 / PI + 360.0) % 360.0
            }
            .toObservable()
    }
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
