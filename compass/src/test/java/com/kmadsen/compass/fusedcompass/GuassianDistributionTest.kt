package com.kmadsen.compass.fusedcompass

import org.junit.Assert.assertEquals
import org.junit.Test

class GuassianDistributionTest {

    @Test
    fun shouldIntegrateToOne() {
        var sum = 0.0
        for (i in 0 .. 10) {
            val x = i.toDouble()
            val result = guassian(x, 5.0, 2.0)
            sum += result
        }

        assertEquals(1.0, sum, 0.01)
    }

    @Test
    fun shouldCalculate3x3Determinant() {
        val testMatrix: Array<DoubleArray> = Array(3) { when(it) {
            0 -> doubleArrayOf(6.0, 1.0, 1.0)
            1 -> doubleArrayOf(4.0, -2.0, 5.0)
            2 -> doubleArrayOf(2.0, 8.0, 7.0)
            else -> throw IllegalArgumentException("Must be 0..2")
        }}
        assertEquals(-306.0, testMatrix.determinant(), 0.0)

        multivariateGaussian(FloatArray(1), CovariantMatrix(testMatrix))
    }

    @Test
    fun shouldCalculate4x4Determinant() {
        val testMatrix: Array<DoubleArray> = Array(4) { when(it) {
            0 -> doubleArrayOf(5.0, -7.0, 2.0, 2.0)
            1 -> doubleArrayOf(0.0, 3.0, 0.0, -4.0)
            2 -> doubleArrayOf(-5.0, -8.0, 0.0, 3.0)
            3 -> doubleArrayOf(0.0, 5.0, 0.0, -6.0)
            else -> throw IllegalArgumentException("Must be 0..3")
        }}
        assertEquals(20.0, testMatrix.determinant(), 0.0)

        multivariateGaussian(FloatArray(1), CovariantMatrix(testMatrix))
    }

    @Test
    fun shouldCalculate5x5Determinant() {
        val testMatrix: Array<DoubleArray> = Array(5) { when(it) {
            0 -> doubleArrayOf(0.0, 6.0, -2.0, -1.0, 5.0)
            1 -> doubleArrayOf(0.0, 0.0, 0.0, -9.0, -7.0)
            2 -> doubleArrayOf(0.0, 15.0, 35.0, 0.0, 0.0)
            3 -> doubleArrayOf(0.0, -1.0, -11.0, -2.0, 1.0)
            4 -> doubleArrayOf(-2.0, -2.0, 3.0, 0.0, -2.0)
            else -> throw IllegalArgumentException("Must be 0..4")
        }}
        assertEquals(2480.0, testMatrix.determinant(), 0.0)

        multivariateGaussian(FloatArray(1), CovariantMatrix(testMatrix))
    }

    @Test
    fun shouldCalculateGaussian() {
        assertEquals(0.121, guassian(8.0, 10.0, 2.0), 0.001)
    }
}
