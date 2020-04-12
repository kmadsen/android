package com.kmadsen.compass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.config.SensorConfigActivity
import com.kylemadsen.core.koin.koinLateModule
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

class CompassMainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
//    private lateinit var compassGLSurfaceView: CompassGLSurfaceView

    private val locationSensor: LocationSensor by inject()
    private val mapLateModule by koinLateModule()

    override fun onCreate(savedInstanceState: Bundle?) {
        loadKoinModules(localizationModule)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.compass_main_activity)

//        compassGLSurfaceView = map_gl_surface_view
//        compassGLSurfaceView.setZOrderMediaOverlay(true)

        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            mapLateModule.load(module {
                single { mapboxMap }
            })

            mapViewController = MapViewController()
            mapViewController.attach(findViewById(R.id.map_overlay_view))

            mapboxMap.setStyle(Style.TRAFFIC_DAY)
//            mapComponent.inject(compassGLSurfaceView)
//            compassGLSurfaceView.attach()
            mapboxMap.addOnMapLongClickListener {
                false
            }
        }

        findViewById<Button>(R.id.configure_sensors).setOnClickListener {
            val intent = Intent(this, SensorConfigActivity::class.java)
            startActivity(intent)
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
//        compassGLSurfaceView.detach()
        mapViewController.detach()

        mapLateModule.unload()
        unloadKoinModules(localizationModule)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationSensor.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
