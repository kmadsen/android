package com.kmadsen.compass

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.kmadsen.compass.location.LocationService
import com.kmadsen.compass.mapbox.MapViewController
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView
import kotlin.properties.Delegates

class CompassMainActivity : AppCompatActivity() {

    lateinit var mapViewController: MapViewController
    lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        val glView = findViewById<CompassGLSurfaceView>(R.id.surface_view)

        locationService = LocationService(application)

        val mapView = findViewById<MapView>(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap -> mapViewController = MapViewController(mapView, mapboxMap) }
    }

    override fun onResume() {
        super.onResume()

        locationService.start {
            rawLocationUpdate ->
            L.i("thread: %d rawLocationUpdate %s", Thread.currentThread().id, rawLocationUpdate.toString())
        }
    }

    override fun onPause() {
        locationService.stop()

        super.onPause()
    }
}
