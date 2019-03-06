package com.kmadsen.compass.fusedcompass

import android.hardware.GeomagneticField
import android.hardware.SensorManager
import com.kmadsen.compass.location.CompassLocation
import com.kylemadsen.core.logger.L
import java.lang.Math.toDegrees
import kotlin.math.PI

class CompassModel {

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private var currentDegree = 0.0

    private var geomagneticField: GeomagneticField? = null


    fun onLocationChange(compassLocation: CompassLocation) {
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

    fun onMagneticFieldChange(eventValues: FloatArray) {
        System.arraycopy(eventValues, 0, lastMagnetometer, 0, eventValues.size)


        lastMagnetometerSet = true
    }

    fun onAccelerationChange(eventValues: FloatArray) {
        System.arraycopy(eventValues, 0, lastAccelerometer, 0, eventValues.size)
        lastAccelerometerSet = true
    }

    fun isReady(): Boolean {
        return lastAccelerometerSet && lastMagnetometerSet
    }

    fun getAzimuthInRadians(): Float {
        L.i("getAzimuthInRadians magnetometer x=${lastMagnetometer[0]} y=${lastMagnetometer[1]} z=${lastMagnetometer[2]}")
        L.i("getAzimuthInRadians magnetometer x=${lastMagnetometer[0]} y=${lastMagnetometer[1]} z=${lastMagnetometer[2]}")

        SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
        SensorManager.getOrientation(rotationMatrix, orientation)
        val azimuthInRadians = orientation[0] - PI
        currentDegree = toDegrees(azimuthInRadians)
        return azimuthInRadians.toFloat()
    }
}
