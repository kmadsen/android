package com.kmadsen.compass.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.kmadsen.compass.sensors.config.SensorConfigManager
import com.kylemadsen.core.time.toSamplingPeriodMicros
import java.util.concurrent.CopyOnWriteArrayList

typealias CompassSensorEventListener = (SensorEvent) -> Unit

class CompassSensorManager(
    private val context: Context,
    private val sensorManager: SensorManager,
    private val sensorConfigManager: SensorConfigManager
) : SensorEventListener {

    private var eventEmitter: CompassSensorEventListener? = null
    private var sensorFileWriter: SensorFileWriter? = null
    private var eventListeners = CopyOnWriteArrayList<CompassSensorEventListener>()

    suspend fun start(eventEmitter: CompassSensorEventListener) {
        this.eventEmitter = eventEmitter

        val sensorConfigs = sensorConfigManager.loadSensorConfigs()
        val sensorFileWriter = SensorFileWriter.open(context)
        val sensorList = sensorConfigs.map { it.sensor }
        sensorFileWriter.write(sensorList)
        this.sensorFileWriter = sensorFileWriter

        sensorConfigs.forEach { sensorConfig ->
            val samplingPeriodUs = toSamplingPeriodMicros(sensorConfig.preference.signalsPerSecond)
            sensorManager.registerListener(this,
                sensorConfig.sensor,
                samplingPeriodUs)
        }
    }

    suspend fun broadcastSensorEvent(sensorEvent: SensorEvent) {
        sensorFileWriter?.write(sensorEvent)
        eventListeners.forEach { it(sensorEvent) }
    }

    fun registerListener(compassSensorEventListener: CompassSensorEventListener) {
        eventListeners.add(compassSensorEventListener)
    }

    fun unregisterListener(compassSensorEventListener: CompassSensorEventListener) {
        eventListeners.remove(compassSensorEventListener)
    }

    fun stop() {
        eventEmitter = null
        sensorManager.unregisterListener(this)
        sensorFileWriter?.close()
    }

    override fun onSensorChanged(event: SensorEvent) {
        eventEmitter?.invoke(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Haven't found a need for this
    }
}
