package com.kmadsen.compass

import android.content.Context
import android.content.res.Resources
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.azimuth.TurnSensor
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.mapbox.MapBottomSheet
import com.kmadsen.compass.mapbox.MapComponent
import com.kmadsen.compass.mapbox.MapModule
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.SensorLogger
import com.kmadsen.compass.walking.WalkingStateSensor
import com.kmadsen.compass.wifilocation.WifiLocationScanner
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocationApi
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocationApiService
import com.kmadsen.compass.wifilocation.wifiscan.WifiScanReceiver
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Scope


@Module
class CompassModule(private val compassMainActivity: CompassMainActivity) {

    @Provides
    fun provideResources(): Resources {
        return compassMainActivity.resources
    }

    @Provides
    @CompassScope
    fun provideLocationRepository(): LocationRepository {
        return LocationRepository()
    }

    @Provides
    fun provideLocationLocationsController(
            locationRepository: LocationRepository
    ): LocationSensor {
        return LocationSensor(
                LocationPermissions(),
                FusedLocationService(compassMainActivity.application),
                locationRepository
        )
    }

    @Provides
    fun provideSensorManager(): SensorManager {
        return compassMainActivity
                .getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    fun provideAndroidSensors(sensorManager: SensorManager): AndroidSensors {
        return AndroidSensors(sensorManager)
    }

    @Provides
    fun provideSensorLogger(
            androidSensors: AndroidSensors,
            sensorManager: SensorManager,
            locationRepository: LocationRepository
    ): SensorLogger {
        return SensorLogger(
                androidSensors,
                sensorManager,
                locationRepository
        )
    }

    @Provides
    fun provideTurnSensor(
        androidSensors: AndroidSensors
    ): TurnSensor {
        return TurnSensor(androidSensors)
    }

    @Provides
    fun provideAzimuthSensor(
            androidSensors: AndroidSensors,
            turnSensor: TurnSensor,
            locationRepository: LocationRepository
    ): AzimuthSensor {
        return AzimuthSensor(androidSensors, turnSensor, locationRepository)
    }

    @Provides
    fun provideWalkingSensor(
        androidSensors: AndroidSensors,
        locationRepository: LocationRepository
    ): WalkingStateSensor {
        return WalkingStateSensor(androidSensors, locationRepository)
    }

    @Provides
    fun googleGeolocationApiService(
        resources: Resources
    ) : GoogleGeolocationApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val googleGeolocationApi = retrofit.create(GoogleGeolocationApi::class.java)

        return GoogleGeolocationApiService(
            googleGeolocationApi,
            resources.getString(R.string.compass_google_geolocation_api)
        )
    }

    @Provides
    fun provideWifiScanReceiver(): WifiScanReceiver {
        val wifiManager = compassMainActivity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return WifiScanReceiver(wifiManager)
    }

    @Provides
    fun provideWifiLocationScanner(
        wifiScanReceiver: WifiScanReceiver,
        geolocationApiService: GoogleGeolocationApiService,
        locationRepository: LocationRepository
    ): WifiLocationScanner {
        return WifiLocationScanner(
            wifiScanReceiver,
            geolocationApiService,
            locationRepository
        )
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CompassScope

@CompassScope
@Component(modules = [CompassModule::class])
interface CompassComponent {
    fun inject(mainActivity: CompassMainActivity)
    fun plus(mapModule: MapModule): MapComponent
    fun inject(bottomSheet: MapBottomSheet)
}

