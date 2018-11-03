package com.kylemadsen.core.view

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View

interface ViewController {

    @get:LayoutRes
    val layoutId: Int

    fun attach(view: View)

    fun detach()

    fun <ViewType : View?> View.find(@IdRes viewResource: Int): ViewType {
        return this.findViewById<ViewType>(viewResource)!!
    }
}
