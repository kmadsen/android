package com.kylemadsen.testandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kylemadsen.testandroid.ar.ArMainActivity;
import com.kylemadsen.testandroid.audio.AnimationMainActivity;
import com.kylemadsen.testandroid.gnsslogger.GnssMainActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View gotoArButton = findViewById(R.id.goto_ar_button);
        gotoArButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ArMainActivity.class);
            startActivity(intent);
        });

        View gotoGnssLoggerButton = findViewById(R.id.goto_gnss_button);
        gotoGnssLoggerButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, GnssMainActivity.class);
            startActivity(intent);
        });

        View gotoAnimationsButton = findViewById(R.id.goto_animations_button);
        gotoAnimationsButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AnimationMainActivity.class);
            startActivity(intent);
        });

        View gotoCompassButton = findViewById(R.id.goto_compass_button);
        gotoCompassButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CompassMainActivity.class);
            startActivity(intent);
        });
    }
}
