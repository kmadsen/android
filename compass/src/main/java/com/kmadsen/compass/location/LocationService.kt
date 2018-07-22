package com.kmadsen.compass.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.kylemadsen.core.logger.L
import java.util.concurrent.TimeUnit

class LocationService(val application: Application) {

    private val locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .setInterval(TimeUnit.SECONDS.toMillis(5))

    private val providerClient: FusedLocationProviderClient by lazy {
        L.i("providerClient)")
        LocationServices.getFusedLocationProviderClient(application)
    }

    private var lastLocation: Location? = null
    private var locationAvailability: LocationAvailability? = null
    private var locationResult: LocationResult? = null

    private lateinit var onRawLocationUpdate: (LocationUpdate) -> (Unit)

    @SuppressLint("MissingPermission")
    fun start(onRawLocationUpdate: (LocationUpdate) -> (Unit)) {
        L.i(".start")
        this.onRawLocationUpdate = onRawLocationUpdate

        providerClient.requestLocationUpdates(locationRequest, locationCallback, null)
        providerClient.lastLocation.addOnSuccessListener {
            location: Location? ->
            L.i(".lastLocationSuccess " + location?.toString())
            this@LocationService.lastLocation = location
            onRawLocationUpdate.invoke(getRawLocationUpdate())
        }
    }

    fun stop() {
        L.i(".stop")
        providerClient.removeLocationUpdates(locationCallback)
    }

    private fun getRawLocationUpdate(): LocationUpdate {
        return LocationUpdate(
                lastLocation,
                locationAvailability,
                locationResult
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            L.i(".onLocationAvailability " + locationAvailability?.toString())
            this@LocationService.locationAvailability = locationAvailability
            onRawLocationUpdate.invoke(getRawLocationUpdate())
        }

        override fun onLocationResult(locationResult: LocationResult?) {
            L.i(".onLocationResult " + locationResult?.toString())
            L.i(".onLocationResult " + locationResult?.locations?.size)
            this@LocationService.locationResult = locationResult
            onRawLocationUpdate.invoke(getRawLocationUpdate())
        }
    }
}
