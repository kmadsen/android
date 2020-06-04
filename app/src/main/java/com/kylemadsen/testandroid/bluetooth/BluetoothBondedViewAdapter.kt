package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kylemadsen.testandroid.R

class BluetoothBondedViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<BluetoothDevice> = emptyList()
    var itemClicked: BluetoothDeviceClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_select_list_item, parent, false)
        return BluetoothDeviceViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataValue = data[position]
        val historyHolder = holder as BluetoothDeviceViewHolder
        historyHolder.textViewTop.text = BluetoothLeScanner.prettyPrint(dataValue)
        historyHolder.itemView.setOnClickListener {
            itemClicked?.invoke(dataValue)
        }
    }

    override fun getItemCount() = data.size
}
