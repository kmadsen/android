package com.kmadsen.compass

import android.content.Context
import android.content.res.Resources
import android.net.wifi.WifiManager
import com.kmadsen.compass.sensors.rx.RxAltitudeSensor
import com.kmadsen.compass.sensors.rx.RxAzimuthSensor
import com.kmadsen.compass.sensors.rx.RxDeviceDirectionSensor
import com.kmadsen.compass.sensors.rx.TurnSensor
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.sensors.CompassSensorManager
import com.kmadsen.compass.sensors.config.SensorConfigManager
import com.kmadsen.compass.sensors.rx.RxAndroidSensors
import com.kmadsen.compass.walking.WalkingStateSensor
import com.kmadsen.compass.wifilocation.WifiLocationScanner
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocationApi
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocationApiService
import com.kmadsen.compass.wifilocation.wifiscan.WifiScanReceiver
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val localizationModule = module {

    single { LocationRepository() }
    single { LocationPermissions() }
    single { FusedLocationService(androidApplication()) }
    single { LocationSensor(get(), get(), get() ) }
    single { RxAndroidSensors(get()) }
    single { TurnSensor(get(), get()) }
    single { SensorConfigManager(get(), get(), get()) }
    single { CompassSensorManager(get(), get()) }
    single { RxAzimuthSensor(get(), get()) }
    single {
        RxDeviceDirectionSensor(
            get(),
            get(),
            get()
        )
    }
    single { RxAltitudeSensor(get(), get()) }
    single { WalkingStateSensor(get(), get()) }
    single {
        val resources = get<Resources>()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val googleGeolocationApi = retrofit.create(GoogleGeolocationApi::class.java)

        GoogleGeolocationApiService(
            googleGeolocationApi,
            resources.getString(R.string.google_geolocation_api)
        )
    }
    single {
        val wifiManager = androidApplication().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        WifiScanReceiver(wifiManager)
    }
    single { WifiLocationScanner(get(), get(), get()) }

}
