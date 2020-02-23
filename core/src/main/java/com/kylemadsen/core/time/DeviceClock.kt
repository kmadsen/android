package com.kylemadsen.core.time

import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.concurrent.TimeUnit

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun currentTimeNanosInstant(): Instant {
        return currentTimeNanosInstant(System.currentTimeMillis(), SystemClock.elapsedRealtimeNanos())
    }

    /**
     * This will take a regular millisecond device clock. Estimate the nano seconds
     * until the next second. Giving a nano-second current time
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun currentTimeNanosInstant(currentTimeMillis: Long, bootTime: Long): Instant {
        val bootTimeSecs = TimeUnit.NANOSECONDS.toSeconds(bootTime)
        val bootTimeFullSeconds = TimeUnit.SECONDS.toNanos(bootTimeSecs)
        val bootTimeNanos = bootTime - bootTimeFullSeconds

        val deviceTimeNanos = TimeUnit.MILLISECONDS.toNanos(currentTimeMillis)
        val deviceTimeSecs = TimeUnit.NANOSECONDS.toSeconds(deviceTimeNanos)
        val deviceTimeFullSecs = TimeUnit.SECONDS.toNanos(deviceTimeSecs)
        val deviceTimeSecsNanos = deviceTimeNanos - deviceTimeFullSecs

        var alignNanoOffset = deviceTimeSecsNanos - bootTimeNanos
        if (alignNanoOffset < 0) {
            alignNanoOffset += TimeUnit.SECONDS.toNanos(1)
        }

        val currentTimeSecs = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis)
        return Instant.ofEpochSecond(currentTimeSecs, alignNanoOffset)
    }
}
