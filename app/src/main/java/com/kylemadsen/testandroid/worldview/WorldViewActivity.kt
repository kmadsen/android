package com.kylemadsen.testandroid.worldview

import android.os.Bundle
import android.view.SurfaceView

import androidx.appcompat.app.AppCompatActivity

import com.kylemadsen.testandroid.R

class WorldViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_view)

        val glSurfaceView = findViewById<WorldGLSurfaceView>(R.id.map_gl_surface_view)
        glSurfaceView.setZOrderMediaOverlay(true)
    }
}
