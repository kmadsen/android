package com.kmadsen.compass.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

class PositionSensors(private val sensorManager: SensorManager) {
    fun observeRotationVector(): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val rotationVectorSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            val sensorListener = SensorListener(emitter)
            sensorManager.registerListener(sensorListener, rotationVectorSensor, 0)
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun observeSensor(sensor: Int): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val rotationVectorSensor: Sensor = sensorManager.getDefaultSensor(sensor)
            val sensorListener = SensorListener(emitter)
            sensorManager.registerListener(sensorListener, rotationVectorSensor, 0)
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    class SensorListener(
            private val emitter: FlowableEmitter<LoggedEvent>
    ) : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(sensorEvent: SensorEvent) {
            val recordedAtNanos: Long = SystemClock.elapsedRealtimeNanos()
            emitter.onNext(LoggedEvent(sensorEvent, recordedAtNanos))
        }
    }
}

data class LoggedEvent(
        val sensorEvent: SensorEvent,
        val recordedAtNanos: Long
)
