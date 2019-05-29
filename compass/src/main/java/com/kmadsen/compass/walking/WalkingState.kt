package com.kmadsen.compass.walking

data class WalkingState(
        val recordedAtMilliseconds: Long,
        val measuredAtMilliseconds: Long,
        val walkingStaleSeconds: Double,
        val walkingSteps: Int,
        val walkingSeconds: Double,
        val stepsPerSecond: Double,
        val pace: Double
)
