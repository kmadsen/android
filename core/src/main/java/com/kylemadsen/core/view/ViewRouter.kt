package com.kylemadsen.core.view

import android.app.Activity
import android.content.Intent

class ViewRouter {

    private var activity: Activity? = null

    fun attach(activity: Activity) {
        this.activity = activity
    }

    fun detach() {
        this.activity = null
    }

    fun goToActivity(activityClass: Class<*>) {
        val intent = Intent(activity, activityClass)
        activity!!.startActivity(intent)
    }
}