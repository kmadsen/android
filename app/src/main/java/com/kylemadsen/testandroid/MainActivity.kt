package com.kylemadsen.testandroid

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kylemadsen.core.view.ViewGroupController
import com.kylemadsen.core.view.ViewGroupController.Companion.createController
import com.kylemadsen.core.view.ViewRouter

class MainActivity : AppCompatActivity() {
    private val viewRouter = ViewRouter()
    private lateinit var viewGroupController: ViewGroupController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayout = findViewById<LinearLayout>(R.id.container)
        viewGroupController = createController(linearLayout)
        viewGroupController.attach(MainViewController(viewRouter))
    }

    override fun onStart() {
        super.onStart()
        viewRouter.attach(this)
    }

    override fun onStop() {
        viewRouter.detach()
        super.onStop()
    }
}