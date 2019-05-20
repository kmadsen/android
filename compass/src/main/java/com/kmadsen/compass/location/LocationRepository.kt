package com.kmadsen.compass.location

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kmadsen.compass.location.fused.FusedLocation
import io.reactivex.Observable

class LocationRepository {

    private val locationRelay: BehaviorRelay<Optional<BasicLocation>> = BehaviorRelay.createDefault(None)
    private val fusedLocationRelay: BehaviorRelay<FusedLocation> = BehaviorRelay.create()

    fun observeLocation(): Observable<Optional<BasicLocation>> {
        return locationRelay
    }

    fun updateLocation(basicLocation: BasicLocation?) {
        locationRelay.accept(basicLocation.toOptional())
    }

    fun observeFusedLocations(): Observable<FusedLocation> {
        return fusedLocationRelay
    }

    fun updateFusedLocation(fusedLocation: FusedLocation) {
        fusedLocationRelay.accept(fusedLocation)
    }
}
