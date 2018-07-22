package com.kmadsen.compass.location

import android.location.Location
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult

data class LocationUpdate(
        val location: Location?,
        val locationAvailability: LocationAvailability?,
        val locationResult: LocationResult?)
