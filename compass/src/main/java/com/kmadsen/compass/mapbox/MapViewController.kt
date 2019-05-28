package com.kmadsen.compass.mapbox

import android.view.LayoutInflater
import com.gojuno.koptional.Optional
import com.kmadsen.compass.R
import com.kmadsen.compass.azimuth.Azimuth
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.azimuth.toDegrees
import com.kmadsen.compass.location.BasicLocation
import com.kmadsen.compass.location.LocationRepository
import com.kmadsen.compass.location.LocationsController
import com.kylemadsen.core.logger.L
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MapViewController {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var mapboxMap: MapboxMap
    @Inject lateinit var locationsController: LocationsController
    @Inject lateinit var azimuthSensor: AzimuthSensor

    private val defaultZoom: Double = 12.0

    fun attach(mapOverlayView: MapOverlayView) {

        L.i("onCreate start")

        val layoutInflater = LayoutInflater.from(mapOverlayView.context)
        val deviceDirectionView = layoutInflater.inflate(R.layout.current_location, mapOverlayView, true)

        compositeDisposable.add(azimuthSensor.observeAzimuth()
                .subscribe { azimuth: Azimuth ->
                    val angle = azimuth.deviceDirectionRadians.toDegrees().toFloat()
                    deviceDirectionView.rotation = angle
                })

        compositeDisposable.add(locationsController.firstValidLocation()
                .subscribe { optionalLocation: Optional<BasicLocation> ->
                    optionalLocation.toNullable()?.apply {
                        centerMap(latitude, longitude)
                    }
                })
        compositeDisposable.add(locationsController.observeLocations()
                .subscribe { optionalLocation: Optional<BasicLocation> ->
                    optionalLocation.toNullable()?.apply {
                        val screenLocation = mapboxMap.projection.toScreenLocation(LatLng(latitude, longitude))
                        deviceDirectionView.translationX = screenLocation.x - deviceDirectionView.right / 2
                        deviceDirectionView.translationY = screenLocation.y - deviceDirectionView.bottom / 2
                    }
                })
    }

    fun onDestroy() {
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
