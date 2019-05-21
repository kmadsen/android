package com.kylemadsen.core

import android.view.Choreographer
import com.kylemadsen.core.logger.L
import io.reactivex.Completable
import java.util.concurrent.TimeUnit

class FpsChoreographer : Choreographer.FrameCallback {
    var isAttached: Boolean = false

    var updateFrequencyMillis = 1000L
    var lastUpdateTimeNanos = 0L
    var lastUpdateFrameCount = 0
    var currentFramesPerSecond = 0.0

    fun attach(): Completable {
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

        L.i("doUpdate currentFramesPerSecond=$currentFramesPerSecond")
    }
}