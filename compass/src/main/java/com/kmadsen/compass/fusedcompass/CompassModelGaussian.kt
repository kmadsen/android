package com.kmadsen.compass.fusedcompass

import android.hardware.SensorManager
import com.google.android.gms.common.internal.Preconditions
import com.kmadsen.compass.location.CompassLocation
import com.kylemadsen.core.logger.L
import java.lang.Math.toDegrees
import kotlin.math.PI

class CompassModelGaussian : ICompassModel {

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var accelerometerProcessor = SignalProcessor(41)
    private var accelerometerProcessorBuffer = SignalProcessor(41)

    private var magnetometerProcessor = SignalProcessor(9)
    private var magnetometerProcessorBuffer = SignalProcessor(9)

    private var currentDegree = 0.0
    private var azimuthInRadians = 0.0

    init {
        val kernel = createGaussianKernel2(1.0, 5)
        println("createGaussianKernel ${kernel.joinToString(",")}")
    }

    override fun onLocationChange(compassLocation: CompassLocation) {
        if (compassLocation.altitudeMeters == null) return
    }

    override fun onMagneticFieldChange(timeNanos: Long, eventValues: FloatArray) {
        magnetometerProcessor.add(timeNanos, eventValues)
    }

    override fun onAccelerationChange(timeNanos: Long, eventValues: FloatArray) {
        accelerometerProcessor.add(timeNanos, eventValues)
    }

    override fun getAzimuthInRadians(): Float {
        if (!accelerometerProcessor.ready() || !magnetometerProcessor.ready()) {
            return azimuthInRadians.toFloat()
        }

        val tempMagnetometerProcessor = magnetometerProcessor
        magnetometerProcessorBuffer.clear()
        magnetometerProcessor = magnetometerProcessorBuffer
        val lastMagnetometer = tempMagnetometerProcessor.create()
        magnetometerProcessorBuffer = tempMagnetometerProcessor

        L.i("getAzimuthInRadians magnetometer x=${lastMagnetometer[0]} y=${lastMagnetometer[1]} z=${lastMagnetometer[2]}")

        val tempAccelerometerProcessor = accelerometerProcessor
        accelerometerProcessorBuffer.clear()
        accelerometerProcessor = accelerometerProcessorBuffer
        val lastAccelerometer = tempAccelerometerProcessor.create()
        accelerometerProcessorBuffer = tempAccelerometerProcessor

        SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
        SensorManager.getOrientation(rotationMatrix, orientation)
        azimuthInRadians = orientation[0] - PI
        currentDegree = toDegrees(azimuthInRadians)
        return azimuthInRadians.toFloat()
    }

    private class SignalProcessor(val kernelSize: Int = 5) {
        val size = 100
        var index = 0

        val kernel = createGaussianKernel2(1.0, kernelSize)

        fun add(timeNanos: Long, eventValues: FloatArray) {
            samplesNanos[index] = timeNanos
            samplesX[index] = eventValues[0]
            samplesY[index] = eventValues[1]
            samplesZ[index] = eventValues[2]
            index++
        }

        fun clear() {
            L.i("clear $index")
            index = 0
        }

        fun create(): FloatArray {
            L.i("create $index")
            Preconditions.checkArgument(ready())

            val values = FloatArray(3) { 0.0f }
            var samplesIndex = index - kernelSize
            for (i in 0 until kernelSize) {
                values[0] += kernel[i] * samplesX[samplesIndex]
                values[1] += kernel[i] * samplesY[samplesIndex]
                values[2] += kernel[i] * samplesZ[samplesIndex]
                samplesIndex++
            }

            L.i("sampled ${values.joinToString(",")}")

            return values
        }

        fun ready(): Boolean {
            return index >= kernelSize
        }

        private val samplesNanos = LongArray(size)
        private val samplesX = FloatArray(size)
        private val samplesY = FloatArray(size)
        private val samplesZ = FloatArray(size)
    }

    companion object {
        private fun createGaussianKernel(sigma: Double): FloatArray {
            Preconditions.checkArgument(sigma >= 1 && sigma < 10)
            val center: Int = (3 * sigma).toInt()
            val sigma2 = sigma * sigma
            return FloatArray(2 * center + 1) { index: Int ->
                val r: Double = (center - index).toDouble()
                Math.exp(-0.5 * (r * r) / sigma2).toFloat()
            }
        }

        private fun createGaussianKernel2(sigma: Double, size: Int): FloatArray {
            Preconditions.checkArgument(sigma >= 1 && sigma < 10 && size % 2 == 1)
            val sqrSigma = sigma * sigma
            return FloatArray(size) { index: Int ->
                val x: Double = index - size / 2.0 + 0.5
                val kernel = Math.exp(x * x / (-2.0 * sqrSigma)) / (Math.sqrt(2.0 * Math.PI) * sigma)
                kernel.toFloat()
            }
        }
    }
}
