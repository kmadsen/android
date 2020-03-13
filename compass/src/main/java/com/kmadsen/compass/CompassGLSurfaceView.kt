package com.kmadsen.compass

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.kmadsen.compass.sensors.SensorGLRenderer
import com.kmadsen.compass.sensors.rx.RxAndroidSensors
import com.kmadsen.compass.wifilocation.WifiLocationResponse
import com.kmadsen.compass.wifilocation.WifiLocationScanner
import com.kylemadsen.core.koin.inject
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class CompassGLSurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val mapboxMap: MapboxMap by inject()
    private val rxAndroidSensors: RxAndroidSensors by inject()
    private val wifiLocationScanner: WifiLocationScanner by inject()

    private val glSurfaceRenderer: SensorGLRenderer = SensorGLRenderer()

    init {
        setEGLContextClientVersion(2)
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
        setRenderer(glSurfaceRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun attach() {
        compositeDisposable.add(rxAndroidSensors.observeRotationVector().subscribe {
            glSurfaceRenderer.update(it.sensorEvent.values)
        })

        compositeDisposable.add(wifiLocationScanner.observeWifiLocations(context)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { wifiLocationResponse: WifiLocationResponse ->
                wifiLocationResponse.wifiLocation?.apply {
                    val screenLocation = mapboxMap.projection.toScreenLocation(LatLng(latitude, longitude))
                    glSurfaceRenderer.updateLocationPosition(screenLocation)
                }
            })
    }

    fun detach() {
        compositeDisposable.dispose()
    }
}
