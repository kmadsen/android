package com.kmadsen.compass

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.location.LocationPermissions

import com.kmadsen.compass.location.LocationService
import com.kmadsen.compass.mapbox.MapViewController
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView

class CompassMainActivity : AppCompatActivity() {

    private var locationPermissions: LocationPermissions = LocationPermissions()

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

    override fun onStart() {
        super.onStart()
        L.i("onStart")

        locationPermissions.onActivityStart(this) {
            isGranted ->
            L.i("permission granted $isGranted")
            if (isGranted) {
                locationService.start { locationUpdate ->
                    L.i("thread: %d rawLocationUpdate %s", Thread.currentThread().id, locationUpdate.toString())
                }
            }
        }
    }

    override fun onStop() {
        locationService.stop()

        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
