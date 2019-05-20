package com.kmadsen.compass.azimuth

import kotlin.math.PI

/**
 * Azimuth is a representation for directionality.
 *
 * Degree values are between 0-360. 0 is north. 90 is East. 180 is South. 270 is West.
 */
data class Azimuth(
        val recordedAtMilliseconds: Long,
        val northDirectionRadians: Double,
        val deviceDirectionRadians: Double
)

fun Double.toDegrees(): Double {
    return this * 180.0 / PI
}
