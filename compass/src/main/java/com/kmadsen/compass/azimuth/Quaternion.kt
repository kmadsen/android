package com.kmadsen.compass.azimuth

import kotlin.math.sqrt

data class Quaternion(
    val values: DoubleArray
) {

    constructor(x: Double, y: Double, z: Double, w: Double
    ) : this(doubleArrayOf(x, y, z, w))

    var x: Double
        get() = values[0]
        set(value) { values[0] = value }
    var y: Double
        get() = values[1]
        set(value) { values[1] = value }
    var z: Double
        get() = values[2]
        set(value) { values[2] = value }
    var w: Double
        get() = values[3]
        set(value) { values[3] = value }

    fun length(): Double = sqrt(x*x + y*y + z*z + w*w)

    fun normalize(): Quaternion {
        val length = length()
        if (length != 0.0) {
            this.x /= length
            this.y /= length
            this.z /= length
            this.w /= length
        }
        return this
    }

    fun vector3d(): Vector3d = Vector3d(x, y, z)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quaternion

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
