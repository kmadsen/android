package com.kmadsen.compass.wifilocation.wifiscan

data class WifiScan(
    val recordedAtMs: Long = 0,
    val wifiAccessPoints: List<WifiAccessPoint> = emptyList(),
    val scanError: String? = null
) {

    companion object {
        val empty: WifiScan = WifiScan()
    }
}

data class WifiAccessPoint(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequencyMhz: Int,
    val channelWidth: Int?,
    val measuredAtMs: Long,
    val recordedElapsedTimeMs: Long
)
