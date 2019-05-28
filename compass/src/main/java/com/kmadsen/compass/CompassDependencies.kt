package com.kmadsen.compass

import android.content.Context
import android.content.res.Resources
import android.hardware.SensorManager
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.mapbox.MapBottomSheet
import com.kmadsen.compass.mapbox.MapComponent
import com.kmadsen.compass.mapbox.MapModule
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.SensorLogger
import dagger.Component
import dagger.Module
import dagger.Provides
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
    ): LocationsController {
        return LocationsController(
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
    fun provideAzimuthSensor(
            androidSensors: AndroidSensors,
            locationRepository: LocationRepository
    ): AzimuthSensor {
        return AzimuthSensor(androidSensors, locationRepository)
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

