package com.kylemadsen.core.time

import android.os.Build
import android.os.SystemClock

object DeviceClock {
    private lateinit var deviceBootTimeProvider: DeviceBootTimeProvider

    fun initialize(deviceBootTimeProvider: DeviceBootTimeProvider) {
        this.deviceBootTimeProvider = deviceBootTimeProvider
    }

    /** This is manual by the user for now. Should be detected by the clock system **/
    fun resetClockDriftInfo() {
        deviceBootTimeProvider.resetDeviceBootTime()
    }

    fun getClockDriftInfo(): String {
        return DeviceBootTimeProvider.getClockDriftInfo()
    }

    fun elapsedMillis() = SystemClock.elapsedRealtime()
    fun elapsedNanos() = SystemClock.elapsedRealtimeNanos()
    fun displayMillis() = System.currentTimeMillis()
    fun gnssMillis(): Long? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            SystemClock.currentGnssTimeClock().instant().toEpochMilli()
        } else null
    }

    fun delta(elapsedMillis: Long): Long {
        return elapsedMillis() - elapsedMillis
    }
}
