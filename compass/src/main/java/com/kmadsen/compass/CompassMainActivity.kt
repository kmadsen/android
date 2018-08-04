package com.kmadsen.compass

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.location.CompassLocation
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.mapbox.MapViewController
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.disposables.CompositeDisposable

class CompassMainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var mapViewController: MapViewController? = null
    private lateinit var locationsController: LocationsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        val glView = findViewById<CompassGLSurfaceView>(R.id.surface_view)

        locationsController = LocationsController(
                LocationPermissions(),
                FusedLocationService(application)
        )

        val mapView = findViewById<MapView>(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            L.i("The map is ready")
            mapViewController = MapViewController(mapView, mapboxMap)
            compositeDisposable.add(locationsController.firstValidLocation()
                    .subscribe { compassLocation: CompassLocation ->
                        mapViewController!!.centerMap(compassLocation.latitude, compassLocation.longitude)
                    })
            compositeDisposable.add(locationsController.allFusedLocations()
                    .subscribe { fusedLocation: FusedLocation ->
                        run {
                            mapViewController!!.updatePinLocation(fusedLocation)
                        }
                    })
        }
    }

    override fun onStart() {
        super.onStart()

        locationsController.onStart(this)
    }

    override fun onStop() {
        locationsController.onStop()
        compositeDisposable.clear()

        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationsController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
