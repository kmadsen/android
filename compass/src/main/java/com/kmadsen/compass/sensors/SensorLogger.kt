package com.kmadsen.compass.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import com.kmadsen.compass.azimuth.Measure1d
import com.kmadsen.compass.azimuth.Measure3d
import com.kmadsen.compass.azimuth.lowPassFilter
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.location.fused.FusedLocation
import com.kylemadsen.core.FileLogger
import com.kylemadsen.core.WritableFile
import com.kylemadsen.core.logger.L
import io.reactivex.Completable
import java.util.concurrent.TimeUnit
import kotlin.math.min

class SensorLogger(
        private val androidSensors: AndroidSensors,
        private val sensorManager: SensorManager,
        private val locationRepository: LocationRepository
) {
    fun attachFileWriting(context: Context): Completable {
        val accelerometerLogger: Completable = FileLogger(context)
                .observeWritableFile("accelerometer")
                .flatMapCompletable { writableFile -> writableFile.write3dSensor(Sensor.TYPE_ACCELEROMETER) }
        val gravityLogger: Completable = FileLogger(context)
                .observeWritableFile("gravity")
                .flatMapCompletable { writableFile -> writableFile.write3dSensor(Sensor.TYPE_GRAVITY) }
        val gyroscopeLogger: Completable = FileLogger(context)
                .observeWritableFile("gyroscope")
                .flatMapCompletable { writableFile -> writableFile.write3dSensor(Sensor.TYPE_GYROSCOPE) }
        val magnetometerLogger: Completable = FileLogger(context)
                .observeWritableFile("magnetometer")
                .flatMapCompletable { writableFile -> writableFile.write3dSensor(Sensor.TYPE_MAGNETIC_FIELD) }
        val magnetometerFilteredLogger: Completable = FileLogger(context)
                .observeWritableFile("magnetometer_filtered")
                .flatMapCompletable { writableFile -> writableFile.writeFiltered3dSensor(Sensor.TYPE_MAGNETIC_FIELD) }
        val pressureLogger: Completable = FileLogger(context)
                .observeWritableFile("pressure")
                .flatMapCompletable { writableFile -> writableFile.write1dSensor(Sensor.TYPE_PRESSURE) }
        val locationLogger: Completable = FileLogger(context)
                .observeWritableFile("gps_locations")
                .flatMapCompletable { writableFile -> writableFile.writeLocations() }
        val azimuthLogger: Completable = FileLogger(context)
                .observeWritableFile("azimuth")
                .flatMapCompletable { writableFile -> writableFile.writeAzimuth() }

        return Completable.mergeArray(
                accelerometerLogger,
                gravityLogger,
                gyroscopeLogger,
                magnetometerLogger,
                magnetometerFilteredLogger,
                pressureLogger,
                locationLogger,
                azimuthLogger
        )
    }

    private fun WritableFile.writeFiltered3dSensor(sensorType: Int): Completable {
        val magnetometer = Measure3d()
        return androidSensors.observeSensor(sensorType)
            .doOnSubscribe {
                val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
                writeLine("name=${sensor.name} vendor=${sensor.vendor} current_time_ms=${System.currentTimeMillis()}")
                writeLine("measured_at recorded_at accuracy value_x value_y value_z")
            }
            .doOnNext { loggedEvent: LoggedEvent ->
                magnetometer.lowPassFilter(loggedEvent)
                val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                    " ${magnetometer.recordedAtNanos}" +
                    " ${magnetometer.accuracy}" +
                    " ${magnetometer.values[0]}" +
                    " ${magnetometer.values[1]}" +
                    " ${magnetometer.values[2]}"
                writeLine(sensorLine)
            }
            .buffer(500).doOnNext {
                flushBuffer()
            }
            .ignoreElements()
            .onErrorComplete()
    }

    fun Measure3d.lowPassFilter(nextEstimate: LoggedEvent): Measure3d {
        val nanosEstimateDelta = (nextEstimate.sensorEvent.timestamp - measuredAtNanos)
        val delayEstimateNanos = TimeUnit.MILLISECONDS.toNanos(500).toDouble()
        val alpha = min(0.9, (nanosEstimateDelta / delayEstimateNanos)).toFloat()
        x = lowPassFilter(x, nextEstimate.sensorEvent.values[0], alpha)
        y = lowPassFilter(y, nextEstimate.sensorEvent.values[1], alpha)
        z = lowPassFilter(z, nextEstimate.sensorEvent.values[2], alpha)
        measuredAtNanos = nextEstimate.sensorEvent.timestamp
        accuracy = nextEstimate.sensorEvent.accuracy
        return this
    }

    private fun WritableFile.write3dSensor(sensorType: Int): Completable {
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
                    flushBuffer()
                }
                .ignoreElements()
                .onErrorComplete()
    }

    private fun WritableFile.write1dSensor(sensorType: Int): Completable {
        return androidSensors.observeSensor(sensorType)
                .doOnSubscribe {
                    val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
                    writeLine("name=${sensor.name} vendor=${sensor.vendor} current_time_ms=${System.currentTimeMillis()}")
                    writeLine("measured_at recorded_at accuracy value")
                }
                .doOnNext { loggedEvent: LoggedEvent ->
                    val sensorLine = "${loggedEvent.sensorEvent.timestamp}" +
                            " ${loggedEvent.recordedAtNanos}" +
                            " ${loggedEvent.sensorEvent.accuracy}" +
                            " ${loggedEvent.sensorEvent.values[0]}"
                    writeLine(sensorLine)
                }
                .buffer(500).doOnNext {
                    flushBuffer()
                }
                .ignoreElements()
                .onErrorComplete()
    }

    private fun WritableFile.writeLocations(): Completable {
        return locationRepository.observeFusedLocations()
                .doOnSubscribe {
                    writeLine("name=GpsLocations vendor=Google current_time_ms=${System.currentTimeMillis()}")
                    writeLine("isLocationAvailable size time latitude longitude speed bearingDegrees altitude accuracy verticalAccuracy")
                }
                .doOnNext { fusedLocation: FusedLocation ->
                    val sensorLine =
                            " ${fusedLocation.locationAvailability?.isLocationAvailable}" +
                            " ${fusedLocation.locationResult?.locations?.size}" +
                            " ${fusedLocation.location?.time}" +
                            " ${fusedLocation.location?.latitude}" +
                            " ${fusedLocation.location?.longitude}" +
                            " ${fusedLocation.location?.speed}" +
                            " ${fusedLocation.location?.bearing}" +
                            " ${fusedLocation.location?.altitude}" +
                            " ${fusedLocation.location?.accuracy}" +
                            " ${fusedLocation.location?.getOreoVerticalAccuracyMeters()}"
                    writeLine(sensorLine)
                    flushBuffer()
                }
                .ignoreElements()
                .onErrorComplete()
    }

    private fun Location.getOreoVerticalAccuracyMeters(): Float? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verticalAccuracyMeters
        } else {
            null
        }
    }

    private fun WritableFile.writeAzimuth(): Completable {
        return locationRepository.observeAzimuth()
                .doOnSubscribe {
                    writeLine("name=AzimuthSensor vendor=kmadsen current_time_ms=${System.currentTimeMillis()}")
                    writeLine("recordedAtMs value azimuthDegrees")
                }
                .doOnNext { measure1d: Measure1d ->
                    val sensorLine =
                            " ${measure1d.recordedAtMs}" +
                            " ${measure1d.value}" +
                            " ${measure1d.value}"
                    writeLine(sensorLine)
                    flushBuffer()
                }
                .ignoreElements()
                .onErrorComplete()
    }

    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun logDeviceSensors(sensorManager: SensorManager) {
            val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
            L.i("This phone has %d sensors", sensorList.size)
            for (sensor in sensorList) {
                val preOreoValues: String =
                        "  name: " + sensor.name + "\n" +
                        "  type: " + sensor.type + "\n" +
                        "  vendor: " + sensor.vendor + "\n" +
                        "  version: " + sensor.version + "\n" +
                        "  maximumRange: " + sensor.maximumRange + "\n" +
                        "  resolution: " + sensor.resolution + "\n" +
                        "  power: " + sensor.power + "\n" +
                        "  minDelay: " + sensor.minDelay + "\n" +
                        "  fifoReservedEventCount: " + sensor.fifoReservedEventCount + "\n" +
                        "  fifoMaxEventCount: " + sensor.fifoMaxEventCount + "\n"
                val postOreoValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    "  isDirectChannelTypeSupported(TYPE_MEMORY_FILE): " + sensor.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_MEMORY_FILE) + "\n" +
                    "  isDirectChannelTypeSupported(TYPE_HARDWARE_BUFFER): " + sensor.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_HARDWARE_BUFFER) + "\n" +
                    "  isWakeUpSensor: " + sensor.isWakeUpSensor + "\n" +
                    "  isDynamicSensor: " + sensor.isDynamicSensor + "\n"
                else ""
                L.i("{\n$preOreoValues$postOreoValues}")
            }
        }
    }
}
