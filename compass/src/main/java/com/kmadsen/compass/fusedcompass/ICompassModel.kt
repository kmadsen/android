package com.kmadsen.compass.fusedcompass

import com.kmadsen.compass.location.CompassLocation

interface ICompassModel {

    fun onLocationChange(compassLocation: CompassLocation)
    fun onMagneticFieldChange(timeNanos: Long, eventValues: FloatArray)
    fun onAccelerationChange(timeNanos: Long, eventValues: FloatArray)
    fun getAzimuthInRadians(): Float
}
