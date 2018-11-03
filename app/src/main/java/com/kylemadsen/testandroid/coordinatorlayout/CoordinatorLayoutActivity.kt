package com.kylemadsen.testandroid.coordinatorlayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup

import com.kylemadsen.core.view.ViewGroupController
import com.kylemadsen.testandroid.R
import com.kylemadsen.testandroid.animation.AnimationController
import com.kylemadsen.testandroid.animation.TextToSpeechController

class CoordinatorLayoutActivity : AppCompatActivity() {

    var viewGroupController: ViewGroupController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coordinator_layout_activity)

    }

    override fun onDestroy() {
        viewGroupController?.onDestroy()

        super.onDestroy()
    }
}
