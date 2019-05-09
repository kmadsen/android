package com.kmadsen.compass.sensors

import com.kylemadsen.core.logger.L
import io.reactivex.Flowable

fun Flowable<Measure3d>.lowPassFilter(alpha: Float): Flowable<Measure3d> {
    return scan { previous: Measure3d, current: Measure3d ->
        val x = previous.x + alpha * (current.x - previous.x)
        System.out.println("$x = ${previous.x} + $alpha * (${current.x} - ${previous.x}")
        val y = previous.y + alpha * (current.y - previous.y)
        val z = previous.z + alpha * (current.z - previous.z)
        val result = Measure3d(x, y, z, current)
        System.out.println("what $result")
        result
    }
}
