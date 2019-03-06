package com.kmadsen.compass.fusedcompass

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.kmadsen.compass.location.CompassLocation
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

class CompassView(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val compassModel = CompassModel()

    private val mPaintLine = Paint()

    init {
        mPaintLine.color = Color.BLUE
        mPaintLine.strokeWidth = dpToPx(4.0f)
    }

    var disposable: Disposable = Disposables.disposed()
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        disposable = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .doOnNext { invalidate() }
                .subscribe()
    }

    override fun onDetachedFromWindow() {
        disposable.dispose()

        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        drawAxes(canvas)
        super.onDraw(canvas)
    }

    private fun drawAxes(canvas: Canvas) {
        if (compassModel.isReady()) {
            val azimuthInRadians: Float = compassModel.getAzimuthInRadians()

            val centerX: Float = canvas.width.toFloat() / 2.0f
            val centerY: Float = canvas.height.toFloat() / 2.0f
            val radiusPx = dpToPx(50f)
            val pointerX = centerX + radiusPx * sin(azimuthInRadians)
            val pointerY = centerY + radiusPx * cos(azimuthInRadians)

            canvas.drawLine(centerX, centerY, pointerX, pointerY, mPaintLine)
            canvas.drawCircle(pointerX, pointerY, dpToPx(5F), mPaintLine)
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun onMagneticFieldChange(eventValues: FloatArray) {
        compassModel.onMagneticFieldChange(eventValues)
    }

    fun onAccelerationChange(eventValues: FloatArray) {
        compassModel.onAccelerationChange(eventValues)
    }

    fun onLocationChanged(compassLocation: CompassLocation) {
        compassModel.onLocationChange(compassLocation)
    }
}
