package com.kylemadsen.testandroid

import android.view.View
import com.kmadsen.compass.CompassMainActivity
import com.kylemadsen.core.localhost.LocalhostFilesActivity
import com.kylemadsen.core.view.ViewController
import com.kylemadsen.core.view.ViewRouter
import com.kylemadsen.testandroid.animation.AnimationMainActivity
import com.kylemadsen.testandroid.clock.ClockInfoActivity
import com.kylemadsen.testandroid.coordinatorlayout.CoordinatorLayoutActivity
import com.kylemadsen.testandroid.renderscript.RenderscriptActivity
import com.kylemadsen.testandroid.worldview.WorldViewActivity

class MainViewController(
        private val viewRouter: ViewRouter
) : ViewController {
    override val layoutId: Int
        get() = R.layout.app_goto_buttons

    override fun attach(view: View) {
        view.find<View>(R.id.goto_animations_button).setOnClickListener {
            viewRouter.goToActivity(AnimationMainActivity::class.java)
        }

        view.find<View>(R.id.goto_compass_button).setOnClickListener {
            viewRouter.goToActivity(CompassMainActivity::class.java)
        }

        view.find<View>(R.id.goto_localhost_button).setOnClickListener {
            viewRouter.goToActivity(LocalhostFilesActivity::class.java)
        }

        view.find<View>(R.id.goto_layout_button).setOnClickListener {
            viewRouter.goToActivity(CoordinatorLayoutActivity::class.java)
        }

        view.find<View>(R.id.goto_world_view).setOnClickListener {
            viewRouter.goToActivity(WorldViewActivity::class.java)
        }

        view.find<View>(R.id.goto_clock_info).setOnClickListener {
            viewRouter.goToActivity(ClockInfoActivity::class.java)
        }

        view.find<View>(R.id.goto_renderscript).setOnClickListener {
            viewRouter.goToActivity(RenderscriptActivity::class.java)
        }
    }

    override fun detach() {

    }
}
