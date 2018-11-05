package com.kylemadsen.testandroid.coordinatorlayout

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.WindowInsetsCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kylemadsen.core.logger.L

abstract class DebugBehavior<ViewType : View>(context: Context, attrs: AttributeSet)
    : CoordinatorLayout.Behavior<ViewType>(context, attrs) {

    fun log(message: String) {
        L.i(message)
    }

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        log("onAttachedToLayoutParams")
        super.onAttachedToLayoutParams(params)
    }

    override fun onDetachedFromLayoutParams() {
        log("onDetachedFromLayoutParams")
        super.onDetachedFromLayoutParams()
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: ViewType, motionEvent: MotionEvent): Boolean {
        val value: Boolean = super.onInterceptTouchEvent(parent, child, motionEvent)
        log("$value = onInterceptTouchEvent motionEvent=$motionEvent")
        return value
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: ViewType, motionEvent: MotionEvent): Boolean {
        val value: Boolean = super.onTouchEvent(parent, child, motionEvent)
        log("$value = onTouchEvent motionEvent=$motionEvent")
        return value
    }

    @ColorInt
    override fun getScrimColor(parent: CoordinatorLayout, child: ViewType): Int {
        val value: Int = super.getScrimColor(parent, child)
        log("$value = getScrimColor")
        return value
    }

    @FloatRange(from = 0.0, to = 1.0)
    override fun getScrimOpacity(parent: CoordinatorLayout, child: ViewType): Float {
        val value: Float = super.getScrimOpacity(parent, child)
        log("$value = getScrimOpacity")
        return value
    }

    override fun blocksInteractionBelow(parent: CoordinatorLayout, child: ViewType): Boolean {
        val value: Boolean = super.blocksInteractionBelow(parent, child)
        log("$value = blocksInteractionBelow")
        return value
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ViewType, dependency: View): Boolean {
        val value: Boolean = super.layoutDependsOn(parent, child, dependency)
        log("$value layoutDependsOn child=%s dependency=%s".format(child.javaClass.simpleName, dependency.javaClass.simpleName))
        return value
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ViewType, dependency: View): Boolean {
        val value: Boolean = super.onDependentViewChanged(parent, child, dependency)
        log("$value onDependentViewChanged child=%s dependency=%s".format(child.javaClass.simpleName, dependency.javaClass.simpleName))
        return value
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: ViewType, dependency: View) {
        log("onDependentViewRemoved child=%s dependency=%s".format(child.javaClass.simpleName, dependency.javaClass.simpleName))
        super.onDependentViewRemoved(parent, child, dependency)
    }

    override fun onMeasureChild(parent: CoordinatorLayout, child: ViewType,
                       parentWidthMeasureSpec: Int, widthUsed: Int,
                       parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        val value: Boolean = super.onMeasureChild(parent, child,
                parentWidthMeasureSpec, widthUsed,
                parentHeightMeasureSpec, heightUsed)
        log("$value = onMeasureChild child=%s ".format(child.javaClass.simpleName) +
                "parentWidthMeasureSpec=$parentWidthMeasureSpec widthUsed=$widthUsed" +
                "parentHeightMeasureSpec=$parentHeightMeasureSpec heightUsed=$heightUsed")
        return value
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: ViewType, layoutDirection: Int): Boolean {
        val value: Boolean = super.onLayoutChild(parent, child, layoutDirection)
        log("$value = onLayoutChild child=%s layoutDirection=$layoutDirection".format(child.javaClass.simpleName))
        return value
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                            child: ViewType, directTargetChild: View, target: View,
                            @ViewCompat.ScrollAxis axes: Int, @ViewCompat.NestedScrollType type: Int): Boolean {
        val value: Boolean = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
        log("$value = onStartNestedScroll")
        return value
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout,
                               child: ViewType, directTargetChild: View, target: View,
                               @ViewCompat.ScrollAxis axes: Int, @ViewCompat.NestedScrollType type: Int) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type)
        log("onNestedScrollAccepted")
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout,
                           child: ViewType, target: View, @ViewCompat.NestedScrollType type: Int) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        log("onStopNestedScroll")
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: ViewType,
                       target: View, dxConsumed: Int, dyConsumed: Int,
                       dxUnconsumed: Int, dyUnconsumed: Int, @ViewCompat.NestedScrollType type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        log("onNestedScroll")
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout,
                          child: ViewType, target: View, dx: Int, dy: Int, consumed: IntArray,
                          @ViewCompat.NestedScrollType type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        log("onNestedPreScroll")
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout,
                      child: ViewType, target: View, velocityX: Float, velocityY: Float,
                      consumed: Boolean): Boolean {
        val value: Boolean = super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
        log("$value = onNestedFling")
        return value
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout,
                         child: ViewType, target: View, velocityX: Float, velocityY: Float): Boolean {
        val value: Boolean = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
        log("$value = onNestedPreFling")
        return value
    }

    override fun onApplyWindowInsets(coordinatorLayout: CoordinatorLayout,
                            child: ViewType, insets: WindowInsetsCompat): WindowInsetsCompat {
        log("WindowInsetsCompat = onApplyWindowInsets")
        return super.onApplyWindowInsets(coordinatorLayout, child, insets)
    }

    override fun onRequestChildRectangleOnScreen(coordinatorLayout: CoordinatorLayout,
                                        child: ViewType, rectangle: Rect, immediate: Boolean): Boolean {
        val value: Boolean = super.onRequestChildRectangleOnScreen(coordinatorLayout, child, rectangle, immediate)
        log("$value = onRequestChildRectangleOnScreen")
        return value
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: ViewType, state: Parcelable) {
        log("onRestoreInstanceState")
        super.onRestoreInstanceState(parent, child, state)
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: ViewType): Parcelable {
        log("onSaveInstanceState")
        return super.onSaveInstanceState(parent, child)
    }

    override fun getInsetDodgeRect(parent: CoordinatorLayout, child: ViewType,
                          rect: Rect): Boolean {
        val value: Boolean = super.getInsetDodgeRect(parent, child, rect)
        log("$value = onRequestChildRectangleOnScreen")
        return value
    }
}
