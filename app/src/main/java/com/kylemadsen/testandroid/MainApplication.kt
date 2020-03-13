package com.kylemadsen.testandroid

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.hardware.SensorManager
import com.google.gson.Gson
import com.kylemadsen.core.logger.AndroidLogger
import com.kylemadsen.core.logger.L
import com.kylemadsen.core.time.DeviceBootTimeProvider
import com.kylemadsen.core.time.DeviceClock
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {

    init {
        initLogging()
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }

        initDeviceClock()
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
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

val appModule = module {
    single<Resources> { androidApplication().resources }
    single { androidContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
}
