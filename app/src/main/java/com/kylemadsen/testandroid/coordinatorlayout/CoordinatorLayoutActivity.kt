package com.kylemadsen.testandroid.coordinatorlayout

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.kylemadsen.core.view.ViewGroupController
import com.kylemadsen.testandroid.R

class CoordinatorLayoutActivity : AppCompatActivity() {

    private var viewGroupController: ViewGroupController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coordinator_layout_activity)

        val coordinatorLayout: CoordinatorLayout = findViewById(R.id.coordinator_layout)
        val snackBarButton: Button = findViewById(R.id.show_snack_bar_button)
        snackBarButton.setOnClickListener {
            Snackbar.make(coordinatorLayout, "Hey there!", Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    override fun onDestroy() {
        viewGroupController?.onDestroy()

        super.onDestroy()
    }
}
