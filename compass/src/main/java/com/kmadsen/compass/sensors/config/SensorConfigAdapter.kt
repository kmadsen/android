package com.kmadsen.compass.sensors.config

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kmadsen.compass.R

typealias SensorConfigItemClicked = (SensorConfig) -> Unit

class SensorConfigAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<SensorConfig> = listOf()
    var itemClicked: SensorConfigItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sensor_data_config_item, parent, false)
        return HistoryFileViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataValue = data[position]
        val historyHolder = holder as HistoryFileViewHolder
        historyHolder.itemView.setOnClickListener {
            itemClicked?.invoke(dataValue)
        }

        historyHolder.setSensorConfig(dataValue)
        historyHolder.textViewBottom.text = """
            Power:${dataValue.sensor.power}mA"
            Resolution:${dataValue.sensor.resolution}"
        """.trimIndent()
        // TODO config with this? Resolution:${dataValue.sensor.highestDirectReportRateLevel}\n"
        historyHolder.seekBarValue.text = dataValue.preference.signalsPerSecond.toString()
        historyHolder.seekBar.progress = dataValue.preference.signalsPerSecond
        val sensorUpdate: (Int) -> Unit = {
            dataValue.preference.signalsPerSecond = it
            historyHolder.seekBarValue.text = it.toString()
        }
        sensorUpdate(dataValue.preference.signalsPerSecond)
        historyHolder.setSeekBarUpdate(sensorUpdate)
    }

    override fun getItemCount() = data.size
}

class HistoryFileViewHolder(topView: View) : RecyclerView.ViewHolder(topView) {
    val textViewTop: TextView = topView.findViewById(R.id.textViewTop)
    val textViewBottom: TextView = topView.findViewById(R.id.textViewBottom)
    val seekBarValue: TextView = topView.findViewById(R.id.seekBarValue)
    val seekBarMinValue: TextView = topView.findViewById(R.id.seekBarMinValue)
    val seekBarMaxValue: TextView = topView.findViewById(R.id.seekBarMaxValue)
    val seekBar: SeekBar = topView.findViewById(R.id.seekBar)

    fun setSensorConfig(sensorConfig: SensorConfig) {
        textViewTop.text = sensorConfig.sensor.name

        val minValue = sensorConfig.minEventsPerSecond ?: 0
        takeIfSdk(26) { seekBar.min = minValue }
        seekBarMinValue.text = minValue.toString()

        val maxValue = sensorConfig.maxEventsPerSecond ?: 50
        seekBar.max = maxValue
        seekBarMaxValue.text = maxValue.toString()
    }

    fun setSeekBarUpdate(function: (Int) -> Unit) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                function(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
    }
}


