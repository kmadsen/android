package com.kmadsen.compass

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.kmadsen.compass.location.CompassLocation
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.mapbox.MapViewController
import com.kmadsen.compass.sensors.LoggedEvent
import com.kmadsen.compass.fusedcompass.CompassView
import com.kmadsen.compass.location.LocationsController
import com.kmadsen.compass.sensors.AndroidSensors
import com.kylemadsen.core.FileLogger
import com.kylemadsen.core.WritableFile
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.maps.MapView
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CompassMainActivity : AppCompatActivity() {


    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mapViewController: MapViewController
    private lateinit var compassGLSurfaceView: CompassGLSurfaceView

    private lateinit var compassView: CompassView

    @Inject lateinit var locationsController: LocationsController
    @Inject lateinit var androidSensors: AndroidSensors
    @Inject lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_main_activity)

        DaggerCompassComponent.builder()
                .compassModule(CompassModule(this))
                .build()
                .inject(this)

        compassGLSurfaceView = findViewById(R.id.surface_view)

        compassView = findViewById(R.id.magnetometer)

        compositeDisposable.add(FileLogger(this).observeWritableFile("accelerometer")
                .flatMapCompletable { writableFile -> writableFile.writeAccelerometer() }
                .subscribe())

        compositeDisposable.add(FileLogger(this).observeWritableFile("gyroscope")
                .flatMapCompletable { writableFile -> writableFile.writeGyroscope() }
                .subscribe())

        compositeDisposable.add(FileLogger(this).observeWritableFile("magnetometer")
                .flatMapCompletable { writableFile -> writableFile.writeMagnetometer() }
                .subscribe())

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

    private fun WritableFile.writeAccelerometer(): Completable {
        val sensorType: Int = Sensor.TYPE_ACCELEROMETER
        return androidSensors.observeSensor(sensorType)
                .doOnSubscribe {
                    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
                    writeLine("name=${sensor.name} vendor=${sensor.vendor} current_time_ms=${System.currentTimeMillis()}")
                    writeLine("measured_at recorded_at accuracy value_x value_y value_z")
                }
                .doOnNext { loggedEvent: LoggedEvent ->
                    compassView.onAccelerationChange(loggedEvent.sensorEvent.timestamp, loggedEvent.sensorEvent.values)

                    val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                            " ${loggedEvent.recordedAtNanos}" +
                            " ${loggedEvent.sensorEvent.accuracy}" +
                            " ${loggedEvent.sensorEvent.values[0]}" +
                            " ${loggedEvent.sensorEvent.values[1]}" +
                            " ${loggedEvent.sensorEvent.values[2]}"
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
        val sensorType: Int = Sensor.TYPE_GYROSCOPE
        return androidSensors.observeSensor(sensorType)
                .doOnSubscribe {
                    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
                    writeLine("name=${sensor.name} vendor=${sensor.vendor} current_time_ms=${System.currentTimeMillis()}")
                    writeLine("measured_at recorded_at accuracy value_x value_y value_z")
                }
                .doOnNext { loggedEvent: LoggedEvent ->
                    val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                            " ${loggedEvent.recordedAtNanos}" +
                            " ${loggedEvent.sensorEvent.accuracy}" +
                            " ${loggedEvent.sensorEvent.values[0]}" +
                            " ${loggedEvent.sensorEvent.values[1]}" +
                            " ${loggedEvent.sensorEvent.values[2]}"
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

    private fun WritableFile.writeMagnetometer(): Completable {
        val sensorType: Int = Sensor.TYPE_MAGNETIC_FIELD
        return androidSensors.observeSensor(sensorType)
                .doOnSubscribe {
                    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
                    writeLine("name=${sensor.name} vendor=${sensor.vendor} current_time_ms=${System.currentTimeMillis()}")
                    writeLine("measured_at recorded_at accuracy value_x value_y value_z")
                }
                .doOnNext { loggedEvent: LoggedEvent ->
                    compassView.onMagneticFieldChange(loggedEvent.sensorEvent.timestamp, loggedEvent.sensorEvent.values)

                    val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                            " ${loggedEvent.recordedAtNanos}" +
                            " ${loggedEvent.sensorEvent.accuracy}" +
                            " ${loggedEvent.sensorEvent.values[0]}" +
                            " ${loggedEvent.sensorEvent.values[1]}" +
                            " ${loggedEvent.sensorEvent.values[2]}"
                    writeLine(sensorLine)
                }
                .buffer(50).doOnNext {
                    val startTime = SystemClock.elapsedRealtime()
                    flushBuffer()
                    L.i("DEBUG_FILE time to flush ${SystemClock.elapsedRealtime() - startTime}")
                }
                .ignoreElements()
                .onErrorComplete()
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
