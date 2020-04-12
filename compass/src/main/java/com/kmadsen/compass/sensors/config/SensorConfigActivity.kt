package com.kmadsen.compass.sensors.config

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kmadsen.compass.R
import com.kmadsen.compass.sensors.CompassSensorManager
import com.kmadsen.compass.sensors.SensorEventViewModel
import com.kylemadsen.core.logger.L

import kotlinx.android.synthetic.main.compass_sensor_config_activity.*
import org.koin.android.ext.android.inject

class SensorConfigActivity : AppCompatActivity() {

    private val viewAdapter = SensorConfigAdapter()
    private var sensorViewModel: SensorEventViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compass_sensor_config_activity)
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

    override fun onStop() {
        sensorViewModel?.saveSensorConfigs(viewAdapter.data)

        super.onStop()
    }

}
