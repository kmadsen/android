package com.kmadsen.compass

import android.content.Context
import android.content.res.Resources
import android.hardware.SensorManager
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.AzimuthService
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class CompassModule(private val compassMainActivity: CompassMainActivity) {

    @Provides
    fun provideResources(): Resources {
        return compassMainActivity.resources
    }

    @Provides
    fun provideLocationPermissions(): LocationPermissions {
        return LocationPermissions()
    }

    @Provides
    fun provideLocationLocationsController(
            locationPermissions: LocationPermissions
    ): LocationsController {
        return LocationsController(
                locationPermissions,
                FusedLocationService(compassMainActivity.application)
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
    fun provideAzimuthService(androidSensors: AndroidSensors): AzimuthService {
        return AzimuthService(androidSensors)
    }
}

@Component(modules = [CompassModule::class])
interface CompassComponent {
    fun inject(mainActivity: CompassMainActivity)
}
