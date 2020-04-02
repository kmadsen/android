package com.kylemadsen.core.localhost

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kylemadsen.core.R


@SuppressLint("HardwareIds")
class LocalhostFilesActivity : AppCompatActivity() {

    var viewController: LocalhostFileViewController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.localhost_files_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            viewController?.connectToLocalhost { connected ->
                Snackbar.make(view, "Successful connection $connected", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val viewManager = LinearLayoutManager(recyclerView.context)
        val viewAdapter = LocalhostFileAdapter()
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

//        val viewController = FirebaseHistoryViewController(this)
//        viewController.attach(viewAdapter)

        val ipAddressStorage = IpAddressStorage(this)
        viewController = LocalhostFileViewController(ipAddressStorage)
        viewController!!.attach(viewAdapter)
    }

    override fun onStop() {
        viewController?.saveIpAddress()
        super.onStop()
    }

    class IpAddressStorage(
        val context: Context
    ): ipAddressStorage {
        private val keyValue = "localhost_ip_address_preference_key"
        private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        override var serverIpAddress: String = ""
            get() = sharedPreferences.getString(keyValue, "") ?: ""
            set(value) {
                if (field != value) {
                    field = value
                    sharedPreferences
                        .edit().putString(keyValue, value)
                        .apply()
                }
            }
    }
}
