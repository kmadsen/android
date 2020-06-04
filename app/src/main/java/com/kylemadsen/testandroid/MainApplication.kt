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
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {

    init {
        initLogging()
    }

    private val deviceBootTimeProvider: DeviceBootTimeProvider by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }

        DeviceClock.initialize(deviceBootTimeProvider)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            L.add(AndroidLogger.getInstance())
        }
    }
}

val appModule = module {
    single<Resources> { androidApplication().resources }
    single { androidContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    single { androidApplication().getSharedPreferences("compass_preferences", Context.MODE_PRIVATE) }
    single { Gson() }
    single { DeviceBootTimeProvider(get(), get()) }
}
