package com.kmadsen.compass.mapbox

import android.content.Context
import android.hardware.Sensor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.gojuno.koptional.Optional
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kmadsen.compass.R
import com.kmadsen.compass.azimuth.AltitudeSensor
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.azimuth.Measure1d
import com.kmadsen.compass.location.BasicLocation
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.location.sensor.SensorLocation
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.walking.WalkingStateSensor
import com.kylemadsen.core.FpsChoreographer
import com.kylemadsen.core.logger.L
import com.kylemadsen.core.time.DeviceClock
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.compass_map_bottom_sheet.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MapBottomSheet(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    @Inject lateinit var locationRepository: LocationRepository
    @Inject lateinit var androidSensors: AndroidSensors
    @Inject lateinit var altitudeSensor: AltitudeSensor
    @Inject lateinit var walkingStateSensor: WalkingStateSensor

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<View>

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(context).inflate(R.layout.compass_map_bottom_sheet, this, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        standardBottomSheetBehavior = BottomSheetBehavior.from(this)

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        }
        standardBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
        standardBottomSheetBehavior.peekHeight = resources.getDimension(R.dimen.bottom_sheet_card_height).toInt() +
                2 * resources.getDimension(R.dimen.bottom_sheet_card_margin).toInt()

        compositeDisposable.add(FpsChoreographer().observeFps().observeOn(AndroidSchedulers.mainThread()).subscribe {
            fps_text.text = "app fps %.2f".format(it)
        })
        compositeDisposable.add(observeSensorLocation().observeOn(AndroidSchedulers.mainThread()).subscribe {
            location_text.text = "lat, lng = %.7f, %.7f\n".format(it.basicLocation?.latitude, it.basicLocation?.longitude) +
                    "  accuracy ${it.basicLocation?.horizontalAccuracyMeters.formatDecimal()}\n" +
                    "  altitude ${it.basicLocation?.altitudeMeters.formatDecimal()}\n" +
                    "  bearing ${it.basicLocation?.bearingDegrees.formatDecimal()}\n" +
                    "  speed ${it.basicLocation?.speedMetersPerSecond.formatDecimal()}\n" +
                    "  stale clock1=${it.staleSeconds.formatDecimal()} clock2=${it.staleDisplaySeconds.formatDecimal()}"
        })

        var firstAltitudeMeters: Float? = null
        var firstAltitudeMetersMeasuredMs: Long? = null
        compositeDisposable.add(altitudeSensor.observeAltitude().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (firstAltitudeMeters == null) {
                firstAltitudeMeters = it.value
                firstAltitudeMetersMeasuredMs = it.recordedAtMs
            }
            val deltaSinceOpen = it.value!! - firstAltitudeMeters!!
            val deltaSecondsSinceOpen = (DeviceClock.elapsedMillis() - firstAltitudeMetersMeasuredMs!!) / TimeUnit.SECONDS.toMillis(1).toDouble()
            pressure_text.text = "altitude meters ${it.value!!.formatDecimal()}   feet ${it.value.metersToFeet().formatDecimal()}\n" +
                    "  delta meters ${deltaSinceOpen.formatDecimal()}        feet ${deltaSinceOpen.metersToFeet().formatDecimal()}\n" +
                    "  delta seconds ${deltaSecondsSinceOpen.toFloat().formatDecimal()}"
        })

        compositeDisposable.add(observeDeviceDirection().observeOn(AndroidSchedulers.mainThread()).subscribe {
            azimuth_text.text = "azimuth\n   %.2f".format(it.first.value)
            turn_text.text = "turn\n   %.2f".format(it.second.value)
            direction_text.text = "direction\n   %.2f".format(it.third.value)
        })

        compositeDisposable.add(walkingStateSensor.observeWalkingState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            walking_state_text.text = "walking stale seconds ${it.realtimeNotWalkingSeconds}\n" +
                    "  walking steps ${it.walkingSteps}\n" +
                    "  walking seconds ${it.realtimeWalkingSeconds}\n" +
                    "  walking pace ${it.walkingStepsPerSecond.formatDecimal()}"
        })

        L.i("WIFI SCAN observe wifi locations")
        compositeDisposable.add(locationRepository.observeWifiLocation().observeOn(AndroidSchedulers.mainThread()).subscribe {
            L.i("WIFI SCAN location $it")
            wifi_location_response.text = "wifi recorded at ${it.recordedAtMs}\n" +
                "  wifi location ${it.wifiLocation}\n" +
                "  wifi location error ${it.errorMessage}\n" +
                "  wifi scan size ${it.wifiScan.wifiAccessPoints.size}\n"
        })
    }

    private fun observeDeviceDirection(): Observable<Triple<Measure1d, Measure1d, Measure1d>> {
        return Observable.combineLatest(
            locationRepository.observeAzimuth(),
            locationRepository.observeTurnDegrees(),
            locationRepository.observeDeviceDirection(),
            Function3 { azimuth, turn, direction ->
                Triple(azimuth, turn, direction)
            }
        )
    }

    private fun observeSensorLocation(): Observable<SensorLocation> {
        val startElapsedMs = DeviceClock.elapsedMillis()
        return Observable.combineLatest(
            Observable.interval(0, 100, TimeUnit.MILLISECONDS),
            locationRepository.observeLocation(),
            BiFunction { _, t2 ->
                val basicLocation = t2.toNullable()
                val staleDisplayMillis = if (basicLocation != null) {
                    DeviceClock.displayMillis() - basicLocation.timeMillis
                } else {
                    DeviceClock.elapsedMillis() - startElapsedMs
                }
                val staleMillis = if (basicLocation != null) {
                    DeviceClock.elapsedMillis() - TimeUnit.NANOSECONDS.toMillis(basicLocation.elapsedRealtimeNanos)
                } else staleDisplayMillis
                val staleDisplaySeconds = staleDisplayMillis / TimeUnit.SECONDS.toMillis(1).toDouble()
                val staleSeconds = staleMillis / TimeUnit.SECONDS.toMillis(1).toDouble()
                SensorLocation(basicLocation, staleDisplaySeconds, staleSeconds)
            }
        )
    }

    override fun onDetachedFromWindow() {
        compositeDisposable.dispose()
        super.onDetachedFromWindow()
    }
}

private fun Float.metersToFeet(): Float = this * 3.28084f

private fun <Value> Value.formatDecimal(): String {
    this ?: return "null"
    return "%.3f".format(this)
}
