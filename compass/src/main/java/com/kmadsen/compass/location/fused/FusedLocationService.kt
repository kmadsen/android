package com.kmadsen.compass.location.fused

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.kylemadsen.core.logger.L
import java.util.concurrent.TimeUnit

class FusedLocationService(val application: Application) {

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

    private lateinit var onRawLocationUpdate: (FusedLocation) -> (Unit)

    @SuppressLint("MissingPermission")
    fun start(fusedLocationUpdate: (FusedLocation) -> (Unit)) {
        L.i(".start")
        this.onRawLocationUpdate = fusedLocationUpdate

        providerClient.requestLocationUpdates(locationRequest, locationCallback, null)
        providerClient.lastLocation.addOnSuccessListener {
            location: Location? ->
            L.i(".lastLocationSuccess " + location?.toString())
            this@FusedLocationService.lastLocation = location
            fusedLocationUpdate.invoke(getRawLocationUpdate())
        }
    }

    fun stop() {
        L.i(".stop")
        providerClient.removeLocationUpdates(locationCallback)
    }

    private fun getRawLocationUpdate(): FusedLocation {
        return FusedLocation(
                lastLocation,
                locationAvailability,
                locationResult
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            L.i(".onLocationAvailability " + locationAvailability?.toString())
            this@FusedLocationService.locationAvailability = locationAvailability
            onRawLocationUpdate.invoke(getRawLocationUpdate())
        }

        override fun onLocationResult(locationResult: LocationResult?) {
            L.i(".onLocationResult " + locationResult?.toString())
            L.i(".onLocationResult " + locationResult?.locations?.size)
            this@FusedLocationService.locationResult = locationResult
            onRawLocationUpdate.invoke(getRawLocationUpdate())
        }
    }
}
