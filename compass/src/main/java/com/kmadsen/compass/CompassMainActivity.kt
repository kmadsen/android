package com.kmadsen.compass

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.location.LocationActivityService
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.mapbox.MapViewController
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView

class CompassMainActivity : AppCompatActivity() {

    var mapViewController: MapViewController? = null
    lateinit var locationActivityService: LocationActivityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        val glView = findViewById<CompassGLSurfaceView>(R.id.surface_view)

        locationActivityService = LocationActivityService(
                LocationPermissions(),
                FusedLocationService(application)
        )

        val mapView = findViewById<MapView>(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            mapboxMap ->
            L.i("The map is ready")
            mapViewController = MapViewController(mapView, mapboxMap)
        }
    }

    override fun onStart() {
        super.onStart()

        locationActivityService.onStart(this) {
            fusedLocation ->
            mapViewController?.updateLocation(fusedLocation)
        }
    }

    override fun onStop() {
        locationActivityService.onStop()

        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationActivityService.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
