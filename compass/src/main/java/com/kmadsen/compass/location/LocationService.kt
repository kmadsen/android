package com.kmadsen.compass.location

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.kylemadsen.core.logger.L
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


@SuppressLint("MissingPermission") // TODO the permission check should be on a "start()" function
class LocationService(application: Application) {

    // Configuration
    private val locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .setInterval(TimeUnit.SECONDS.toMillis(5))

    var lastLocation: Location? = null
    var locationAvailability: LocationAvailability? = null
    var locationResult: LocationResult? = null

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        L.i("fusedLocationProviderClient)")
        LocationServices.getFusedLocationProviderClient(application)
    }

    private lateinit var onRawLocationUpdate: (RawLocationUpdate) -> (Unit)

    fun start(onRawLocationUpdate: (RawLocationUpdate) -> (Unit)) {
        L.i(".start")
        this.onRawLocationUpdate = onRawLocationUpdate
        activeFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun stop() {
        L.i(".stop")
        activeFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun getRawLocationUpdate(): RawLocationUpdate {
        return RawLocationUpdate(
                lastLocation,
                locationAvailability,
                locationResult
        )
    }

    private val activeFusedLocationProviderClient by Delegates.observable(fusedLocationProviderClient) {
        _, old, new ->
        L.i("activeFusedLocationProviderClient")
        old.removeLocationUpdates(locationCallback)
        new.lastLocation.addOnSuccessListener {
            location: Location? ->
            L.i(".lastLocationSuccess " + location?.toString())
            this@LocationService.lastLocation = location
            onRawLocationUpdate.invoke(getRawLocationUpdate())
        }
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
