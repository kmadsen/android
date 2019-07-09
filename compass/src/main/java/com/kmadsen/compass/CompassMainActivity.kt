package com.kmadsen.compass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.mapbox.MapModule
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.sensors.SensorLogger
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.compass_main_activity.*
import javax.inject.Inject

class CompassMainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
    private lateinit var compassGLSurfaceView: CompassGLSurfaceView

    @Inject lateinit var locationSensor: LocationSensor
    @Inject lateinit var sensorLogger: SensorLogger
    @Inject lateinit var androidSensors: AndroidSensors

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        val compassComponent = DaggerCompassComponent.builder()
                .compassModule(CompassModule(this))
                .build()
        compassComponent.inject(this)
        compassComponent.inject(bottom_sheet)

        compositeDisposable.add(sensorLogger.attachFileWriting(this)
                .subscribe())

//        compassGLSurfaceView = map_gl_surface_view
//        compassGLSurfaceView.setZOrderMediaOverlay(true)

        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            val mapComponent = compassComponent.plus(MapModule(mapboxMap))
            mapViewController = MapViewController()
            mapComponent.inject(mapViewController)
            mapViewController.attach(findViewById(R.id.map_overlay_view))

            mapComponent.inject(compassGLSurfaceView)
//            compassGLSurfaceView.attach()
        }
    }

    override fun onStart() {
        super.onStart()
        locationSensor.onStart(this)
    }

    override fun onStop() {
        locationSensor.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        compassGLSurfaceView.detach()
        mapViewController.detach()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationSensor.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
