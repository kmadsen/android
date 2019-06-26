package com.kmadsen.compass.mapbox

import android.animation.TimeInterpolator
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.view.animation.PathInterpolatorCompat
import com.gojuno.koptional.Optional
import com.kmadsen.compass.R
import com.kmadsen.compass.azimuth.Azimuth
import com.kmadsen.compass.azimuth.AzimuthSensor
import com.kmadsen.compass.location.BasicLocation
import com.kmadsen.compass.location.LocationSensor
import com.kmadsen.compass.wifilocation.WifiLocationScanner
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
    private var isShowingDirection: Boolean = false

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
                            hideDirection(rotationView)
                        } else {
                            deviceDirectionView.rotation = angle.toFloat()
                            showDirection(rotationView)
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

    private fun showDirection(view: View) {
        if (isShowingDirection) return

        isShowingDirection = true
        view.visibility = View.VISIBLE
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setInterpolator(ENTER)
            .setDuration(DURATION_SHORT)
            .start()
    }

    private fun hideDirection(view: View) {
        if (view.visibility == View.GONE || !isShowingDirection) return

        isShowingDirection = false
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setInterpolator(EXIT)
            .setDuration(DURATION_SHORT)
            .start()
    }

    companion object {
        val ENTER: TimeInterpolator = PathInterpolatorCompat.create(0f, 0f, 0.15f, 1f)
        val EXIT: TimeInterpolator = PathInterpolatorCompat.create(0.45f, 0f, 1f, 1f)

        const val DURATION_SHORT = 200L
    }
}
