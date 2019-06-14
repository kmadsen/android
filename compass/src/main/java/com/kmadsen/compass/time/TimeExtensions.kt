package com.kmadsen.compass.time

import java.util.concurrent.TimeUnit

fun Long.nanosToSeconds(): Double = TimeUnit.NANOSECONDS.toMillis(this) / TimeUnit.SECONDS.toMillis(1).toDouble()

fun Long.millisToSeconds(): Double = this / TimeUnit.SECONDS.toMillis(1).toDouble()

fun toMillisecondPeriod(framesPerSecond: Long): Long = TimeUnit.SECONDS.toMillis(1) / framesPerSecond
