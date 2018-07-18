package com.kmadsen.compass;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CompassMainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.compass_main_activity);

        glSurfaceView = new CompassGLSurfaceView(this);
        setContentView(glSurfaceView);
    }
}
