package com.kmadsen.compass

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.mapbox.MapModule
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.SensorLogger
import com.kylemadsen.core.FpsChoreographer
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CompassMainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
//    private lateinit var compassGLSurfaceView: CompassGLSurfaceView
//    private lateinit var compassView: CompassView

    @Inject lateinit var locationsController: LocationsController
    @Inject lateinit var sensorLogger: SensorLogger
    @Inject lateinit var azimuthSensor: AzimuthSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        val compassComponent = DaggerCompassComponent.builder()
                .compassModule(CompassModule(this))
                .build()
        compassComponent.inject(this)


//        compassView = findViewById(R.id.compass_view)

        compositeDisposable.add(FpsChoreographer().attach().subscribe())
        compositeDisposable.add(azimuthSensor.attachSensorUpdates()
                .subscribe())

//        compositeDisposable.add(azimuthSensor.observeAzimuth()
//                .subscribe { compassView.updateAzimuthRadians(it.deviceDirectionRadians) })

        compositeDisposable.add(sensorLogger.attachFileWriting(this)
                .subscribe())

//        compassGLSurfaceView = findViewById(R.id.surface_view)
//        compositeDisposable.add(androidSensors.observeRotationVector()
//                .subscribe { compassGLSurfaceView.update(it.sensorEvent.values) })

        L.i("start map")
        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            L.i("start map loaded")
            val mapComponent = compassComponent.plus(MapModule(mapboxMap))
            mapViewController = MapViewController()
            mapComponent.inject(mapViewController)
            mapViewController.attach(findViewById(R.id.map_overlay_view))
            L.i("start map all ready")
        }
    }

    override fun onStart() {
        super.onStart()
        locationsController.onStart(this)
    }

    override fun onStop() {
        locationsController.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        mapViewController.onDestroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationsController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


