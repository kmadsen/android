package com.kmadsen.compass.location

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kmadsen.compass.azimuth.Measure1d
import com.kmadsen.compass.location.fused.FusedLocation
import com.kmadsen.compass.walking.WalkingState
import com.kmadsen.compass.wifilocation.WifiLocationResponse
import io.reactivex.Observable

class LocationRepository {

    private val locationRelay: BehaviorRelay<Optional<BasicLocation>> = BehaviorRelay.createDefault(None)
    private val fusedLocationRelay: BehaviorRelay<FusedLocation> = BehaviorRelay.create()
    private val azimuthRelay: BehaviorRelay<Measure1d> = BehaviorRelay.create()
    private val turnRelay: BehaviorRelay<Measure1d> = BehaviorRelay.create()
    private val deviceDirectionRelay: BehaviorRelay<Measure1d> = BehaviorRelay.create()
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

    fun observeAzimuth(): Observable<Measure1d> {
        return azimuthRelay
    }

    fun updateAzimuth(azimuthMeasure1d: Measure1d) {
        azimuthRelay.accept(azimuthMeasure1d)
    }

    fun updateTurnDegrees(turnDegreesMeasure1d: Measure1d) {
        turnRelay.accept(turnDegreesMeasure1d)
    }

    fun observeTurnDegrees(): Observable<Measure1d> {
        return turnRelay
    }

    fun updateDeviceDirection(deviceDirectionMeasure1d: Measure1d) {
        deviceDirectionRelay.accept(deviceDirectionMeasure1d)
    }

    fun observeDeviceDirection(deviceDirectionMeasure1d: Measure1d): Observable<Measure1d> {
        return deviceDirectionRelay
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
