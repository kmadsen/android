package com.kylemadsen.core.time

import android.os.SystemClock

object DeviceClock {
    fun elapsedMillis() = SystemClock.elapsedRealtime()
    fun elapsedNanos() = SystemClock.elapsedRealtimeNanos()
    fun displayMillis() = System.currentTimeMillis()

    fun delta(elapsedMillis: Long): Long {
        return elapsedMillis() - elapsedMillis
    }
}
