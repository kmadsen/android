package com.kmadsen.compass.sensors

data class Measure3d(
        val x: Float,
        val y: Float,
        val z: Float,
        val measuredAtNanos: Long,
        val recordedAtNanos: Long,
        val accuracy: Int
) {
    constructor(x: Float, y: Float, z: Float,
                other: Measure3d
    ) : this(x, y, z, other.measuredAtNanos, other.recordedAtNanos, other.accuracy)

    override fun toString(): String {
        return "Measure3d(x=$x, y=$y, z=$z, measuredAtNanos=$measuredAtNanos, recordedAtNanos=$recordedAtNanos, accuracy=$accuracy)"
    }


}