package com.kmadsen.compass.azimuth

import kotlin.math.abs
import kotlin.math.sqrt


data class Vector3d(
    val values: DoubleArray
) {
    constructor(x: Double, y: Double, z: Double)
        : this(doubleArrayOf(x, y, z))

    constructor(values: FloatArray) : this(
        doubleArrayOf(values[0].toDouble(), values[1].toDouble(), values[2].toDouble())
    )

    var x: Double
        get() = values[0]
        set(value) { values[0] = value }
    var y: Double
        get() = values[1]
        set(value) { values[1] = value }
    var z: Double
        get() = values[2]
        set(value) { values[2] = value }

    fun length(): Double = sqrt(x*x + y*y + z*z)

    fun normalize(): Vector3d {
        val length = length()
        if (length != 0.0) {
            this.x /= length
            this.y /= length
            this.z /= length
        }
        return this
    }

    fun multiply(magnitude: Double): Vector3d {
        this.x *= magnitude
        this.y *= magnitude
        this.z *= magnitude
        return this
    }

    fun add(v: Vector3d): Vector3d {
        this.x += v.x
        this.y += v.y
        this.z += v.z
        return this
    }

    fun subtract(v: Vector3d): Vector3d  {
        this.x -= v.x
        this.y -= v.y
        this.z -= v.z
        return this
    }

    fun abs(): Vector3d  {
        this.x = abs(x)
        this.y = abs(y)
        this.z = abs(z)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector3d

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}

fun dot(v1: Vector3d, v2: Vector3d): Double {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
}

fun cross(v1: Vector3d, v2: Vector3d): Vector3d {
    return Vector3d(
        v1.y * v2.z - v1.z * v2.y,
        v2.x * v1.z - v2.z * v1.x,
        v1.x * v2.y - v1.y * v2.x)
}
