package com.kmadsen.compass.sensors

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorManager
import android.os.Build
import com.kylemadsen.core.logger.L

class SensorLogger {
    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun logDeviceSensors(sensorManager: SensorManager) {
            val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
            L.i("This phone has %d sensors", sensorList.size)
            for (sensor in sensorList) {
                val preOreoValues: String =
                        "  name: " + sensor.name + "\n" +
                        "  type: " + sensor.type + "\n" +
                        "  vendor: " + sensor.vendor + "\n" +
                        "  version: " + sensor.version + "\n" +
                        "  maximumRange: " + sensor.maximumRange + "\n" +
                        "  resolution: " + sensor.resolution + "\n" +
                        "  power: " + sensor.power + "\n" +
                        "  minDelay: " + sensor.minDelay + "\n" +
                        "  fifoReservedEventCount: " + sensor.fifoReservedEventCount + "\n" +
                        "  fifoMaxEventCount: " + sensor.fifoMaxEventCount + "\n"
                val postOreoValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    "  isDirectChannelTypeSupported(TYPE_MEMORY_FILE): " + sensor.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_MEMORY_FILE) + "\n" +
                    "  isDirectChannelTypeSupported(TYPE_HARDWARE_BUFFER): " + sensor.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_HARDWARE_BUFFER) + "\n" +
                    "  isWakeUpSensor: " + sensor.isWakeUpSensor + "\n" +
                    "  isDynamicSensor: " + sensor.isDynamicSensor + "\n"
                else ""
                L.i("{\n$preOreoValues$postOreoValues}")
            }
        }
    }
}
