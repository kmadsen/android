package com.kylemadsen.testandroid.worldview

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import io.reactivex.disposables.CompositeDisposable

class WorldGLSurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val glWorldRenderer = WorldGLRenderer()

    init {
        setEGLContextClientVersion(2)
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
        setRenderer(glWorldRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun attach() {

    }

    fun detach() {
        compositeDisposable.dispose()
    }
}
