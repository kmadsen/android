package com.kmadsen.compass.mapbox

import com.kmadsen.compass.CompassGLSurfaceView
import com.mapbox.mapboxsdk.maps.MapboxMap
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Module
class MapModule(val mapboxMap: MapboxMap) {

    @Provides
    fun provideMapboxMap(): MapboxMap {
        return mapboxMap
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MapScope

@MapScope
@Subcomponent(modules = [MapModule::class])
interface MapComponent {
    fun inject(mapViewController: MapViewController)
    fun inject(compassGLSurfaceView: CompassGLSurfaceView)
}
