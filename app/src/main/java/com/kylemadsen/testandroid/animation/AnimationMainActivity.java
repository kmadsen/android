package com.kylemadsen.testandroid.animation;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.kylemadsen.core.view.ViewGroupController;
import com.kylemadsen.testandroid.R;

public class AnimationMainActivity extends AppCompatActivity {

    ViewGroupController viewGroupController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_main_activity);

        ViewGroup contentView = findViewById(R.id.animation_content);
        viewGroupController = ViewGroupController.createController(contentView);
        viewGroupController.attach(new TextToSpeechController());
        viewGroupController.attach(new AnimationController());
    }

    @Override
    protected void onDestroy() {
        viewGroupController.onDestroy();

        super.onDestroy();
    }
}
