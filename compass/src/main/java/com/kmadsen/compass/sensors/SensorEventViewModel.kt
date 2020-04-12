package com.kmadsen.compass.sensors

import android.app.Application
import android.hardware.SensorEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.kmadsen.compass.sensors.config.SensorConfig
import com.kmadsen.compass.sensors.config.SensorConfigManager
import com.kylemadsen.core.koin.inject
import com.kylemadsen.core.logger.L
import kotlinx.coroutines.launch

class SensorEventViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sensorConfigManager: SensorConfigManager by inject()
    private val navigationSensorManager: CompassSensorManager by inject()

    fun start(eventEmitter: (SensorEvent) -> Unit) {
        L.i("sensor_debug start sensors")
        val eventEmitterWithWriter = attachEventFileWriter(eventEmitter)
        viewModelScope.launch {
            navigationSensorManager.start(eventEmitterWithWriter)
        }
    }

    fun stop() {
        navigationSensorManager.stop()
    }

    override fun onCleared() {
        stop()

        super.onCleared()
    }

    fun loadSensorConfigs(function: (List<SensorConfig>) -> Unit) {
        viewModelScope.launch {
            val sensorConfigs = sensorConfigManager.loadSensorConfigs()
            function(sensorConfigs)
        }
    }

    private fun attachEventFileWriter(eventEmitter: (SensorEvent) -> Unit): (SensorEvent) -> Unit {
        return { sensorEvent ->
            eventEmitter(sensorEvent)
            viewModelScope.launch {
                navigationSensorManager.broadcastSensorEvent(sensorEvent)
            }
        }
    }

    fun saveSensorConfigs(sensorConfigs: List<SensorConfig>) {
        sensorConfigManager.saveSensorConfigs(sensorConfigs)
    }

    companion object {
        fun get(owner: ViewModelStoreOwner): SensorEventViewModel {
            return ViewModelProvider(owner).get(SensorEventViewModel::class.java)
        }
    }
}

