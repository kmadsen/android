package com.kmadsen.compass.sensors.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kmadsen.compass.R
import com.kmadsen.compass.sensors.SensorEventViewModel
import com.kylemadsen.core.logger.L
import kotlinx.android.synthetic.main.sensor_data_config_layout.*
import kotlinx.coroutines.launch

class SensorConfigFragment : Fragment() {

    private val viewAdapter = SensorConfigAdapter()
    private var sensorViewModel: SensorEventViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sensor_data_config_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewManager = LinearLayoutManager(recyclerView.context)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewAdapter.itemClicked = {
            L.i("sensor_debug sensor clicked")
        }

        val sensorViewModel = ViewModelProvider(this)
            .get(SensorEventViewModel::class.java)
        this.sensorViewModel = sensorViewModel

//        sensorViewModel.loadSensorConfigs { sensorConfigs ->
//            viewAdapter.data = sensorConfigs
//        }
    }
}