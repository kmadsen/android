package com.kmadsen.compass

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet

import com.kmadsen.compass.sensors.SensorGLRenderer

class CompassGLSurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val glSurfaceRenderer: SensorGLRenderer = SensorGLRenderer()

    init {
        setEGLContextClientVersion(2)
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
        setRenderer(glSurfaceRenderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    fun update(rotationMatrix: FloatArray) {
        glSurfaceRenderer.update(rotationMatrix)
    }
}
