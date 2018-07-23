package com.kmadsen.compass.mapbox

import android.location.Location
import com.kmadsen.compass.location.fused.FusedLocation
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap

class MapViewController(private val mapView: MapView, private val mapboxMap: MapboxMap) {

    var marker: Marker? = null

    fun updateLocation(fusedLocation: FusedLocation) {
        if (fusedLocation.locationResult == null) {
            return
        }
        val markerOptions = MarkerOptions()
        val lastLocation: Location = fusedLocation.locationResult.lastLocation
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
        if (marker == null) {
            markerOptions.position = latLng
            marker = mapboxMap.addMarker(markerOptions)
        } else {
            marker?.position = latLng
        }
    }
}
