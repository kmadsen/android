package com.kmadsen.compass.azimuth

import com.jakewharton.rxrelay2.BehaviorRelay
import com.kmadsen.compass.location.LocationRepository
import com.kylemadsen.core.time.DeviceClock
import io.reactivex.Observable

class DeviceDirectionSensor(
    private val azimuthSensor: AzimuthSensor,
    private val turnSensor: TurnSensor,
    private val locationRepository: LocationRepository
) {
    private val calibratedRelay: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)
    private var calibratedDirectionDegrees = 0f

    fun observeHorizontalDirection(): Observable<Measure1d> {
        return calibratedRelay.distinctUntilChanged()
            .switchMap { isCalibrated ->
                if (!isCalibrated) {
                    observeCalibratingDirection()
                } else {
                    turnSensor.observeTurn(calibratedDirectionDegrees.toDouble())
                        .mergeWith(azimuthSensor.observeAzimuth().ignoreElements())
                }
            }
            .doOnNext {
                locationRepository.updateDeviceDirection(it)
            }
    }

    private fun observeCalibratingDirection(): Observable<Measure1d> {
        var startTimeMs: Long? = null
        return azimuthSensor.observeAzimuth()
            .doOnNext { azimuthMeasure1d ->
                if (startTimeMs == null && azimuthMeasure1d.value != null) {
                    startTimeMs = DeviceClock.elapsedMillis()
                } else if (azimuthMeasure1d.hasTimeElapsed(startTimeMs, 1000)) {
                    calibratedDirectionDegrees = azimuthMeasure1d.value!!
                    calibratedRelay.accept(true)
                }
            }
    }

    private fun Measure1d.hasTimeElapsed(startTimeMs: Long?, thresholdMs: Long): Boolean {
        return startTimeMs != null && value != null && DeviceClock.delta(startTimeMs) > thresholdMs
    }
}
