package com.kylemadsen.testandroid.coordinatorlayout

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View
import com.kylemadsen.core.logger.L

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
        val translationY = getFabTranslationYForSnackbar(parent, child)
        val percentComplete = -translationY / dependency.height
        val scaleFactor = 1 - percentComplete

        child.scaleX = scaleFactor
        child.scaleY = scaleFactor
        L.i("onDependentViewChanged scaleFactor=$scaleFactor")
        return false
    }

    private fun getFabTranslationYForSnackbar(parent: CoordinatorLayout,
                                              fab: FloatingActionButton): Float {
        var minOffset = 0f
        for (view: View in parent.getDependencies(fab)) {
            if (view is Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset, view.translationY - view.getHeight())
            }
        }

        return minOffset
    }
}
