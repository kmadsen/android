package com.kmadsen.compass.mapbox

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.gojuno.koptional.Optional
import com.kmadsen.compass.R
import com.kmadsen.compass.azimuth.Azimuth
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.location.BasicLocation
import com.kmadsen.compass.location.LocationSensor
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject


class MapViewController {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var mapboxMap: MapboxMap
    @Inject lateinit var locationSensor: LocationSensor
    @Inject lateinit var azimuthSensor: AzimuthSensor

    private val defaultZoom: Double = 12.0

    fun attach(mapOverlayView: MapOverlayView) {

        L.i("onCreate start")

        val layoutInflater = LayoutInflater.from(mapOverlayView.context)
        val deviceDirectionView = layoutInflater.inflate(R.layout.current_location, mapOverlayView, true)
        val rotationView = deviceDirectionView.findViewById<ImageView>(R.id.location_direction)

        compositeDisposable.add(locationSensor.observeLocations()
                .withLatestFrom(azimuthSensor.observeAzimuth())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { azimuthLocationPair: Pair<Optional<BasicLocation>, Azimuth> ->
                    azimuthLocationPair.first.toNullable()?.apply {
                        val screenLocation = mapboxMap.projection.toScreenLocation(LatLng(latitude, longitude))
                        deviceDirectionView.translationX = screenLocation.x - deviceDirectionView.right / 2
                        deviceDirectionView.translationY = screenLocation.y - deviceDirectionView.bottom / 2

                        val angle = azimuthLocationPair.second.deviceDirectionDegrees
                        L.i("")
                        if (angle == null) {
                            rotationView.visibility = View.GONE
                        } else {
                            deviceDirectionView.rotation = angle.toFloat()
                            rotationView.visibility = View.VISIBLE
                        }
                    }
                })

        compositeDisposable.add(locationSensor.firstValidLocation()
                .subscribe { optionalLocation: Optional<BasicLocation> ->
                    optionalLocation.toNullable()?.apply {
                        centerMap(latitude, longitude)
                    }
                })
    }

    fun detach() {
        compositeDisposable.clear()
    }

    private fun centerMap(latitude: Double, longitude: Double) {
        L.i("CompassMainActivity centerMap start")

        val cameraPosition: CameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(defaultZoom)
                .build()
        mapboxMap.cameraPosition = cameraPosition

        L.i("CompassMainActivity centerMap end")
    }
}
