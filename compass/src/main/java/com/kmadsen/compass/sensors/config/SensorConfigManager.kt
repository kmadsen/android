package com.kmadsen.compass.sensors.config

import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kylemadsen.core.logger.L
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.ceil

class SensorConfigManager(
    private val sensorManager: SensorManager,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    fun getSensorConfig(sensorType: Int): SensorConfig? {
        val savedSensorConfigs = savedSensorConfigs
        check(savedSensorConfigs.isNotEmpty()) { "You must first call loadSensorConfigs" }
        return savedSensorConfigs.firstOrNull { it.sensor.type == sensorType }
    }

    suspend fun loadSensorConfigs(): List<SensorConfig> = suspendCoroutine { cont ->
        val sensorConfigMap = SENSOR_CONFIG_PREFERENCES.toMutableMap()
        val sensorConfigsPreferences = loadFromPreferences()
        sensorConfigsPreferences.forEach { sensorConfigsPreference ->
            sensorConfigMap[sensorConfigsPreference.sensorType] = sensorConfigsPreference
        }

        val sensorConfigs = sensorManager.getSensorList(Sensor.TYPE_ALL)
            .filter { sensor ->
                sensorConfigMap.contains(sensor.type)
            }
            .mapNotNull { sensor ->
                val preference = sensorConfigMap[sensor.type]
                preference?.toSensorConfig(sensor)
            }
        savedSensorConfigs = sensorConfigs
        cont.resume(sensorConfigs)
    }

    fun saveSensorConfigs(sensorConfigs: List<SensorConfig>) {
        val configPreferences = sensorConfigs.map { it.preference }
        val configPreferencesJson = gson.toJson(configPreferences)
        L.i("sensor_debug sensor saveSensorConfigPreferences $configPreferencesJson")
        sharedPreferences.edit()
            .putString("sensor_config_json", configPreferencesJson)
            .apply()
        savedSensorConfigs = sensorConfigs
    }

    private fun loadFromPreferences(): List<SensorConfigPreference> {
        val json = sharedPreferences.getString("sensor_config_json", null) ?: ""
        L.i("sensor_debug loadSensorConfigPreferences: \"$json\"")
        return readFromJson(json)
    }

    private fun readFromJson(json: String?): List<SensorConfigPreference> {
        val failedList: List<SensorConfigPreference> = emptyList()
        return if (json.isNullOrEmpty()) {
            failedList
        } else {
            val successList: List<SensorConfigPreference> = try {
                val collectionType: Type = object : TypeToken<List<SensorConfigPreference>>() {}.type
                gson.fromJson(json, collectionType)
            } catch (t: Throwable) {
                L.e("sensor_debug Failed to load sensor config preferences: \"$json\"")
                failedList
            }
            successList
        }
    }

    private fun SensorConfigPreference.toSensorConfig(sensor: Sensor): SensorConfig {
        val minEventsPerSecond = takeIfSdk(21) { fromDelayUs(sensor.maxDelay) }
        val maxEventsPerSecond = fromDelayUs(sensor.minDelay)
        return SensorConfig(sensor, this, minEventsPerSecond, maxEventsPerSecond)
    }

    private fun fromDelayUs(minDelayUs: Int): Int? {
        return if (minDelayUs == 0) {
            null
        } else {
            ceil(MICROSECONDS_IN_SECONDS / minDelayUs).toInt()
        }
    }

    internal companion object {
        private var savedSensorConfigs: List<SensorConfig> = emptyList()

        val SENSOR_CONFIG_PREFERENCES: MutableMap<Int, SensorConfigPreference> by lazy {
            val defaultPreferences = mutableMapOf(
                Sensor.TYPE_ACCELEROMETER to defaultSensorConfigPreference(Sensor.TYPE_ACCELEROMETER),
                Sensor.TYPE_MAGNETIC_FIELD to defaultSensorConfigPreference(Sensor.TYPE_MAGNETIC_FIELD),
                Sensor.TYPE_GYROSCOPE to defaultSensorConfigPreference(Sensor.TYPE_GYROSCOPE),
                Sensor.TYPE_GRAVITY to defaultSensorConfigPreference(Sensor.TYPE_GRAVITY),
                Sensor.TYPE_PRESSURE to defaultSensorConfigPreference(Sensor.TYPE_PRESSURE),
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED to defaultSensorConfigPreference(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED),
                Sensor.TYPE_GYROSCOPE_UNCALIBRATED to defaultSensorConfigPreference(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
            )
            if (Build.VERSION.SDK_INT >= 26) {
                defaultPreferences[Sensor.TYPE_ACCELEROMETER_UNCALIBRATED] = defaultSensorConfigPreference(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
            }
            defaultPreferences
        }

        private const val MICROSECONDS_IN_SECONDS = 1e6
    }
}

private fun defaultSensorConfigPreference(sensorType: Int): SensorConfigPreference {
    return SensorConfigPreference(
        sensorType,
        25
    )
}

inline fun <T> takeIfSdk(version: Int, f: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= version) f() else null
}
