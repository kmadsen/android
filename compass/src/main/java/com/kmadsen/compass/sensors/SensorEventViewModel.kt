package com.kmadsen.compass.sensors

import android.app.Application
import android.hardware.SensorEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.kylemadsen.core.koin.inject
import kotlinx.coroutines.launch

class SensorEventViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val navigationSensorManager: CompassSensorManager by inject()

    fun start(eventEmitter: (SensorEvent) -> Unit) {
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

    private fun attachEventFileWriter(eventEmitter: (SensorEvent) -> Unit): (SensorEvent) -> Unit {
        return { sensorEvent ->
            eventEmitter(sensorEvent)
            viewModelScope.launch {
                navigationSensorManager.broadcastSensorEvent(sensorEvent)
            }
        }
    }

    companion object {
        fun get(owner: ViewModelStoreOwner): SensorEventViewModel {
            return ViewModelProvider(owner).get(SensorEventViewModel::class.java)
        }
    }
}

