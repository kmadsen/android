package com.kmadsen.compass.location

import android.app.Application
import android.content.Context
import android.location.LocationManager


class LocationService(application: Application) {

    val locationManager: LocationManager by lazy {
        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
