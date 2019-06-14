package com.kmadsen.compass.wifilocation.googlegeolocation

import android.os.SystemClock
import com.google.gson.Gson
import com.kmadsen.compass.wifilocation.wifiscan.WifiScan
import com.kylemadsen.core.logger.L
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers


class GoogleGeolocationApiService(
    private val googleGeolocationApi: GoogleGeolocationApi,
    private val apiKey: String
) {

    fun requestWifiLocation(wifiScan: WifiScan): Single<GoogleGeolocateResponse> {
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

        val singleResponse = Single.create { emitter: SingleEmitter<GoogleGeolocateResponse> ->
            try {
                val response = callResponse.execute()

                if (response.isSuccessful && response.body() != null) {
                    emitter.onSuccess(GoogleGeolocateResponse(successResponse = response.body()))
                } else if (response.errorBody() != null){
                    val errorBody = Gson().fromJson(response.errorBody()!!.charStream(), GoogleGeolocateErrorResponseDTO::class.java)
                    emitter.onSuccess(GoogleGeolocateResponse(failureResponse = errorBody))
                }
            } catch (t: Throwable) {
                emitter.onSuccess(GoogleGeolocateResponse(throwable = t))
            }
        }
        return singleResponse.subscribeOn(Schedulers.io())
    }
}

data class GoogleGeolocateResponse(
    val successResponse: GoogleGeolocateResponseDTO? = null,
    val failureResponse: GoogleGeolocateErrorResponseDTO? = null,
    val throwable: Throwable? = null
)