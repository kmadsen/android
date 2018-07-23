package com.kmadsen.compass.location

import android.app.Activity
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kylemadsen.core.logger.L
import kotlin.properties.Delegates

class LocationActivityService(
        private val locationPermissions: LocationPermissions,
        private val fusedLocationService: FusedLocationService
) {
    private var fusedLocation: FusedLocation by Delegates.observable(FusedLocation()) {
        property, oldValue, newValue ->


    }

    fun onStart(activity: Activity) {
        locationPermissions.onActivityStart(activity) {
            isGranted ->
            L.i("permission granted $isGranted")
            if (isGranted.not()) {
                return@onActivityStart
            }
            fusedLocationService.start { locationUpdate -> fusedLocation = locationUpdate }
        }
    }

    fun onStop() {
        fusedLocationService.stop()
    }

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<out String>,
                                   grantResults: IntArray) {
        locationPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
