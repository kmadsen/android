package com.kylemadsen.core.time

import android.content.SharedPreferences
import android.os.SystemClock
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class DeviceBootTimeProvider (
    private val gson: Gson,
    private val clockPreferences: SharedPreferences
) {
    init {
        val savedInstant = clockPreferences.getString("boot_time_instant", null)
        lastDeviceBootTime = gson.fromJson(savedInstant, DeviceBootTime::class.java)
        deviceBootTime = createDeviceBootInstant()
        if (lastDeviceBootTime == null) {
            saveDeviceBootTime(deviceBootTime)
        }
    }

    fun resetDeviceBootTime() {
        deviceBootTime = createDeviceBootInstant()
        saveDeviceBootTime(deviceBootTime)
    }

    private fun saveDeviceBootTime(deviceBootTime: DeviceBootTime) {
        clockPreferences.edit()
            .putString("boot_time_instant", gson.toJson(deviceBootTime))
            .apply()
        lastDeviceBootTime = deviceBootTime
    }

    private fun createDeviceBootInstant(): DeviceBootTime {
        val gnssTimeMs = DeviceClock.gnssMillis()
        val deviceTimeMs = System.currentTimeMillis()
        val launchTimeNanos = SystemClock.elapsedRealtimeNanos()
        return DeviceBootTime(
            bootDeviceTimeMs = deviceTimeMs - nanosToMilliseconds(launchTimeNanos),
            deviceTimeMs = deviceTimeMs,
            bootGnssTimeMs = gnssTimeMs?.minus(nanosToMilliseconds(launchTimeNanos)),
            gnssTimeMs = gnssTimeMs,
            elapsedTimeNanos = launchTimeNanos
        )
    }

    private fun nanosToMilliseconds(nanos: Long): Long = TimeUnit.NANOSECONDS.toMillis(nanos)

    companion object {
        private var lastDeviceBootTime: DeviceBootTime? = null
        private lateinit var deviceBootTime: DeviceBootTime

        fun getClockDriftInfo(): String {
            val lastInstant = lastDeviceBootTime
            return if (lastInstant != null) {
                "Last device boot info: $lastInstant\n" +
                    "Current device boot info: $deviceBootTime\n" +
                    "Device clock boot time difference: ${deviceBootTime.bootDeviceTimeMs.minus(lastInstant.bootDeviceTimeMs).millisToSeconds()}s\n" +
                    "Gnss clock boot time difference: ${deviceBootTime.bootGnssTimeMs?.minus(lastInstant.bootGnssTimeMs)?.millisToSeconds()}s\n" +
                    "Device clock recording time difference: ${deviceBootTime.deviceTimeMs.minus(lastInstant.deviceTimeMs).millisToSeconds()}s\n" +
                    "Gnss clock recording time difference: ${deviceBootTime.gnssTimeMs?.minus(lastInstant.gnssTimeMs)?.millisToSeconds()}s\n" +
                    "Nano time difference: ${deviceBootTime.elapsedTimeNanos.minus(lastInstant.elapsedTimeNanos).nanosToSeconds()}s\n"
            } else {
                "This is the first application launch:\n" +
                    "$deviceBootTime"
            }
        }
    }
}

private fun Long?.minus(other: Long?): Long? {
    return if (this == null || other == null) null
    else this - other
}
