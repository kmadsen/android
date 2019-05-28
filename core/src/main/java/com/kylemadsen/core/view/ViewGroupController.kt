package com.kylemadsen.core.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread

import java.util.ArrayList

class ViewGroupController private constructor(
        private var viewGroup: ViewGroup?,
        private val layoutInflater: LayoutInflater
) {

    private val viewControllers = ArrayList<ViewController>()

    @MainThread
    fun <ViewControllerType : ViewController> attach(viewController: ViewControllerType) {
        val view = layoutInflater.inflate(viewController.layoutId, viewGroup, true)
        viewController.attach(view)
        viewControllers.add(viewController)
    }

    @MainThread
    fun onDestroy() {
        for (viewController in viewControllers) {
            viewController.detach()
        }
        viewControllers.clear()
        viewGroup?.removeAllViews()
        viewGroup = null
    }

    companion object {

        @MainThread
        @JvmStatic
        fun onCreate(viewGroup: ViewGroup): ViewGroupController {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            return ViewGroupController(viewGroup, layoutInflater)
        }
    }
}
