package com.kmadsen.compass.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.viewModelScope
import com.kmadsen.compass.sensors.config.SensorConfigManager
import com.kylemadsen.core.logger.L
import com.kylemadsen.core.time.toSamplingPeriodMicros
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ext.scope

class CompassSensorManager(
    private val sensorManager: SensorManager,
    private val sensorConfigManager: SensorConfigManager
) : SensorEventListener {

    private var eventEmitter: (SensorEvent) -> Unit = { }
    private var sensorFileWriter: SensorFileWriter? = null

    suspend fun start(context: Context, eventEmitter: (SensorEvent) -> Unit) {
        this.eventEmitter = eventEmitter

        val sensorConfigs = sensorConfigManager.loadSensorConfigs()
        val sensorFileWriter = SensorFileWriter.open(context)
        val sensorList = sensorConfigs.map { it.sensor }
        sensorFileWriter.write(sensorList)
        this.sensorFileWriter = sensorFileWriter

        sensorConfigs.forEach { sensorConfig ->
            L.i("sensor_debug register listener ${sensorConfig.preference}")
            val samplingPeriodUs = toSamplingPeriodMicros(sensorConfig.preference.signalsPerSecond)
            sensorManager.registerListener(this,
                sensorConfig.sensor,
                samplingPeriodUs)
        }
    }

    fun stop() {
        eventEmitter = { }
        sensorManager.unregisterListener(this)
        sensorFileWriter?.close()
    }

    suspend fun writeEvent(sensorEvent: SensorEvent) {
        sensorFileWriter?.write(sensorEvent)
    }

    override fun onSensorChanged(event: SensorEvent) {
        eventEmitter.invoke(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Haven't found a need for this
    }
}
