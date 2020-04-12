package com.kmadsen.compass.sensors

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kmadsen.compass.sensors.config.SensorConfig
import com.kmadsen.compass.sensors.config.SensorConfigManager
import kotlinx.coroutines.launch

class SensorEventViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("sensor_config", Context.MODE_PRIVATE)
    private val sensorConfigManager = SensorConfigManager(sensorManager, sharedPreferences, Gson())
    private val navigationSensorManager = CompassSensorManager(sensorManager, sensorConfigManager)

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
}

