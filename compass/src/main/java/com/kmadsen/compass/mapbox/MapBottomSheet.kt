package com.kmadsen.compass.mapbox

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kmadsen.compass.R
import com.kmadsen.compass.azimuth.toDegrees
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.sensors.AndroidSensors
import com.kmadsen.compass.walking.WalkingStateSensor
import com.kylemadsen.core.FpsChoreographer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.compass_map_bottom_sheet.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MapBottomSheet(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    @Inject lateinit var locationRepository: LocationRepository
    @Inject lateinit var androidSensors: AndroidSensors
    @Inject lateinit var walkingStateSensor: WalkingStateSensor

    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<View>
    private var lastLocationReceivedTimeMillis = 0L

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
        compositeDisposable.add(locationRepository.observeLocation().observeOn(AndroidSchedulers.mainThread()).subscribe {
            it.toNullable()?.apply {
                location_text.text = "lat, lng = %.5f,%.5f\n".format(latitude, longitude) +
                        "  accuracy ${horizontalAccuracyMeters.formatDecimal()}\n" +
                        "  altitude ${altitudeMeters.formatDecimal()}\n" +
                        "  bearing ${bearingDegrees.formatDecimal()}\n" +
                        "  speed ${speedMetersPerSecond.formatDecimal()}"
                lastLocationReceivedTimeMillis = System.currentTimeMillis()
            }
        })
        compositeDisposable.add(locationRepository.observeAzimuth().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val staleLocationStringMessage = if (lastLocationReceivedTimeMillis > 0) {
                val staleSeconds = (System.currentTimeMillis() - lastLocationReceivedTimeMillis) / TimeUnit.SECONDS.toMillis(1).toDouble()
                "location stale seconds = %.2f\n".format(staleSeconds)
            } else {
                ""
            }
            azimuth_text.text =
                staleLocationStringMessage +
                "azimuth %.2f".format(it.deviceDirectionRadians.toDegrees())
        })
        compositeDisposable.add(walkingStateSensor.observeWalkingState().observeOn(AndroidSchedulers.mainThread()).subscribe {
            walking_state_text.text = "walking stale seconds ${it.realtimeNotWalkingSeconds}\n" +
                    "walking steps ${it.walkingSteps}\n" +
                    "walking seconds ${it.realtimeWalkingSeconds}\n" +
                    "walking pace ${it.walkingStepsPerSecond.formatDecimal()}"
        })
    }

    override fun onDetachedFromWindow() {
        compositeDisposable.dispose()
        super.onDetachedFromWindow()
    }
}

private fun <Value> Value.formatDecimal(): String {
    this ?: return "null"
    return "%.3f".format(this)
}
