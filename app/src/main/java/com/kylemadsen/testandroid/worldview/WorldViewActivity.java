package com.kylemadsen.testandroid.worldview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kylemadsen.testandroid.R;

public class WorldViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
