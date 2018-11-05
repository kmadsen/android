package com.kylemadsen.testandroid.coordinatorlayout

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button
import com.kylemadsen.core.view.ViewController

import com.kylemadsen.core.view.ViewGroupController
import com.kylemadsen.testandroid.R
import com.kylemadsen.testandroid.animation.AnimationController
import com.kylemadsen.testandroid.animation.TextToSpeechController

class CoordinatorLayoutActivity : AppCompatActivity() {

    private var viewGroupController: ViewGroupController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coordinator_layout_activity)

        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.coordinator_layout)
        val snackBarButton: Button = findViewById(R.id.show_snack_bar_button)
        snackBarButton.setOnClickListener {
            Snackbar.make(coordinatorLayout, "Hey there!", Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    override fun onDestroy() {
        viewGroupController?.onDestroy()

        super.onDestroy()
    }
}
