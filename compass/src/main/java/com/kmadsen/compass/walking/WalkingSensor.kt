package com.kmadsen.compass.walking

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.SystemClock
import android.util.TimeUtils
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kylemadsen.core.logger.L
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import kotlin.math.max
import kotlin.math.pow

class WalkingSensor(
        private val androidSensors: AndroidSensors,
        private val locationRepository: LocationRepository
) {

    private var stepCountSinceBoot: Int = 0
    private var stepElapsedRealtimeNanos: Long = 0L
    private var stepCountSinceWalkingStart: Int = 0
    private var walkingStartElapsedRealtimeNanos: Long = 0L
    private var walkingStaleSeconds: Double = 0.0
    private var currentStepsPerSecond = 0.0

    fun observeWalkingState(): Observable<WalkingState> {
        return locationRepository.observeWalkingState().mergeWith(attachSensorUpdates())
    }

    private fun attachSensorUpdates(): Completable {
        return Completable.mergeArray(
            attachStepCounterUpdates(),
            attachStepDetector(),
            attachWalkingStateUpdates()
        )
    }

    private fun attachStepCounterUpdates(): Completable {
        return androidSensors.observeRawSensor(Sensor.TYPE_STEP_COUNTER)
                .doOnNext {
                    stepCountSinceBoot = it.values[0].toInt()
                    stepElapsedRealtimeNanos = it.timestamp
                    if (stepCountSinceWalkingStart == 0 || stepCountSinceBoot == stepCountSinceWalkingStart) {
                        stepCountSinceWalkingStart = stepCountSinceBoot
                        walkingStartElapsedRealtimeNanos = stepElapsedRealtimeNanos
                    }
                }
                .ignoreElements()
    }

    private fun attachStepDetector(): Completable {
        return androidSensors.observeRawSensor(Sensor.TYPE_STEP_DETECTOR)
            .doOnNext {
                // TODO any reason to care about this?
            }
            .ignoreElements()
    }

    private fun attachWalkingStateUpdates(): Completable {
        return Observable.interval(1, toMillisecondPeriod(10), TimeUnit.MILLISECONDS)
            .mergeWith(attachStepDetector())
            .map {
                val recordedAtNanos = SystemClock.elapsedRealtimeNanos()
                walkingStaleSeconds = (recordedAtNanos - stepElapsedRealtimeNanos) / TimeUnit.SECONDS.toNanos(1).toDouble()
                if (walkingStaleSeconds > 10) {
                    stepCountSinceWalkingStart = 0
                    walkingStartElapsedRealtimeNanos = 0
                    WalkingState(
                        SystemClock.elapsedRealtime(),
                        TimeUnit.NANOSECONDS.toMillis(stepElapsedRealtimeNanos),
                        walkingStaleSeconds,
                        0,
                        0.0,
                        0.0,
                        currentStepsPerSecond
                    )
                } else {
                    val walkingSteps = stepCountSinceBoot - stepCountSinceWalkingStart
                    val walkingSeconds = TimeUnit.NANOSECONDS.toMillis(stepElapsedRealtimeNanos - walkingStartElapsedRealtimeNanos) / TimeUnit.SECONDS.toMillis(1).toDouble()
                    val realtimeWalkingSeconds = (recordedAtNanos - walkingStartElapsedRealtimeNanos) / TimeUnit.SECONDS.toNanos(1).toDouble()
                    val walkingStepsPerSecond = if (realtimeWalkingSeconds < 0.001) 0.0 else walkingSteps / realtimeWalkingSeconds
                    WalkingState(
                        SystemClock.elapsedRealtime(),
                        TimeUnit.NANOSECONDS.toMillis(stepElapsedRealtimeNanos),
                        walkingStaleSeconds,
                        walkingSteps,
                        walkingSeconds,
                        walkingStepsPerSecond,
                        currentStepsPerSecond
                    )
                }
            }
            .doOnNext { locationRepository.updateWalkingState(it) }
            .throttleLatest(1, TimeUnit.SECONDS)
            .scan(WalkingStep(0, 0, 0.0)) { previous: WalkingStep, walkingState: WalkingState ->
                val timeDeltaMillis = (walkingState.measuredAtMilliseconds - previous.recordedAtMilliseconds)
                currentStepsPerSecond = if (previous.recordedAtMilliseconds != 0L && timeDeltaMillis > 0L) {
                    val numSteps = walkingState.walkingSteps - previous.recordedWalkingSteps
                    numSteps * TimeUnit.SECONDS.toMillis(1) / timeDeltaMillis.toDouble()
                } else { 0.0 }
                WalkingStep(walkingState.measuredAtMilliseconds, walkingState.walkingSteps, currentStepsPerSecond)
            }
            .ignoreElements()
    }
}

fun toMillisecondPeriod(framesPerSecond: Long): Long = TimeUnit.SECONDS.toMillis(1) / framesPerSecond
