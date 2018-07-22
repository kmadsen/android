package com.kmadsen.compass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kmadsen.compass.location.LocationService;
import com.kmadsen.compass.mapbox.MapViewController;
import com.mapbox.mapboxsdk.maps.MapView;

public class CompassMainActivity extends AppCompatActivity {

    MapViewController mapViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_main_activity);

        CompassGLSurfaceView glView = findViewById(R.id.surface_view);

        LocationService locationService = new LocationService(getApplication());
        locationService.getLocationManager();

        MapView mapView = findViewById(R.id.mapbox_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            mapViewController = new MapViewController(mapView, mapboxMap);
        });
    }
}
