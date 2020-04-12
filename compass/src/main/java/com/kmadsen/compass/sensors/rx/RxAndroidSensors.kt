package com.kmadsen.compass.sensors.rx

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.kylemadsen.core.time.toSamplingPeriodMicros
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

class RxAndroidSensors(private val sensorManager: SensorManager) {
    fun observeRotationVector(): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val rotationVectorSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            val sensorListener =
                SensorListener(
                    emitter
                )
            sensorManager.registerListener(sensorListener, rotationVectorSensor, toSamplingPeriodMicros(100))
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun observeSensor(sensorType: Int): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
            val sensorListener =
                SensorListener(
                    emitter
                )
            sensorManager.registerListener(sensorListener, sensor, toSamplingPeriodMicros(100))
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
                emitter.onNext(
                    LoggedEvent(
                        sensorEvent,
                        recordedAtNanos
                    )
                )
            }
        }
    }

    fun observeRawSensor(sensorType: Int): Flowable<SensorEvent> {
        return Flowable.create({ emitter ->
            val sensor: Sensor = sensorManager.getDefaultSensor(sensorType) ?: return@create
            val sensorListener =
                SensorRawListener(
                    emitter
                )
            sensorManager.registerListener(sensorListener, sensor, toSamplingPeriodMicros(100))
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    class SensorRawListener(
            private val emitter: FlowableEmitter<SensorEvent>
    ) : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if (emitter.isCancelled.not()) {
                emitter.onNext(sensorEvent)
            }
        }
    }
}

data class LoggedEvent(
        val sensorEvent: SensorEvent,
        val recordedAtNanos: Long
)
