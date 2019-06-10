package com.kmadsen.compass

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.gojuno.koptional.Optional
import com.kmadsen.compass.azimuth.Azimuth
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.location.BasicLocation
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.sensors.AndroidSensors

import com.kmadsen.compass.sensors.SensorGLRenderer
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class CompassGLSurfaceView constructor(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var mapboxMap: MapboxMap
    @Inject lateinit var locationSensor: LocationSensor
    @Inject lateinit var azimuthSensor: AzimuthSensor
    @Inject lateinit var androidSensors: AndroidSensors

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

        compositeDisposable.add(azimuthSensor.observeAzimuth()
            .withLatestFrom(locationSensor.observeLocations())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { azimuthLocationPair: Pair<Azimuth, Optional<BasicLocation>> ->
                azimuthLocationPair.second.toNullable()?.apply {
                    val screenLocation = mapboxMap.projection.toScreenLocation(LatLng(latitude, longitude))
                    L.i("SensorGLRenderer screen location = ${screenLocation.x}, ${screenLocation.y}")
                    glSurfaceRenderer.updateLocationPosition(screenLocation)
                }
            })
    }

    fun detach() {
        compositeDisposable.dispose()
    }
}
