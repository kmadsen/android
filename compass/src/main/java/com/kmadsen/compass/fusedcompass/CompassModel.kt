package com.kmadsen.compass.fusedcompass

import android.hardware.GeomagneticField
import android.hardware.SensorManager
import com.kmadsen.compass.location.CompassLocation
import com.kylemadsen.core.logger.L
import java.lang.Math.toDegrees
import kotlin.math.PI

class CompassModel : ICompassModel {

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
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
        val x = (lastMagnetometer[0] + 0.015 * (eventValues[0] - lastMagnetometer[0])).toFloat()
        System.out.println("$x = ${lastMagnetometer[0]} + 0.015 * (${eventValues[0]} - ${lastMagnetometer[0]}")
        val floatArray = FloatArray(3) { i ->
            (lastMagnetometer[i] + 0.015 * (eventValues[i] - lastMagnetometer[i])).toFloat()
        }
        System.arraycopy(floatArray, 0, lastMagnetometer, 0, eventValues.size)

        lastMagnetometerSet = true
    }

    override fun onAccelerationChange(timeNanos: Long, eventValues: FloatArray) {
        val floatArray = FloatArray(3) { i ->
            (lastAccelerometer[i] + 0.015 * (eventValues[i] - lastAccelerometer[i])).toFloat()
        }
        System.arraycopy(floatArray, 0, lastAccelerometer, 0, eventValues.size)

        lastAccelerometerSet = true
    }

    override fun getAzimuthInRadians(): Float {

        SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
        SensorManager.getOrientation(rotationMatrix, orientation)
        var azimuthInRadians = orientation[0] - PI

        lastAzimuthInRadians = azimuthInRadians.mRound(0.5)
        L.i("getAzimuthInRadians $lastAzimuthInRadians magnetometer x=${lastMagnetometer[0]} y=${lastMagnetometer[1]} z=${lastMagnetometer[2]}")
        return azimuthInRadians.toFloat()
    }
}

fun Double.mRound(factor: Double): Double {
    return Math.round(this / factor) * factor
}
