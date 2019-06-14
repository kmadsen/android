package com.kmadsen.compass.wifilocation.googlegeolocation

import android.os.SystemClock
import com.kmadsen.compass.wifilocation.wifiscan.WifiScan
import com.kylemadsen.core.logger.L
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers


class GoogleGeolocationApiService(
    private val googleGeolocationApi: GoogleGeolocationApi,
    private val apiKey: String
) {

    fun requestWifiLocation(wifiScan: WifiScan): Single<GoogleGeolocateResponseDTO> {
        L.i("requestWifiLocation from ${wifiScan.wifiAccessPoints.size} access points")

        val requestBody = GoogleGeolocateRequestDTO(
            wifiAccessPoints = wifiScan.wifiAccessPoints.map {
                GoogleGeolocateRequestWifiAccessPointsDTO(
                    macAddress = it.bssid,
                    signalStrength = it.rssi,
                    age = SystemClock.elapsedRealtime() - it.recordedElapsedTimeMs,
                    channel = it.channelWidth
                )
            }
        )

        val callResponse = googleGeolocationApi.geolocate(apiKey, requestBody)

        val singleResponse = Single.create { emitter: SingleEmitter<GoogleGeolocateResponseDTO> ->
            try {
                val response = callResponse.execute()

                if (response.isSuccessful && response.body() != null) {
                    emitter.onSuccess(response.body()!!)
                } else {
                    emitter.onError(Exception("Failed request: ${response.errorBody()?.string()}"))
                }
            } catch (t: Throwable) {
                emitter.onError(t)
            }
        }
        return singleResponse.subscribeOn(Schedulers.io())
    }
}
