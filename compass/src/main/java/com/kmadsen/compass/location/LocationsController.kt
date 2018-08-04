package com.kmadsen.compass.location

import android.app.Activity
import android.location.Location
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kylemadsen.core.logger.L
import io.reactivex.Observable
import io.reactivex.Single

class LocationsController constructor(
        private val locationPermissions: LocationPermissions,
        private val fusedLocationService: FusedLocationService
) {
    private val fusedLocationRelay: BehaviorRelay<FusedLocation> = BehaviorRelay.create()

    fun onStart(activity: Activity) {
        locationPermissions.onActivityStart(activity) {
            isGranted ->
            L.i("permission granted $isGranted")
            if (isGranted.not()) {
                return@onActivityStart
            }
            fusedLocationService.start {
                fusedLocation: FusedLocation ->
                fusedLocationRelay.accept(fusedLocation)
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

    fun firstValidLocation(): Single<CompassLocation> {
        return fusedLocationRelay
                    .filter { fusedLocation: FusedLocation -> fusedLocation.location != null }
                    .first(FusedLocation())
                    .map { fusedLocation: FusedLocation ->
                        val location: Location? = fusedLocation.location
                        if (location != null) {
                            return@map CompassLocation(location.latitude, location.longitude)
                        }
                        return@map CompassLocation(0.0, 0.0)
                    }
    }

    fun allFusedLocations(): Observable<FusedLocation> {
        return fusedLocationRelay
    }
}
