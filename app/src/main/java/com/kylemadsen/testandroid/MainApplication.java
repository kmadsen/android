package com.kylemadsen.testandroid;

import android.app.Application;
import com.kylemadsen.testandroid.logger.AndroidLogger;
import com.kylemadsen.testandroid.logger.L;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            L.add(AndroidLogger.getInstance());
        }
    }
}
