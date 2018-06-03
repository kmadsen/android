package com.kylemadsen.testandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kylemadsen.testandroid.audio.TextToSpeechController;

public class MainActivity extends AppCompatActivity {

    ViewGroupController viewGroupController;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_dashboard:
                        return true;
                    case R.id.navigation_notifications:
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ViewGroup contentView = findViewById(R.id.content);
        viewGroupController = ViewGroupController.onCreate(contentView);
        viewGroupController.attach(new TextToSpeechController());
    }

    @Override
    protected void onDestroy() {
        viewGroupController.onDestroy();
        super.onDestroy();
    }

}
