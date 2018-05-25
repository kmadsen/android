package com.kylemadsen.testandroid.coordinatorlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

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
