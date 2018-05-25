package com.kmadsen.compass.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class LocationPermissions {

    private val permissionRequestCode: Int = 201

    private lateinit var permissionGranted: (isGranted: Boolean) -> (Unit)

    fun onActivityStart(activity: Activity,
                        permissionGranted: (isGranted: Boolean) -> (Unit)) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions granted at install time.
            permissionGranted.invoke(true)
            return
        }

        this.permissionGranted = permissionGranted
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted.invoke(true)
        } else {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    permissionRequestCode)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<out String>,
                                   grantResults: IntArray) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                permissionGranted.invoke(true)
            } else {
                permissionGranted.invoke(false)
            }
        }
    }
}