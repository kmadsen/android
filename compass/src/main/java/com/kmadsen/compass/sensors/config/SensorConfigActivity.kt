package com.kmadsen.compass.sensors.config

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kmadsen.compass.R
import com.kylemadsen.core.logger.L
import kotlinx.android.synthetic.main.compass_sensor_config_activity.*
import org.koin.android.ext.android.inject

class SensorConfigActivity : AppCompatActivity() {

    private val viewAdapter = SensorConfigAdapter()
    private val sensorConfigManager: SensorConfigManager by inject()

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
            L.i("sensor clicked ${it.sensor.name}")
        }

        viewAdapter.data = SensorConfigManager.savedSensorConfigs
    }

    override fun onPause() {
        sensorConfigManager.saveSensorConfigs(viewAdapter.data)
        super.onPause()
    }
}
