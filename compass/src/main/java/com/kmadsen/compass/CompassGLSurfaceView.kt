package com.kmadsen.compass

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet

import com.kmadsen.compass.sensors.PositionSensors
import com.kmadsen.compass.sensors.SensorGLRenderer
import com.kylemadsen.core.logger.L

class CompassGLSurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val glSurfaceRenderer: SensorGLRenderer = SensorGLRenderer(context)

    init {
        setEGLContextClientVersion(2)
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
        setRenderer(glSurfaceRenderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    fun onStart() {
        L.i("sensor start")
        glSurfaceRenderer.start()
    }

    fun onStop() {
        L.i("sensor stop")
        glSurfaceRenderer.stop()
    }
}
