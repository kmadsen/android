package com.kmadsen.compass

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.location.CompassLocation
import com.kmadsen.compass.location.LocationPermissions
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kmadsen.compass.mapbox.MapViewController
import com.kylemadsen.core.FileLogger
import com.kylemadsen.core.WritableFile
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.disposables.CompositeDisposable

class CompassMainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
    private lateinit var locationsController: LocationsController
    private lateinit var compassGLSurfaceView: CompassGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        compassGLSurfaceView = findViewById(R.id.surface_view)

        locationsController = LocationsController(
                LocationPermissions(),
                FusedLocationService(application)
        )

        compositeDisposable.add(FileLogger(this).observeWritableFile()
                .doOnNext { writableFile: WritableFile ->
                    writableFile.writeLine("writeLine something")
                    writableFile.flushBuffer()
                }
                .doOnError { throwable: Throwable ->
                    L.i(throwable, "DEBUG_FILE file writer closed")
                }
                .subscribe())

        val mapView = findViewById<MapView>(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            L.i("The map is ready")
            mapViewController = MapViewController(mapboxMap)
            compositeDisposable.add(locationsController.firstValidLocation()
                    .subscribe { compassLocation: CompassLocation ->
                        mapViewController.centerMap(compassLocation.latitude, compassLocation.longitude)
                    })
            compositeDisposable.add(locationsController.allFusedLocations()
                    .subscribe { fusedLocation: FusedLocation ->
                        run {
                            mapViewController.updatePinLocation(fusedLocation)
                        }
                    })
        }
    }

    override fun onStart() {
        super.onStart()

        compassGLSurfaceView.onStart()
        locationsController.onStart(this)
    }

    override fun onStop() {
        compassGLSurfaceView.onStop()
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
