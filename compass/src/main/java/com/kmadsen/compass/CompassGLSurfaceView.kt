package com.kmadsen.compass

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.kmadsen.compass.azimuth.Azimuth
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.sensors.AndroidSensors

import com.kmadsen.compass.sensors.SensorGLRenderer
import com.kmadsen.compass.wifilocation.WifiLocationResponse
import com.kmadsen.compass.wifilocation.WifiLocationScanner
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class CompassGLSurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var mapboxMap: MapboxMap
    @Inject lateinit var azimuthSensor: AzimuthSensor
    @Inject lateinit var androidSensors: AndroidSensors
    @Inject lateinit var wifiLocationScanner: WifiLocationScanner

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
        compositeDisposable.add(androidSensors.observeRotationVector().subscribe {
            glSurfaceRenderer.update(it.sensorEvent.values)
        })

        compositeDisposable.add(azimuthSensor.observeAzimuth(context)
            .withLatestFrom(wifiLocationScanner.observeWifiLocations(context))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { azimuthLocationPair: Pair<Azimuth, WifiLocationResponse> ->
                azimuthLocationPair.second.wifiLocation?.apply {
                    val screenLocation = mapboxMap.projection.toScreenLocation(LatLng(latitude, longitude))
                    glSurfaceRenderer.updateLocationPosition(screenLocation)
                }
            })
    }

    fun detach() {
        compositeDisposable.dispose()
    }
}
