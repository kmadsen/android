package com.kmadsen.compass.fusedcompass

import android.hardware.GeomagneticField
import android.hardware.SensorManager
import com.kmadsen.compass.location.CompassLocation
import com.kylemadsen.core.logger.L
import java.lang.Math.min
import java.lang.Math.toDegrees
import java.util.concurrent.TimeUnit
import kotlin.math.PI

class CompassModel : ICompassModel {

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var lastAccelerometerNanos: Long = 0
    private var lastMagnetometerNanos: Long = 0

    private var lastAzimuthInRadians: Double? = null

    private var geomagneticField: GeomagneticField? = null

    override fun onLocationChange(compassLocation: CompassLocation) {
        if (compassLocation.altitudeMeters == null) return

        val geomagneticField = GeomagneticField(
                compassLocation.latitude.toFloat(),
                compassLocation.longitude.toFloat(),
                compassLocation.altitudeMeters.toFloat(),
                compassLocation.timeMillis)
        this.geomagneticField = geomagneticField

        L.i("geomagneticField update x=${geomagneticField.x} y=${geomagneticField.x} z=${geomagneticField.z}")
        L.i("geomagneticField update declination=${geomagneticField.declination}")
        L.i("geomagneticField update inclination=${geomagneticField.inclination}")
        L.i("geomagneticField update fieldStrength=${geomagneticField.fieldStrength}")
        L.i("geomagneticField update horizontalStrength=${geomagneticField.horizontalStrength}")
    }

    override fun onMagneticFieldChange(timeNanos: Long, eventValues: FloatArray) {
        val alpha = if (lastMagnetometerNanos > 0) {
            min(0.9, ((timeNanos - lastMagnetometerNanos) / TimeUnit.MILLISECONDS.toNanos(100).toDouble()))
        } else {
            0.15
        }
        lastMagnetometerNanos = timeNanos
        val x = (lastMagnetometer[0] + 0.015 * (eventValues[0] - lastMagnetometer[0])).toFloat()
        System.out.println("$x = ${lastMagnetometer[0]} + $alpha * (${eventValues[0]} - ${lastMagnetometer[0]} $alpha")
        val floatArray = FloatArray(3) { i ->
            (lastMagnetometer[i] + alpha * (eventValues[i] - lastMagnetometer[i])).toFloat()
        }
        System.arraycopy(floatArray, 0, lastMagnetometer, 0, eventValues.size)
    }

    override fun onAccelerationChange(timeNanos: Long, eventValues: FloatArray) {
        val alpha = if (lastAccelerometerNanos > 0) {
            min(0.9, (timeNanos - lastAccelerometerNanos) / TimeUnit.MILLISECONDS.toNanos(100).toDouble())
        } else {
            0.15
        }
        lastAccelerometerNanos = timeNanos
        val floatArray = FloatArray(3) { i ->
            (lastAccelerometer[i] + alpha * (eventValues[i] - lastAccelerometer[i])).toFloat()
        }
        System.arraycopy(floatArray, 0, lastAccelerometer, 0, eventValues.size)
    }

    override fun getAzimuthInRadians(): Float {

        SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
        SensorManager.getOrientation(rotationMatrix, orientation)

        var azimuthInRadians = PI - orientation[0].toDouble()

        lastAzimuthInRadians = azimuthInRadians.mRound(0.5)
        L.i("getAzimuthInRadians $lastAzimuthInRadians magnetometer x=${lastMagnetometer[0]} y=${lastMagnetometer[1]} z=${lastMagnetometer[2]}")
        return azimuthInRadians.toFloat()
    }
}

fun Double.mRound(factor: Double): Double {
    return Math.round(this / factor) * factor
}
