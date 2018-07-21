package com.kylemadsen.testandroid;

import android.app.Application;
import com.kylemadsen.testandroid.logger.AndroidLogger;
import com.kylemadsen.testandroid.logger.L;
import com.mapbox.mapboxsdk.Mapbox;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mapbox.getInstance(getApplicationContext(), getString(R.string.compass_mapbox_access_token));

        initLogging();
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            L.add(AndroidLogger.getInstance());
        }
    }
}
