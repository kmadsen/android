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

        compositeDisposable.add(FileLogger(this).observeWritableFile("accelerometer")
                .flatMapCompletable { writableFile -> writableFile.writeAccelerometer() }
                .subscribe())

        compositeDisposable.add(FileLogger(this).observeWritableFile("gyroscope")
                .flatMapCompletable { writableFile -> writableFile.writeGyroscope() }
                .subscribe())

        val mapView: MapView = findViewById(R.id.mapbox_mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
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

    private fun WritableFile.writeAccelerometer(): Completable {
        return compassDependencies.androidSensors.observeSensor(Sensor.TYPE_ACCELEROMETER)
                .doOnSubscribe {
                    val sensor: Sensor = compassDependencies.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                    writeLine("name=${sensor.name} vendor=${sensor.vendor}")
                    writeLine("measured_at\trecorded_at\taccuracy\tvalue_x\tvalue_y\tvalue_z")
                }
                .doOnNext { loggedEvent: LoggedEvent ->
                    val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                            "\t${loggedEvent.recordedAtNanos}" +
                            "\t${loggedEvent.sensorEvent.accuracy}" +
                            "\t${loggedEvent.sensorEvent.values[0]}" +
                            "\t${loggedEvent.sensorEvent.values[1]}" +
                            "\t${loggedEvent.sensorEvent.values[2]}"
                    writeLine(sensorLine)
                }
                .buffer(500).doOnNext {
                    val startTime = SystemClock.elapsedRealtime()
                    flushBuffer()
                    L.i("DEBUG_FILE time to flush ${SystemClock.elapsedRealtime() - startTime}")
                }
                .ignoreElements()
                .onErrorComplete()
    }

    private fun WritableFile.writeGyroscope(): Completable {
        return compassDependencies.androidSensors.observeSensor(Sensor.TYPE_GYROSCOPE)
                .doOnSubscribe {
                    val sensor: Sensor = compassDependencies.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
                    writeLine("name=${sensor.name} vendor=${sensor.vendor}")
                    writeLine("measured_at\trecorded_at\taccuracy\tvalue_x\tvalue_y\tvalue_z")
                }
                .doOnNext { loggedEvent: LoggedEvent ->
                    val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                            "\t${loggedEvent.recordedAtNanos}" +
                            "\t${loggedEvent.sensorEvent.accuracy}" +
                            "\t${loggedEvent.sensorEvent.values[0]}" +
                            "\t${loggedEvent.sensorEvent.values[1]}" +
                            "\t${loggedEvent.sensorEvent.values[2]}"
                    writeLine(sensorLine)
                }
                .buffer(500).doOnNext {
                    val startTime = SystemClock.elapsedRealtime()
                    flushBuffer()
                    L.i("DEBUG_FILE time to flush ${SystemClock.elapsedRealtime() - startTime}")
                }
                .ignoreElements()
                .onErrorComplete()
    }


    override fun onStart() {
        super.onStart()

        compositeDisposable.add(compassDependencies.androidSensors.observeRotationVector()
                .subscribe { compassGLSurfaceView.update(it.sensorEvent.values) }
        )

        compassDependencies.locationsController.onStart(this)
    }

    override fun onStop() {
        compassDependencies.locationsController.onStop()
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

        compassDependencies.locationsController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
