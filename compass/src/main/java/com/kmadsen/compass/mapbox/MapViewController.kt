package com.kmadsen.compass.mapbox

import com.gojuno.koptional.Optional
import com.kmadsen.compass.location.BasicLocation
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

class MapViewController(private val mapboxMap: MapboxMap) {

    private val defaultZoom: Double = 12.0

    private var marker: Marker? = null

    fun updatePinLocation(optionalLocation: Optional<BasicLocation>) {
        val location = optionalLocation.toNullable()
        if (location == null) {
            marker?.remove()
            return
        }

        val markerOptions = MarkerOptions()
        val latLng = LatLng(location.latitude, location.longitude)
        if (marker == null) {
            markerOptions.position = latLng
            marker = mapboxMap.addMarker(markerOptions)
        } else {
            marker?.position = latLng
        }
    }

    fun centerMap(latitude: Double, longitude: Double) {
        val cameraPosition: CameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(defaultZoom)
                .build()
        mapboxMap.cameraPosition = cameraPosition
    }
}
