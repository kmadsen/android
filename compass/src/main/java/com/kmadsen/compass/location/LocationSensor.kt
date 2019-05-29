package com.kmadsen.compass.location

import android.app.Activity
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.location.fused.FusedLocationService
import com.kylemadsen.core.logger.L
import io.reactivex.Observable
import io.reactivex.Single

class LocationSensor constructor(
        private val locationPermissions: LocationPermissions,
        private val fusedLocationService: FusedLocationService,
        private val locationRepository: LocationRepository
) {

    fun onStart(activity: Activity) {
        locationPermissions.onActivityStart(activity) { isGranted ->
            L.i("permission granted $isGranted")
            if (isGranted.not()) {
                locationRepository.updateLocation(null)
                return@onActivityStart
            }
            fusedLocationService.start {
                fusedLocation: FusedLocation ->
                locationRepository.updateFusedLocation(fusedLocation)
                locationRepository.updateLocation(fusedLocation.toBasicLocation())
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

    fun firstValidLocation(): Single<Optional<BasicLocation>> {
        return locationRepository.observeLocation()
                .filter { optionalLocation: Optional<BasicLocation> ->
                    optionalLocation.toNullable() != null
                }
                .first(None)
    }

    fun observeLocations(): Observable<Optional<BasicLocation>> {
        return locationRepository.observeLocation()
    }
}

private fun FusedLocation.toBasicLocation(): BasicLocation? {
    if (location == null) return null
    return BasicLocation(
            location.time,
            location.latitude,
            location.longitude,
            getNullableValue(location.hasAccuracy(), location.accuracy),
            getNullableValue(location.hasAltitude(), location.altitude),
            getNullableValue(location.hasBearing(), location.bearing),
            getNullableValue(location.hasSpeed(), location.speed)
    )
}

fun <ValueType> getNullableValue(hasValue: Boolean, value: ValueType): ValueType? {
    return if (hasValue) value else null
}

