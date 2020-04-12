package com.kylemadsen.core.time

import java.util.concurrent.TimeUnit
import kotlin.math.ceil

val secondInMillis = TimeUnit.SECONDS.toMillis(1)
val secondInMicros = TimeUnit.SECONDS.toMicros(1)
//val millisPerSecond = TimeUnit.SECONDS.toMillis(1)

fun Long.nanosToSeconds(): Double {
    return TimeUnit.NANOSECONDS.toMillis(this) / secondInMillis.toDouble()
}
fun Long.millisToSeconds(): Double = this / secondInMillis.toDouble()

fun toMillisecondPeriod(framesPerSecond: Long): Long {
    return secondInMillis / framesPerSecond
}

fun toSamplesPerSecond(minDelayUs: Int): Int? {
    return if (minDelayUs == 0) {
        null
    } else {
        ceil(secondInMicros.toDouble() / minDelayUs).toInt()
    }
}

fun toSamplingPeriodMicros(signalsPerSecond: Int): Int {
    return 1000000 / signalsPerSecond
}