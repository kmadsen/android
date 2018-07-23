package com.kmadsen.compass.location

import android.app.Activity
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kylemadsen.core.logger.L

class LocationActivityService(
        val locationPermissions: LocationPermissions,
        val fusedLocationService: FusedLocationService
) {

    fun onStart(activity: Activity) {
        locationPermissions.onActivityStart(activity) {
            isGranted ->
            L.i("permission granted $isGranted")
            if (isGranted) {
                fusedLocationService.start { locationUpdate ->
                    L.i("thread: %d rawLocationUpdate %s", Thread.currentThread().id, locationUpdate.toString())
                }
            }
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
