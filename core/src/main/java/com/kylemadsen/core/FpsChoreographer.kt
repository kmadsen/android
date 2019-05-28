package com.kylemadsen.core

import android.view.Choreographer
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class FpsChoreographer : Choreographer.FrameCallback {
    private var isAttached: Boolean = false
    private var lastUpdateTimeNanos = 0L
    private var lastUpdateFrameCount = 0
    private var currentFramesPerSecond = 0.0

    private val currentFpsRelay: BehaviorRelay<Double> = BehaviorRelay.create()
    private val updateFrequencyMillis = 1000L

    fun observeFps(): Observable<Double> {
        return currentFpsRelay.mergeWith(attach())
    }

    private fun attach(): Completable {
        return Completable.create {
            isAttached = true
            Choreographer.getInstance().postFrameCallback(this)
            it.setCancellable { isAttached = false }
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (isAttached.not()) return

        val deltaSinceUpdateMillis = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos - lastUpdateTimeNanos)
        if (deltaSinceUpdateMillis > updateFrequencyMillis) {
            doUpdate(frameTimeNanos)
        } else {
            lastUpdateFrameCount++
        }

        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun doUpdate(frameTimeNanos: Long) {
        currentFramesPerSecond = if (lastUpdateFrameCount > 0 && lastUpdateTimeNanos > 0) {
            val deltaPerSecond = (frameTimeNanos - lastUpdateTimeNanos) / TimeUnit.SECONDS.toNanos(1).toDouble()
            lastUpdateFrameCount.toDouble() * deltaPerSecond
        } else {
            0.0
        }
        lastUpdateTimeNanos = frameTimeNanos
        lastUpdateFrameCount = 0
    }
}
