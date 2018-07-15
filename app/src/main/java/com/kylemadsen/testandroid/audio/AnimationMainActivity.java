package com.kylemadsen.testandroid.audio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.kylemadsen.testandroid.R;
import com.kylemadsen.testandroid.ViewGroupController;

public class AnimationMainActivity extends AppCompatActivity {

    ViewGroupController viewGroupController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_main_activity);

        ViewGroup contentView = findViewById(R.id.text_to_speech_content);
        viewGroupController = ViewGroupController.onCreate(contentView);
        viewGroupController.attach(new TextToSpeechController());
    }

    @Override
    protected void onDestroy() {
        viewGroupController.onDestroy();

        super.onDestroy();
    }
}
