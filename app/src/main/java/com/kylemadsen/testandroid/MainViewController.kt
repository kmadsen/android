package com.kylemadsen.testandroid

import android.view.View
import com.kmadsen.compass.CompassMainActivity
import com.kylemadsen.core.view.ViewController
import com.kylemadsen.core.view.ViewRouter
import com.kylemadsen.testandroid.animation.AnimationMainActivity
import com.kylemadsen.testandroid.ar.ArMainActivity
import com.kylemadsen.testandroid.gnsslogger.GnssMainActivity

class MainViewController(
        private val viewRouter: ViewRouter
) : ViewController {
    override val layoutId: Int
        get() = R.layout.app_goto_buttons

    override fun attach(view: View) {
        val gotoArButton: View = view.find(R.id.goto_ar_button)
        gotoArButton.setOnClickListener {
            viewRouter.goToActivity(ArMainActivity::class.java)
        }

        val gotoGnssLoggerButton: View = view.find(R.id.goto_gnss_button)
        gotoGnssLoggerButton.setOnClickListener {
            viewRouter.goToActivity(GnssMainActivity::class.java)
        }

        val gotoAnimationsButton: View = view.find(R.id.goto_animations_button)
        gotoAnimationsButton.setOnClickListener {
            viewRouter.goToActivity(AnimationMainActivity::class.java)
        }

        val gotoCompassButton: View = view.find(R.id.goto_compass_button)
        gotoCompassButton.setOnClickListener {
            viewRouter.goToActivity(CompassMainActivity::class.java)
        }
    }

    override fun detach() {

    }
}