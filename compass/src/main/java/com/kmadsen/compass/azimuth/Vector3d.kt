package com.kmadsen.compass.azimuth


data class Vector3d(
    val values: DoubleArray
) {
    constructor(x: Double, y: Double, z: Double)
        : this(doubleArrayOf(x, y, z))

    var x: Double
        get() = values[0]
        set(value) { values[0] = value }
    var y: Double
        get() = values[1]
        set(value) { values[1] = value }
    var z: Double
        get() = values[2]
        set(value) { values[2] = value }

    fun length(): Double {
        return Math.sqrt(x*x + y*y + z*z)
    }

    fun normalize(): Vector3d {
        val length = length()
        if (length != 0.0) {
            this.x /= length
            this.y /= length
            this.z /= length
        }
        return this
    }

    fun multiply(magnitude: Double) {
        this.x *= magnitude
        this.y *= magnitude
        this.z *= magnitude
    }

    fun add(v: Vector3d) {
        this.x += v.x
        this.y += v.y
        this.z += v.z
    }

    fun subtract(v: Vector3d): Vector3d  {
        this.x -= v.x
        this.y -= v.y
        this.z -= v.z
        return this
    }

    fun abs(): Vector3d  {
        this.x = Math.abs(x)
        this.y = Math.abs(y)
        this.z = Math.abs(z)
        return this
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
