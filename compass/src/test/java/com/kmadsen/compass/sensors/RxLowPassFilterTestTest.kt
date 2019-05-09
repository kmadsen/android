package com.kmadsen.compass.sensors

import io.reactivex.Flowable
import io.reactivex.subscribers.TestSubscriber
import org.junit.Test
import java.util.concurrent.TimeUnit

class RxLowPassFilterTestTest {

    @Test
    fun shouldStaySmoothWithZeros() {
        val rateOfChange: TestSubscriber<Measure3d> = Flowable.just(
                testMeasure3d(0.0f, 0.0f,0.0f, 0L),
                testMeasure3d(0.0f, 0.0f,0.0f, 1L),
                testMeasure3d(0.0f, 0.0f,0.0f, 2L)
        ).lowPassFilter(0.1f).test()

        rateOfChange.assertValues(
                testMeasure3d(0.0f, 0.0f,0.0f, 0L),
                testMeasure3d(0.0f, 0.0f,0.0f, 1L),
                testMeasure3d(0.0f, 0.0f,0.0f, 2L))
    }

    @Test
    fun shouldStaySmoothForLinearLine() {
        val rateOfChange: TestSubscriber<Measure3d> = Flowable.just(
                testMeasure3d(0.10f, 1.00f,-2.0f, 0L),
                testMeasure3d(0.10f, 1.00f,-2.0f, 0L),
                testMeasure3d(0.12f, 1.10f,-2.5f, 1L),
                testMeasure3d(0.12f, 1.10f,-2.5f, 1L),
                testMeasure3d(0.12f, 1.10f,-2.5f, 1L),
                testMeasure3d(0.12f, 1.10f,-2.5f, 1L),
                testMeasure3d(0.12f, 1.10f,-2.5f, 1L)
        ).lowPassFilter(0.2f).doOnNext { } .test()

        rateOfChange.assertValueAt(5) { it.x == 0.12f }
//        rateOfChange.assertValues(
//                testMeasure3d(0.10f, 1.00f,-2.0f, 0L),
//                testMeasure3d(0.10f, 1.00f,-2.5f, 1L),
//                testMeasure3d(0.10f, 1.00f,-2.0f, 2L))
    }

// alpha=0.3    x=0.114, y=1.007, z=-2.35
// alpha=0.003  x=0.11994, y=1.00997, z=-2.4985
// alpha=0.9    x=0.102, y=1.001, z=-2.05
    private fun testMeasure3d(x: Float, y: Float, z: Float, millis: Long): Measure3d {
        return Measure3d(x, y, z,
                TimeUnit.MILLISECONDS.toNanos(millis),
                TimeUnit.MILLISECONDS.toNanos(millis),
                0)
    }
}
