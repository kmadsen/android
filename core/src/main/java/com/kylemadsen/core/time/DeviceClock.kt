package com.kylemadsen.core.time

import android.content.SharedPreferences
import android.os.Build
import android.os.SystemClock
import com.google.gson.Gson
import com.kylemadsen.core.logger.L
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object DeviceClock {
    private lateinit var clockPreferences: SharedPreferences
    private var lastDeviceBootInstant: DeviceBootInstant? = null
    private lateinit var deviceBootInstant: DeviceBootInstant

    fun initialize(gson: Gson, clockPreferences: SharedPreferences) {
        this.clockPreferences = clockPreferences
        val savedInstant = clockPreferences.getString("boot_time_instant", null)
        lastDeviceBootInstant = gson.fromJson(savedInstant, DeviceBootInstant::class.java)
        val gnssTimeMs = gnssMillis()
        val deviceTimeMs = System.currentTimeMillis()
        val launchTimeNanos = SystemClock.elapsedRealtimeNanos()
        deviceBootInstant = DeviceBootInstant(
            bootDeviceTimeMs = deviceTimeMs - nanosToMilliseconds(launchTimeNanos),
            deviceTimeMs = deviceTimeMs,
            bootGnssTimeMs = gnssTimeMs?.minus(nanosToMilliseconds(launchTimeNanos)),
            gnssTimeMs = gnssTimeMs,
            elapsedTimeNanos = launchTimeNanos
        )
        val savedBootDeviceTimeMs = lastDeviceBootInstant?.bootDeviceTimeMs ?: 0
        if (abs(deviceBootInstant.bootDeviceTimeMs - savedBootDeviceTimeMs) > 1) {
            L.i("Save new boot time:\n$deviceBootInstant")
            clockPreferences.edit()
                .putString("boot_time_instant", gson.toJson(deviceBootInstant))
                .apply()
        } else {
            L.i("Start with old boot time:\n$lastDeviceBootInstant")
        }
    }

    private fun nanosToMilliseconds(nanos: Long): Long = TimeUnit.NANOSECONDS.toMillis(nanos)

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

    fun getClockDriftInfo(): String {
        val lastInstant = lastDeviceBootInstant
        return if (lastInstant != null) {
            "Last device boot info: $lastInstant\n" +
                "Current device boot info: $deviceBootInstant\n" +
                "Device clock boot time difference: ${deviceBootInstant.bootDeviceTimeMs.minus(lastInstant.bootDeviceTimeMs).millisToSeconds()}s\n" +
                "Gnss clock boot time difference: ${deviceBootInstant.bootGnssTimeMs?.minus(lastInstant.bootGnssTimeMs)?.millisToSeconds()}s\n" +
                "Device clock recording time difference: ${deviceBootInstant.deviceTimeMs.minus(lastInstant.deviceTimeMs).millisToSeconds()}s\n" +
                "Gnss clock recording time difference: ${deviceBootInstant.gnssTimeMs?.minus(lastInstant.gnssTimeMs)?.millisToSeconds()}s\n" +
                "Nano time difference: ${deviceBootInstant.elapsedTimeNanos.minus(lastInstant.elapsedTimeNanos).nanosToSeconds()}s\n"
        } else {
            "This is the first application launch:\n" +
                "$deviceBootInstant"
        }
    }
}

private fun Long?.minus(other: Long?): Long? {
    return if (this == null || other == null) null
    else this - other
}
