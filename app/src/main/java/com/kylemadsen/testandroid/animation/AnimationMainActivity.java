package com.kylemadsen.testandroid.animation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.kylemadsen.testandroid.R;
import com.kylemadsen.core.view.ViewGroupController;

public class AnimationMainActivity extends AppCompatActivity {

    ViewGroupController viewGroupController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_main_activity);

        ViewGroup contentView = findViewById(R.id.animation_content);
        viewGroupController = ViewGroupController.Companion.onCreate(contentView);
        viewGroupController.attach(new TextToSpeechController());
        viewGroupController.attach(new AnimationController());
    }

    @Override
    protected void onDestroy() {
        viewGroupController.onDestroy();

        super.onDestroy();
    }
}
