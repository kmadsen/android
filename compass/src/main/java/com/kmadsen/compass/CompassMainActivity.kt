package com.kmadsen.compass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.mapbox.MapModule
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.SensorLogger
import com.kylemadsen.core.FpsChoreographer
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.compass_main_activity.*
import javax.inject.Inject

class CompassMainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController

    @Inject lateinit var locationsController: LocationsController
    @Inject lateinit var sensorLogger: SensorLogger

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

        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            val mapComponent = compassComponent.plus(MapModule(mapboxMap))
            mapViewController = MapViewController()
            mapComponent.inject(mapViewController)
            mapViewController.attach(findViewById(R.id.map_overlay_view))
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
        mapViewController.detach()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationsController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
