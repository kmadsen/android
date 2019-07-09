package com.kmadsen.compass.walking

import android.hardware.Sensor
import android.os.SystemClock
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.time.nanosToSeconds
import com.kmadsen.compass.time.toMillisecondPeriod
import com.kylemadsen.core.logger.L
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class WalkingStateSensor(
    private val androidSensors: AndroidSensors,
    private val locationRepository: LocationRepository
) {

    private var stepCountSinceDeviceBoot: Int = 0
    private var lastStepMeasuredAtNanos: Long = 0L
    private var stepCountSinceWalkingStart: Int = 0
    private var walkingStartMeasuredAtNanos: Long = 0L
    private var realtimeNotWalkingSeconds: Double = 0.0
    private var walkingStepsPerSecond = 0.0

    fun observeWalkingState(): Observable<WalkingState> {
        return locationRepository.observeWalkingState().mergeWith(attachSensorUpdates())
    }

    private fun attachSensorUpdates(): Completable {
        return Completable.mergeArray(
            attachStepCounterUpdates(),
            attachWalkingStateUpdates()
        )
    }

    private fun attachStepCounterUpdates(): Completable {
        return androidSensors.observeRawSensor(Sensor.TYPE_STEP_COUNTER)
            .doOnNext {
                stepCountSinceDeviceBoot = it.values[0].toInt()
                lastStepMeasuredAtNanos = it.timestamp
                if (stepCountSinceWalkingStart == 0 || stepCountSinceDeviceBoot == stepCountSinceWalkingStart) {
                    stepCountSinceWalkingStart = stepCountSinceDeviceBoot
                    walkingStartMeasuredAtNanos = lastStepMeasuredAtNanos
                }
            }
            .ignoreElements()
    }

    private fun attachWalkingStateUpdates(): Completable {
        return Observable.interval(1, toMillisecondPeriod(10), TimeUnit.MILLISECONDS)
            .map {
                val recordedAtNanos = SystemClock.elapsedRealtimeNanos()
                realtimeNotWalkingSeconds = (recordedAtNanos - lastStepMeasuredAtNanos).nanosToSeconds()
                if (realtimeNotWalkingSeconds > 5.0) {
                    stepCountSinceWalkingStart = 0
                    walkingStartMeasuredAtNanos = 0
                    WalkingState(
                        SystemClock.elapsedRealtime(),
                        TimeUnit.NANOSECONDS.toMillis(lastStepMeasuredAtNanos),
                        realtimeNotWalkingSeconds)
                } else {
                    val walkingSteps = stepCountSinceDeviceBoot - stepCountSinceWalkingStart
                    val realtimeWalkingSeconds = (recordedAtNanos - walkingStartMeasuredAtNanos).nanosToSeconds()
                    WalkingState(
                        SystemClock.elapsedRealtime(),
                        TimeUnit.NANOSECONDS.toMillis(lastStepMeasuredAtNanos),
                        realtimeNotWalkingSeconds,
                        walkingSteps,
                        realtimeWalkingSeconds,
                        walkingStepsPerSecond
                    )
                }
            }
            .doOnNext { locationRepository.updateWalkingState(it) }
            .throttleLatest(1, TimeUnit.SECONDS)
            .scan(WalkingStep(0, 0, 0.0))
            { previous: WalkingStep, walkingState: WalkingState ->
                val timeDeltaMillis = (walkingState.lastStepMeasuredAtMs - previous.recordedAtMilliseconds)
                walkingStepsPerSecond = if (previous.recordedAtMilliseconds != 0L && timeDeltaMillis > 0L) {
                    val numSteps = walkingState.walkingSteps - previous.recordedWalkingSteps
                    numSteps * TimeUnit.SECONDS.toMillis(1) / timeDeltaMillis.toDouble()
                } else { 0.0 }
                WalkingStep(walkingState.lastStepMeasuredAtMs, walkingState.walkingSteps, walkingStepsPerSecond)
            }
            .ignoreElements()
    }
}
