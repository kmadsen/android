package com.kylemadsen.testandroid.ar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.kylemadsen.core.logger.L;
import com.kylemadsen.testandroid.R;
import com.kylemadsen.testandroid.ViewGroupController;

public class ArMainActivity extends AppCompatActivity {

    ViewGroupController configurationController;
    ViewGroupController statusController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_main_activity);

        FrameLayout configurationLayout = findViewById(R.id.ar_configuration);
        FrameLayout statusLayout = findViewById(R.id.ar_status);
        configurationController = ViewGroupController.onCreate(configurationLayout);
        statusController = ViewGroupController.onCreate(statusLayout);

        ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            L.i("onTapPlane");
            L.i(ArObjectReader.read(hitResult));
            L.i(ArObjectReader.read(plane));
            L.i("motionEvent: %s", motionEvent.toString());

            //float distance = hitResult.getDistance();
            //Pose pose = hitResult.getHitPose();
            //L.i("");
            //Anchor anchor = hitResult.createAnchor();
            //Trackable trackable = hitResult.getTrackable();
        });

    }

    @Override
    protected void onDestroy() {
        statusController.onDestroy();
        configurationController.onDestroy();

        super.onDestroy();
    }
}
