package com.kmadsen.compass.location

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kmadsen.compass.azimuth.Azimuth
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.walking.WalkingState
import com.kmadsen.compass.wifilocation.WifiLocationResponse
import io.reactivex.Observable

class LocationRepository {

    private val locationRelay: BehaviorRelay<Optional<BasicLocation>> = BehaviorRelay.createDefault(None)
    private val fusedLocationRelay: BehaviorRelay<FusedLocation> = BehaviorRelay.create()
    private val azimuthRelay: BehaviorRelay<Azimuth> = BehaviorRelay.create()
    private val walkingStateRelay: BehaviorRelay<WalkingState> = BehaviorRelay.create()
    private val wifiLocationRelay: BehaviorRelay<WifiLocationResponse> = BehaviorRelay.create()

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

    fun observeAzimuth(): Observable<Azimuth> {
        return azimuthRelay
    }

    fun updateAzimuth(azimuth: Azimuth) {
        azimuthRelay.accept(azimuth)
    }

    fun observeWalkingState(): Observable<WalkingState> {
        return walkingStateRelay
    }

    fun updateWalkingState(walkingState: WalkingState) {
        walkingStateRelay.accept(walkingState)
    }

    fun observeWifiLocation(): Observable<WifiLocationResponse> {
        return wifiLocationRelay
    }

    fun updateWifiLocation(wifiLocationResponse: WifiLocationResponse) {
        wifiLocationRelay.accept(wifiLocationResponse)
    }
}
