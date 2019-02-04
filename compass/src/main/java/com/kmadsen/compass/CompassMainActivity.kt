package com.kmadsen.compass

import android.hardware.Sensor
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.location.CompassLocation
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.LoggedEvent
import com.kylemadsen.core.FileLogger
import com.kylemadsen.core.WritableFile
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class CompassMainActivity : AppCompatActivity() {

    private lateinit var compassDependencies: CompassDependencies

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
    private lateinit var compassGLSurfaceView: CompassGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        compassDependencies = CompassDependencies(this)

        compassGLSurfaceView = findViewById(R.id.surface_view)

        compositeDisposable.add(FileLogger(this).observeWritableFile()
                .flatMapCompletable { writableFile ->
                    writableFile.writeInBuffers()
                }
                .doOnError { throwable: Throwable ->
                    L.i(throwable, "DEBUG_FILE file writer closed")
                }
                .subscribe())

        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            L.i("The map is ready")
            mapViewController = MapViewController(mapboxMap)
            compositeDisposable.add(compassDependencies.locationsController.firstValidLocation()
                    .subscribe { compassLocation: CompassLocation ->
                        mapViewController.centerMap(compassLocation.latitude, compassLocation.longitude)
                    })
            compositeDisposable.add(compassDependencies.locationsController.allFusedLocations()
                    .subscribe { fusedLocation: FusedLocation ->
                        run {
                            mapViewController.updatePinLocation(fusedLocation)
                        }
                    })
        }
    }

    private fun WritableFile.writeInBuffers(): Completable {
        return compassDependencies.positionSensors.observeSensor(Sensor.TYPE_ACCELEROMETER)
                .buffer(1L, TimeUnit.SECONDS)
                .doOnNext { loggedEventList: List<LoggedEvent> ->
                    val startTime = SystemClock.elapsedRealtime()
                    writeLine("read block ${loggedEventList.size}")
                    loggedEventList.forEach { loggedEvent ->
                        writeLine("measuredAt=${loggedEvent.sensorEvent.timestamp} recordedAt=${loggedEvent.recordedAtNanos}")
                    }
                    flushBuffer()
                    L.i("DEBUG_FILE time to flush ${SystemClock.elapsedRealtime() - startTime}")
                }
                .ignoreElements()
    }

    private fun WritableFile.writeEach(): Completable {
        return compassDependencies.positionSensors.observeSensor(Sensor.TYPE_ACCELEROMETER)
                .doOnNext { loggedEvent ->
                    writeLine("measuredAt=${loggedEvent.sensorEvent.timestamp} recordedAt=${loggedEvent.recordedAtNanos}")
                    flushBuffer()
                }
                .ignoreElements()
    }

    override fun onStart() {
        super.onStart()

        compositeDisposable.add(compassDependencies.positionSensors.observeRotationVector()
                .subscribe { compassGLSurfaceView.update(it.sensorEvent.values) }
        )

        compassDependencies.locationsController.onStart(this)
    }

    override fun onStop() {
        compassDependencies.locationsController.onStop()
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        compassDependencies.locationsController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
