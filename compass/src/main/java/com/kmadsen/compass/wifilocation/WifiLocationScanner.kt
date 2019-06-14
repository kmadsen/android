package com.kmadsen.compass.wifilocation

import android.content.Context
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocateResponseDTO
import com.kmadsen.compass.wifilocation.googlegeolocation.GoogleGeolocationApiService
import com.kmadsen.compass.wifilocation.wifiscan.WifiScan
import com.kmadsen.compass.wifilocation.wifiscan.WifiScanReceiver
import io.reactivex.Observable
import io.reactivex.Single

class WifiLocationScanner(
    private val wifiScanReceiver: WifiScanReceiver,
    private val geolocationApiService: GoogleGeolocationApiService
) {
    fun observeWifiLocations(context: Context): Observable<WifiLocationResponse> {
        return wifiScanReceiver.observeWifiScans(context)
            .flatMapSingle { requestWifiLocation(it) }
    }

    private fun requestWifiLocation(wifiScan: WifiScan): Single<WifiLocationResponse> {
        return geolocationApiService.requestWifiLocation(wifiScan)
            .map {
                WifiLocationResponse(
                    System.currentTimeMillis(),
                    it.toWifiLocation()
                )
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

data class WifiLocationResponse(
    val recordedAtMs: Long,
    val wifiLocation: WifiLocation?
)

data class WifiLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double
)