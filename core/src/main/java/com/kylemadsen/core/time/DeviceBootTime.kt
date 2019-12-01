package com.kylemadsen.core.time

import com.google.gson.annotations.SerializedName

data class DeviceBootTime(
    @SerializedName("bootDeviceTimeMs") val bootDeviceTimeMs: Long,
    @SerializedName("deviceTimeMs") val deviceTimeMs: Long,
    @SerializedName("bootGnssTimeMs") val bootGnssTimeMs: Long?,
    @SerializedName("gnssTimeMs") val gnssTimeMs: Long?,
    @SerializedName("elapsedTimeNanos") val elapsedTimeNanos: Long
) {
    override fun toString(): String {
        return "" +
            "DeviceBootTime(\n" +
            "  bootDeviceTimeMs=$bootDeviceTimeMs,\n" +
            "  deviceTimeMs=$deviceTimeMs,\n" +
            "  bootGnssTimeMs=$bootGnssTimeMs,\n" +
            "  gnssTimeMs=$gnssTimeMs,\n" +
            "  elapsedTimeNanos=$elapsedTimeNanos\n" +
            ")"
    }
}
