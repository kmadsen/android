package com.kmadsen.compass.wifilocation

import android.content.Context
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocateResponse
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocateResponseDTO
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocationApiService
import com.kmadsen.compass.wifilocation.wifiscan.WifiScan
import com.kmadsen.compass.wifilocation.wifiscan.WifiScanReceiver
import com.kylemadsen.core.logger.L
import io.reactivex.Observable
import io.reactivex.Single

class WifiLocationScanner(
    private val wifiScanReceiver: WifiScanReceiver,
    private val geolocationApiService: GoogleGeolocationApiService,
    private val locationRepository: LocationRepository
) {
    fun observeWifiLocations(context: Context): Observable<WifiLocationResponse> {
        return wifiScanReceiver.observeWifiScans(context)
            .flatMapSingle { requestWifiLocation(it) }
    }

    private fun requestWifiLocation(wifiScan: WifiScan): Single<WifiLocationResponse> {
        return geolocationApiService.requestWifiLocation(wifiScan)
            .map {
                WifiLocationResponse(
                    wifiScan,
                    System.currentTimeMillis(),
                    it.successResponse?.toWifiLocation(),
                    it.toFailureMessage()
                )
            }
            .doOnSuccess {
                locationRepository.updateWifiLocation(it)
            }
    }
}

private fun GoogleGeolocateResponseDTO.toWifiLocation(): WifiLocation? {
    if (location == null || accuracy == null) return null

    return WifiLocation(
        location.lat,
        location.lng,
        accuracy
    )
}

private fun GoogleGeolocateResponse.toFailureMessage(): String? {
    if (failureResponse != null) {
        L.i("Was there a failure response $failureResponse")
        return "Google Response Error"
    }
    if (throwable != null) {
        L.i(throwable, "There was an exception")
        return "Crash error"
    }
    return null
}

data class WifiLocationResponse(
    val wifiScan: WifiScan,
    val recordedAtMs: Long,
    val wifiLocation: WifiLocation?,
    val errorMessage: String?
)

data class WifiLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double
)
