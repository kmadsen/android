package com.kmadsen.compass.walking

data class WalkingState(
    val recordedAtMs: Long,
    val lastStepMeasuredAtMs: Long,
    val realtimeNotWalkingSeconds: Double,
    val walkingSteps: Int,
    val realtimeWalkingSeconds: Double,
    val walkingStepsPerSecond: Double)
{
    constructor(recordedAtMs: Long, measuredAtMs: Long, realtimeStaleSeconds: Double)
        : this(recordedAtMs, measuredAtMs, realtimeStaleSeconds,
        0, 0.0, 0.0)
}
