package com.kmadsen.compass.walking

data class WalkingStep (
    val recordedAtMilliseconds: Long,
    val recordedWalkingSteps: Int,
    val recordedStepsPerSecond: Double
)
