package com.kmadsen.compass.fusedcompass

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.kmadsen.compass.location.BasicLocation
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

    private var azimuthInRadians: Double = 0.0

    private val mPaintLine = Paint()

    init {
        mPaintLine.color = Color.BLUE
        mPaintLine.strokeWidth = dpToPx(4.0f)
    }

    override fun onDraw(canvas: Canvas) {
        drawAxes(canvas)
        super.onDraw(canvas)
    }

    private fun drawAxes(canvas: Canvas) {
        val centerX: Float = canvas.width.toFloat() / 2.0f
        val centerY: Float = canvas.height.toFloat() / 2.0f
        val radiusPx = dpToPx(50f)
        val pointerX = centerX + radiusPx * sin(azimuthInRadians.toFloat())
        val pointerY = centerY + radiusPx * cos(azimuthInRadians.toFloat())

        canvas.drawLine(centerX, centerY, pointerX, pointerY, mPaintLine)
        canvas.drawCircle(pointerX, pointerY, dpToPx(5F), mPaintLine)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun updateAzimuthRadians(azimuthInRadians: Double) {
        this.azimuthInRadians = azimuthInRadians
        invalidate()
    }
}
