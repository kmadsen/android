package com.kylemadsen.testandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import com.kylemadsen.testandroid.ar.ArFragment;
import com.kylemadsen.testandroid.ar.ArObjectReader;
import com.kylemadsen.testandroid.logger.L;

public class MainActivity extends AppCompatActivity {

    ViewGroupController configurationController;
    ViewGroupController statusController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
