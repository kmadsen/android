package com.kmadsen.compass

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.fusedcompass.CompassView
import com.kmadsen.compass.location.CompassLocation
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.SensorLogger
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CompassMainActivity : AppCompatActivity() {


    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
    private lateinit var compassGLSurfaceView: CompassGLSurfaceView

    private lateinit var compassView: CompassView

    @Inject lateinit var locationsController: LocationsController
    @Inject lateinit var androidSensors: AndroidSensors
    @Inject lateinit var sensorLogger: SensorLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        DaggerCompassComponent.builder()
                .compassModule(CompassModule(this))
                .build()
                .inject(this)

        compassGLSurfaceView = findViewById(R.id.surface_view)

        compassView = findViewById(R.id.magnetometer)

        compositeDisposable.add(sensorLogger.attachFileWriting(this).subscribe())

        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            mapViewController = MapViewController(mapboxMap)
            compositeDisposable.add(locationsController.firstValidLocation()
                    .subscribe { compassLocation: CompassLocation ->
                        mapViewController.centerMap(compassLocation.latitude, compassLocation.longitude)
                        compassView.onLocationChanged(compassLocation)
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

        compositeDisposable.add(androidSensors.observeRotationVector()
                .subscribe { compassGLSurfaceView.update(it.sensorEvent.values) }
        )

        locationsController.onStart(this)
    }

    override fun onStop() {
        locationsController.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()

        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationsController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
