package com.kylemadsen.testandroid

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson

import com.kylemadsen.core.logger.AndroidLogger
import com.kylemadsen.core.logger.L
import com.kylemadsen.core.time.DeviceBootTimeProvider
import com.kylemadsen.core.time.DeviceClock
import com.mapbox.mapboxsdk.Mapbox

class MainApplication : Application() {

    init {
        initLogging()
    }

    override fun onCreate() {
        super.onCreate()

        initDeviceClock()
        Mapbox.getInstance(applicationContext, getString(R.string.compass_mapbox_access_token))
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            L.add(AndroidLogger.getInstance())
        }
    }

    private fun initDeviceClock() {
        val deviceBootTimeProvider = DeviceBootTimeProvider(
            gson = Gson(),
            clockPreferences = getSharedPreferences("device_clock", Context.MODE_PRIVATE)
        )
        DeviceClock.initialize(deviceBootTimeProvider)
    }
}
