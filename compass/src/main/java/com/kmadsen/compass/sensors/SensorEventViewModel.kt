package com.kmadsen.compass.sensors

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.sensors.config.SensorConfig
import com.kmadsen.compass.sensors.config.SensorConfigManager
import com.kylemadsen.core.koin.inject
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SensorEventViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sensorConfigManager: SensorConfigManager by inject()
    private val navigationSensorManager: CompassSensorManager by inject()

    fun start(eventEmitter: (SensorEvent) -> Unit) {
        val eventEmitterWithWriter = attachEventFileWriter(eventEmitter)
        viewModelScope.launch {
            navigationSensorManager.start(getApplication(), eventEmitterWithWriter)
        }
    }

    override fun onCleared() {
        navigationSensorManager.stop()

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
                navigationSensorManager.writeEvent(sensorEvent)
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

