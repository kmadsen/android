package com.kmadsen.compass.mapbox

import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
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
        rotationView.visibility = View.GONE

        compositeDisposable.add(azimuthSensor.observeAzimuth()
                .withLatestFrom(locationSensor.observeLocations())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { azimuthLocationPair: Pair<Azimuth, Optional<BasicLocation>> ->
                    azimuthLocationPair.second.toNullable()?.apply {
                        val screenLocation = mapboxMap.projection.toScreenLocation(LatLng(latitude, longitude))
                        deviceDirectionView.translationX = screenLocation.x - deviceDirectionView.right / 2
                        deviceDirectionView.translationY = screenLocation.y - deviceDirectionView.bottom / 2

                        val angle = azimuthLocationPair.first.deviceDirectionDegrees
                        if (angle == null) {
                            slideDown(rotationView)
                        } else {
                            deviceDirectionView.rotation = angle.toFloat()
                            slideUp(rotationView)
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


    var isShowingRotation: Boolean = false

    // slide the view from below itself to the current position
    private fun slideUp(view: View) {
        if (isShowingRotation) return
        isShowingRotation = true

        val translateAnimation = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            view.height.toFloat() / 2.0f,
            0.0f
        ) // toYDelta
        translateAnimation.duration = 500
        translateAnimation.fillAfter = true

        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 500
        alphaAnimation.fillAfter = true

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.fillAfter = true

        view.visibility = View.VISIBLE
        view.startAnimation(animationSet)
    }

    // slide the view from its current position to below itself
    private fun slideDown(view: View) {
        if (view.visibility == View.GONE || !isShowingRotation) return
        isShowingRotation = false

        val translateAnimation = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            0.0f,
            view.height.toFloat() / 2.0f
        ) // toYDelta
        translateAnimation.duration = 500
        translateAnimation.fillAfter = true

        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = 500
        alphaAnimation.fillAfter = true

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.fillAfter = true

        view.startAnimation(animationSet)
    }
}
