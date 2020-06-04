package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kylemadsen.testandroid.R

typealias BluetoothDeviceClicked = (BluetoothDevice) -> Unit

class BluetoothDevicesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<ScanResult> = emptyList()
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
        historyHolder.textViewTop.text = """
            rssi: ${dataValue.rssi}
            txPower: ${dataValue.txPower}
            isConnectable: ${dataValue.isConnectable}
            isLegacy: ${dataValue.isLegacy}
            name: ${dataValue.device.name}
            address: ${dataValue.device.address}
            bluetoothClass: ${dataValue.device.bluetoothClass}
            bondState: ${dataValue.device.bondState}
            type: ${dataValue.device.type}
        """.trimIndent()
        historyHolder.itemView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemClicked?.invoke(dataValue.device)
            }
        }
    }

    override fun getItemCount() = data.size
}

class BluetoothDeviceViewHolder(topView: View) : RecyclerView.ViewHolder(topView) {
    val textViewTop: TextView = topView.findViewById(R.id.textViewTop)
    val textViewBottom: TextView = topView.findViewById(R.id.textViewBottom)
}
