package com.kmadsen.compass.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.kylemadsen.core.logger.L
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

class AndroidSensors(private val sensorManager: SensorManager) {
    fun observeRotationVector(): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val rotationVectorSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            val sensorListener = SensorListener(emitter)
            sensorManager.registerListener(sensorListener, rotationVectorSensor, toSamplingPeriodUs(100))
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun observeSensor(sensorType: Int): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
            val sensorListener = SensorListener(emitter)
            sensorManager.registerListener(sensorListener, sensor, toSamplingPeriodUs(100))
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    class SensorListener(
            private val emitter: FlowableEmitter<LoggedEvent>
    ) : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if (emitter.isCancelled.not()) {
                val recordedAtNanos: Long = SystemClock.elapsedRealtimeNanos()
                emitter.onNext(LoggedEvent(sensorEvent, recordedAtNanos))
            }
        }
    }

    // 1/25 = 0.04 or 40000 microseconds
    private fun toSamplingPeriodUs(signalsPerSecond: Int): Int {
        return 1000000 / signalsPerSecond
    }
}

data class LoggedEvent(
        val sensorEvent: SensorEvent,
        val recordedAtNanos: Long
)