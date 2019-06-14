package com.kmadsen.compass.wifilocation.wifiscan

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import com.kylemadsen.core.logger.L
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class WifiScanReceiver(
    private val wifiManager: WifiManager
) {

    fun observeWifiScans(context: Context): Observable<WifiScan> {
        return context.observeWifiScanBroadcast()
            .mergeWith(observeStartWifiScan())
            .switchMap {
                return@switchMap if (it.wifiAccessPoints.isEmpty()) {
                    Observable.empty<WifiScan>()
                } else {
                    Observable.just(it)
                }
            }
    }

    private fun observeStartWifiScan(): Completable {
        return Observable.interval(0, 1, TimeUnit.MINUTES)
            .map {
                val isScanAvailable: Boolean = wifiManager.isWifiEnabled || wifiManager.isScanAlwaysAvailable
                if (isScanAvailable) startScan()
            }
            .ignoreElements()
    }

    /**
     * https://issuetracker.google.com/issues/79906367
     * Adopt new WiFi scanner for Android P
     * https://developer.android.com/guide/topics/connectivity/wifi-rtt
     */
    @Suppress("DEPRECATION")
    private fun startScan() {
        L.i("WIFI SCAN startScan")
        try {
            wifiManager.startScan()
        } catch (throwable: Throwable) {
            L.i(throwable, "WIFI SCAN startScan failed")
        }
    }

    private fun Context.observeWifiScanBroadcast(): Observable<WifiScan> {
        return observe(IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            .map { intent: Intent ->
                L.i("WIFI SCAN received scan")
                return@map toWifiScan(intent)
            }
    }

    private fun toWifiScan(it: Intent): WifiScan {
        var wifiScan = WifiScan.empty
        if (it.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            val success: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            } else {
                true
            }
            wifiScan = if (success) {
                val scanResults: List<ScanResult> = safeGetScanResults()
                fromScanResults(scanResults)
            } else {
                WifiScan(System.currentTimeMillis(), scanError = "EXTRA_RESULTS_UPDATED was false")
            }
        }
        return wifiScan
    }

    private fun safeGetScanResults(): List<ScanResult> {
        return try {
            wifiManager.scanResults
        } catch (exception: Exception) {
            emptyList()
        }
    }

    private fun fromScanResults(scanResults: List<ScanResult>): WifiScan {
        val maxNetworks = 100
        val maxStaleSeconds = 10

        val displayTimeMs: Long = System.currentTimeMillis()
        val currentElapsedTime: Long = SystemClock.elapsedRealtime()

        val accessPoints: List<WifiAccessPoint> = scanResults
            .asSequence()
            .filterNot { it.SSID.endsWith("_nomap") }
            .filter {
                val recordedElapsedTimeMs: Long = TimeUnit.MICROSECONDS.toMillis(it.timestamp)
                val staleSeconds = TimeUnit.MILLISECONDS.toSeconds(currentElapsedTime - recordedElapsedTimeMs)
                staleSeconds < maxStaleSeconds
            }
            .sortedByDescending { it.level }
            .take(maxNetworks)
            .map { scanResult: ScanResult ->
                val recordedElapsedTimeMs: Long = TimeUnit.MICROSECONDS.toMillis(scanResult.timestamp)
                val offsetFromElapsedTime: Long = currentElapsedTime - recordedElapsedTimeMs
                val scanMeasuredAtMs: Long = displayTimeMs - offsetFromElapsedTime
                WifiAccessPoint(
                    ssid = scanResult.SSID,
                    bssid = scanResult.BSSID,
                    rssi = scanResult.level,
                    frequencyMhz = scanResult.frequency,
                    channelWidth = scanResult.getChannelWidth(),
                    measuredAtMs = scanMeasuredAtMs,
                    recordedElapsedTimeMs = recordedElapsedTimeMs
                )
            }
            .toList()

        return WifiScan(displayTimeMs, accessPoints)
    }

    private fun ScanResult.getChannelWidth(): Int? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            channelWidth
        } else {
            null
        }
    }
}