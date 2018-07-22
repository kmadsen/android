package com.kmadsen.compass.mapbox;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class MapViewController {

    private final MapView mapView;
    private final MapboxMap mapboxMap;

    public MapViewController(MapView mapView, MapboxMap mapboxMap) {
        this.mapView = mapView;
        this.mapboxMap = mapboxMap;
    }
}
