package com.kylemadsen.core.localhost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kylemadsen.core.R

class LocalhostFilesActivity : AppCompatActivity() {

    var viewController: LocalhostFileViewController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.localhost_files_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val viewManager = LinearLayoutManager(recyclerView.context)
        val viewAdapter = LocalhostFileAdapter()
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewController = LocalhostFileViewController()
        viewController!!.attach(viewAdapter)

        viewController?.requestHistory(viewAdapter) { connected ->
            Snackbar.make(recyclerView, "Successful connection $connected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}
