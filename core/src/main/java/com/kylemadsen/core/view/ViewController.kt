package com.kylemadsen.core.view

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

interface ViewController {

    @get:LayoutRes
    val layoutId: Int

    fun attach(view: View)

    fun detach()

    fun <ViewType : View?> View.find(@IdRes viewResource: Int): ViewType {
        return this.findViewById<ViewType>(viewResource)!!
    }
}
