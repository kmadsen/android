package com.kmadsen.compass.timeseries

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.hardware.SensorManager
import com.kylemadsen.core.logger.L
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CompassView(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private var currentDegree = 0f

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
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
            SensorManager.getOrientation(rotationMatrix, orientation)
            val azimuthInRadians = orientation[0] - PI.toFloat()
            currentDegree = (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360

            val centerX: Float = canvas.width.toFloat() / 2.0f
            val centerY: Float = canvas.height.toFloat() / 2.0f
            val radiusPx = dpToPx(50f)
            val pointerX = centerX + radiusPx * sin(azimuthInRadians)
            val pointerY = centerY + radiusPx * cos(azimuthInRadians)
            L.i("on draw degrees $currentDegree")

            canvas.drawLine(centerX, centerY, pointerX, pointerY, mPaintLine)
            canvas.drawCircle(pointerX, pointerY, dpToPx(5F), mPaintLine)
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun onMagneticFieldChange(eventValues: FloatArray) {
        System.arraycopy(eventValues, 0, lastMagnetometer, 0, eventValues.size)
        lastMagnetometerSet = true
    }

    fun onAccelerationChange(eventValues: FloatArray) {
        System.arraycopy(eventValues, 0, lastAccelerometer, 0, eventValues.size)
        lastAccelerometerSet = true
    }
}
