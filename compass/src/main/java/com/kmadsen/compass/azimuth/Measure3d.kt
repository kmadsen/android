package com.kmadsen.compass.azimuth

data class Measure3d(
        val values: FloatArray,
        var measuredAtNanos: Long,
        var recordedAtNanos: Long,
        var accuracy: Int?
) {
    constructor()
    : this(0f, 0f, 0f, 0L, 0L, null)

    constructor(x: Float, y: Float, z: Float,
                measuredAtNanos: Long, recordedAtNanos: Long, accuracy: Int?
    ) : this(floatArrayOf(x, y, z), measuredAtNanos, recordedAtNanos, accuracy)

    constructor(x: Float, y: Float, z: Float,
                other: Measure3d
    ) : this(x, y, z, other.measuredAtNanos, other.recordedAtNanos, other.accuracy)

    var x: Float
        get() = values[0]
        set(value) { values[0] = value }
    var y: Float
        get() = values[1]
        set(value) { values[1] = value }
    var z: Float
        get() = values[2]
        set(value) { values[2] = value }

    override fun toString(): String {
        return "Measure3d(x=${values[0]}, y=${values[1]}, z=${values[2]}," +
                " measuredAtNanos=$measuredAtNanos," +
                " recordedAtNanos=$recordedAtNanos," +
                " accuracy=$accuracy)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Measure3d

        if (!values.contentEquals(other.values)) return false
        if (measuredAtNanos != other.measuredAtNanos) return false
        if (recordedAtNanos != other.recordedAtNanos) return false
        if (accuracy != other.accuracy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.contentHashCode()
        result = 31 * result + measuredAtNanos.hashCode()
        result = 31 * result + recordedAtNanos.hashCode()
        result = 31 * result + (accuracy ?: 0)
        return result
    }
}