package com.kylemadsen.testandroid;

import android.app.Application;

import com.kylemadsen.core.logger.AndroidLogger;
import com.kylemadsen.core.logger.L;
import com.mapbox.mapboxsdk.Mapbox;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.compass_mapbox_access_token));
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            L.add(AndroidLogger.getInstance());
        }
    }
}
