package com.kylemadsen.testandroid

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.kmadsen.compass.CompassMainActivity
import com.kylemadsen.core.logger.L
import com.kylemadsen.core.time.DeviceClock
import com.kylemadsen.core.view.ViewController
import com.kylemadsen.core.view.ViewRouter
import com.kylemadsen.testandroid.animation.AnimationMainActivity
import com.kylemadsen.testandroid.ar.ArMainActivity
import com.kylemadsen.testandroid.coordinatorlayout.CoordinatorLayoutActivity

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

        val gotoAnimationsButton: View = view.find(R.id.goto_animations_button)
        gotoAnimationsButton.setOnClickListener {
            viewRouter.goToActivity(AnimationMainActivity::class.java)
        }

        val gotoCompassButton: View = view.find(R.id.goto_compass_button)
        gotoCompassButton.setOnClickListener {
            viewRouter.goToActivity(CompassMainActivity::class.java)
        }

        val gotoCoordinatorLayoutButton: View = view.find(R.id.goto_layout_button)
        gotoCoordinatorLayoutButton.setOnClickListener {
            viewRouter.goToActivity(CoordinatorLayoutActivity::class.java)
        }

        val clockInfoText: TextView = view.find(R.id.clock_info)
        clockInfoText.text = DeviceClock.getClockDriftInfo()
        val resetDriftButton: Button = view.find(R.id.clock_info_rest)
        resetDriftButton.setOnClickListener {
            DeviceClock.resetClockDriftInfo()
            clockInfoText.text = DeviceClock.getClockDriftInfo()
        }
    }

    override fun detach() {

    }
}
