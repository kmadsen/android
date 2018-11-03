package com.kylemadsen.testandroid.animation

import android.graphics.drawable.Animatable
import android.view.View
import android.widget.ImageView

import com.kylemadsen.testandroid.R
import com.kylemadsen.core.view.ViewController

class AnimationController : ViewController {

    override val layoutId: Int
        get() = R.layout.animation_view

    override fun attach(view: View) {
        val imageView: ImageView = view.find(R.id.image_view)
        (imageView.drawable as Animatable).start()
    }

    override fun detach() {

    }
}
