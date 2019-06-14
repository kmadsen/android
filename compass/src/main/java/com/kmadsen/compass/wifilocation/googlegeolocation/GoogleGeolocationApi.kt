package com.kmadsen.compass.wifilocation.googlegeolocation

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Query

//https://developers.google.com/maps/documentation/geolocation/intro

interface GoogleGeolocationApi {

    @HTTP(method = "POST", path = "/geolocation/v1/geolocate", hasBody = true)
    fun geolocate(@Query("key") action: String,
                  @Body requestBody: GoogleGeolocateRequestDTO
    ) : Call<GoogleGeolocateResponseDTO>

}

/**
{
    "homeMobileCountryCode": 310,
    "homeMobileNetworkCode": 410,
    "radioType": "gsm",
    "carrier": "Vodafone",
    "considerIp": "true",
    "cellTowers": [
        {
            "cellId": 42,
            "locationAreaCode": 415,
            "mobileCountryCode": 310,
            "mobileNetworkCode": 410,
            "age": 0,
            "signalStrength": -60,
            "timingAdvance": 15
        }
    ],
    "wifiAccessPoints": [
        {
            "macAddress": "00:25:9c:cf:1c:ac",
            "signalStrength": -43,
            "age": 0,
            "channel": 11,
            "signalToNoiseRatio": 0
        }
    ]
}
*/
data class GoogleGeolocateRequestDTO(
    @SerializedName("homeMobileCountryCode") val homeMobileCountryCode: Int? = null,
    @SerializedName("homeMobileNetworkCode") val homeMobileNetworkCode: Int? = null,
    @SerializedName("radioType") val radioType: String? = null,
    @SerializedName("carrier") val carrier: String? = null,
    @SerializedName("considerIp") val considerIp: Boolean? = null,
    @SerializedName("cellTowers") val cellTowers: List<GoogleGeolocateRequestCellTowersDTO>? = null,
    @SerializedName("wifiAccessPoints") val wifiAccessPoints: List<GoogleGeolocateRequestWifiAccessPointsDTO>? = null
)

data class GoogleGeolocateRequestCellTowersDTO(
    @SerializedName("cellId") val cellId: Int? = null,
    @SerializedName("locationAreaCode") val locationAreaCode: Int? = null,
    @SerializedName("mobileCountryCode") val mobileCountryCode: Int? = null,
    @SerializedName("mobileNetworkCode") val mobileNetworkCode: Int? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("signalStrength") val signalStrength: Int? = null,
    @SerializedName("timingAdvance") val timingAdvance: Int? = null
)

data class GoogleGeolocateRequestWifiAccessPointsDTO(
    /** (required) The MAC address of the WiFi node. It's typically called a BSS, BSSID or MAC address. Separators must be : (colon). */
    @SerializedName("macAddress") val macAddress: String,

    /** The current signal strength measured in dBm. */
    @SerializedName("signalStrength") val signalStrength: Int? = null,

    /** The number of milliseconds since this access point was detected. */
    @SerializedName("age") val age: Long? = null,

    /** The channel over which the client is communicating with the access point. */
    @SerializedName("channel") val channel: Int? = null,

    /** The current signal to noise ratio measured in dB. */
    @SerializedName("signalToNoiseRatio") val signalToNoiseRatio: Int? = null
)

/**
{
    "location": {
        "lat": 51.0,
        "lng": -0.1
    },
    "accuracy": 1200.4
}
*/
data class GoogleGeolocateResponseDTO(
    @SerializedName("location") val location: GoogleGeolocateLocationDTO?,
    @SerializedName("accuracy") val accuracy: Double?
)

data class GoogleGeolocateLocationDTO(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

/**
{
    "error": {
        "errors": [
            {
                "domain": "global",
                "reason": "parseError",
                "message": "Parse Error",
            }
        ],
        "code": 400,
        "message": "Parse Error"
    }
}
*/

data class GoogleGeolocateErrorResponseDTO(
    @SerializedName("error") val location: GoogleGeolocateErrorDTO?
)

data class GoogleGeolocateErrorDTO(
    @SerializedName("errors") val errorValues: List<GoogleGeolocateErrorValueDTO>?,
    @SerializedName("code") val code: Int?,
    @SerializedName("message") val message: String?
)

data class GoogleGeolocateErrorValueDTO(
    @SerializedName("domain") val domain: String?,
    @SerializedName("reason") val reason: String?,
    @SerializedName("message") val message: String?
)