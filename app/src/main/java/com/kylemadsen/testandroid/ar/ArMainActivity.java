package com.kylemadsen.testandroid.ar;

import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.kylemadsen.core.logger.L;
import com.kylemadsen.core.view.ViewGroupController;
import com.kylemadsen.testandroid.R;

public class ArMainActivity extends AppCompatActivity {

    ViewGroupController configurationController;
    ViewGroupController statusController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_main_activity);

        FrameLayout configurationLayout = findViewById(R.id.ar_configuration);
        FrameLayout statusLayout = findViewById(R.id.ar_status);
        configurationController = ViewGroupController.Companion.onCreate(configurationLayout);
        statusController = ViewGroupController.Companion.onCreate(statusLayout);

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
