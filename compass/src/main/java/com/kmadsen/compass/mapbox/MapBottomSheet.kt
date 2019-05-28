package com.kmadsen.compass.mapbox

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kmadsen.compass.R
import com.kmadsen.compass.azimuth.toDegrees
import com.kmadsen.compass.location.LocationRepository
import com.kylemadsen.core.FpsChoreographer
import com.kylemadsen.core.logger.L
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.compass_map_bottom_sheet.view.*
import javax.inject.Inject


class MapBottomSheet(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {


    @Inject lateinit var locationRepository: LocationRepository

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

        compositeDisposable.add(FpsChoreographer().observeFps().subscribe {
            fps_text.text = "fps %.2f".format(it)
        })
        compositeDisposable.add(locationRepository.observeLocation().subscribe {
            it.toNullable()?.apply {
                location_text.text = "lat, lng = %.5f,%.5f\n".format(latitude, longitude) +
                        "accuracy %.2f\n".format(horizontalAccuracyMeters) +
                        "altitude %.2f\n".format(altitudeMeters) +
                        "bearing %.2f\n".format(bearingDegrees) +
                        "speed %.2f".format(speedMetersPerSecond)
            }
        })
        compositeDisposable.add(locationRepository.observeAzimuth().subscribe {
            azimuth_text.text = "azimuth %.2f".format(it.deviceDirectionRadians.toDegrees())
        })
    }

    override fun onDetachedFromWindow() {
        compositeDisposable.dispose()
        super.onDetachedFromWindow()
    }
}

fun Resources.convertDpToPixel(dp: Float): Int {
    return (dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}