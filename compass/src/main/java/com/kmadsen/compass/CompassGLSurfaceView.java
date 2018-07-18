package com.kmadsen.compass;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class CompassGLSurfaceView extends GLSurfaceView {

    private final CompassGLRenderer renderer;

    public CompassGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new CompassGLRenderer();
        setRenderer(renderer);
    }
}