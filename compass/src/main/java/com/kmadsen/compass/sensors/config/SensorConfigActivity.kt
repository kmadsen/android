package com.kmadsen.compass.sensors.config

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kmadsen.compass.R
import com.kmadsen.compass.sensors.SensorEventViewModel
import com.kylemadsen.core.logger.L

import kotlinx.android.synthetic.main.activity_sensor_config.*

class SensorConfigActivity : AppCompatActivity() {

    private val viewAdapter = SensorConfigAdapter()
    private var sensorViewModel: SensorEventViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_config)
        setSupportActionBar(toolbar)

        val viewManager = LinearLayoutManager(recyclerView.context)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewAdapter.itemClicked = {
            L.i("sensor_debug sensor clicked")
        }

        val sensorViewModel = SensorEventViewModel.get(this)
        this.sensorViewModel = sensorViewModel

        sensorViewModel.loadSensorConfigs { sensorConfigs ->
            viewAdapter.data = sensorConfigs
        }
    }

}
