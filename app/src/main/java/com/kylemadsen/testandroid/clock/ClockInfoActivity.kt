package com.kylemadsen.testandroid.clock

import android.os.Bundle
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.kylemadsen.core.time.DeviceClock

import com.kylemadsen.testandroid.R

class ClockInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clock_info_activity)

        val clockInfoText: TextView = findViewById(R.id.clock_info)
        clockInfoText.text = DeviceClock.getClockDriftInfo()
        val resetDriftButton: Button = findViewById(R.id.clock_info_rest)
        resetDriftButton.setOnClickListener {
            DeviceClock.resetClockDriftInfo()
            clockInfoText.text = DeviceClock.getClockDriftInfo()
        }
    }
}
