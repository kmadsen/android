package com.kylemadsen.testandroid.coordinatorlayout

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

class ShrinkingBehavior(context: Context, attrs: AttributeSet)
    : DebugBehavior<FloatingActionButton>(context, attrs) {

    override fun layoutDependsOn(
            parent: CoordinatorLayout,
            child: FloatingActionButton,
            dependency: View
    ): Boolean {
        val value: Boolean = dependency is Snackbar.SnackbarLayout
        log("$value layoutDependsOn child=%s dependency=%s".format(child.javaClass.simpleName, dependency.javaClass.simpleName))
        return value
    }

    override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: FloatingActionButton,
            dependency: View
    ): Boolean {
        val percentComplete = 1.0F - dependency.translationY / dependency.height
        transformView(child, percentComplete)
        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View) {
        transformView(child, 0.0F)
    }

    private fun transformView(child: FloatingActionButton, percentComplete: Float) {
        val scaleFactor = 1 - percentComplete
        child.scaleX = scaleFactor
        child.scaleY = scaleFactor
        log("transformView percentComplete=$percentComplete scaleFactor=$scaleFactor")
    }
}
