package com.kmadsen.compass.sensors.rx

import android.hardware.SensorEvent
import android.os.SystemClock
import com.kmadsen.compass.sensors.CompassSensorEventListener
import com.kmadsen.compass.sensors.CompassSensorManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Emitter
import io.reactivex.Flowable

class RxAndroidSensors(
    private val sensorManager: CompassSensorManager
) {
    fun observeSensor(sensorType: Int): Flowable<LoggedEvent> {
        return Flowable.create({ emitter ->
            val sensorListener = LoggedSensorEventListener(emitter, sensorType)
            sensorManager.registerListener(sensorListener)
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun observeRawSensor(sensorType: Int): Flowable<SensorEvent> {
        return Flowable.create({ emitter ->
            val sensorListener = RawSensorEventListener(emitter, sensorType)
            sensorManager.registerListener(sensorListener)
            emitter.setCancellable {
                sensorManager.unregisterListener(sensorListener)
            }
        }, BackpressureStrategy.BUFFER)
    }
}

private class RawSensorEventListener(
    private val emitter: Emitter<SensorEvent>,
    private val sensorType: Int
) : CompassSensorEventListener {
    override fun invoke(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == sensorType) {
            emitter.onNext(sensorEvent)
        }
    }
}

private class LoggedSensorEventListener(
    private val emitter: Emitter<LoggedEvent>,
    private val sensorType: Int
) : CompassSensorEventListener {
    override fun invoke(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == sensorType) {
            val recordedAtNanos: Long = SystemClock.elapsedRealtimeNanos()
            val loggedEvent = LoggedEvent(sensorEvent, recordedAtNanos)
            emitter.onNext(loggedEvent)
        }
    }
}

data class LoggedEvent(
        val sensorEvent: SensorEvent,
        val recordedAtNanos: Long
)
