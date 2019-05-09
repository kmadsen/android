package com.kmadsen.compass.fusedcompass

import java.lang.IllegalStateException
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.exp
import kotlin.text.StringBuilder

typealias Matrix = Array<DoubleArray>

/**
 * Guassian kernel which gives a probability of a scalar value, x
 */
fun guassian(x: Double, mean: Double, variance: Double): Double {
    val varianceSquared = variance * variance
    val leftHandSide = pow(2.0 * PI * varianceSquared, -0.5)
    val rightHandSide = exp(-0.5 * pow(x - mean, 2.0) / varianceSquared)
    return leftHandSide * rightHandSide
}

fun multivariateGaussian(xVector: FloatArray, covariantMatrix: CovariantMatrix) {

    covariantMatrix.print()

}

/**
 * Determinant of an NxN matrix
 */
fun Matrix.determinant(): Double {
    if (size < 1) {
        throw IllegalStateException("n=$size should never be less than 1")
    } else if (this[0].size != size) {
        throw IllegalStateException("Only square matrices allowed")
    } else if (size == 1) {
        return this[0][0]
    } else if (size == 2) {
        return this[0][0] * this[1][1] - this[1][0] * this[0][1]
    } else {
        var sign = 1
        var det = 0.0
        val subMatrix = Array(size-1) { DoubleArray(size-1) }
        for (x in 0 until size) {
            for ((row, i) in (1 until size).withIndex()) {
                var col = 0
                for (j in 0 until size) {
                    if (j == x)
                        continue
                    subMatrix[row][col] = this[i][j]
                    col++
                }
            }
            det += sign * this[0][x] * subMatrix.determinant()
            sign = -sign
        }
        return det
    }
}

class CovariantMatrix {
    private val size: Int
    private val covariantMatrix: Matrix

    constructor(matrix: Matrix) {
        this.size = matrix.size
        this.covariantMatrix = matrix
    }

    constructor(size: Int) {
        this.size = size
        this.covariantMatrix = Array(this.size) {DoubleArray(this.size)}
    }

    fun print() {
        val stringBuilder = StringBuilder()
        covariantMatrix.forEachIndexed { _, ints ->
            stringBuilder.append("|")
            stringBuilder.append(ints.joinToString(" "))
            stringBuilder.append("|\n")
        }
        System.out.println(stringBuilder.toString())
    }
}