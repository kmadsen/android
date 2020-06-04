package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context

class BluetoothDevicesViewController {

    private var viewAdapter: BluetoothDevicesAdapter? = null
//    private val historyFilesApi = HistoryFilesClient()

    fun attach(context: Context, viewAdapter: BluetoothDevicesAdapter, result: (BluetoothDevice) -> Unit) {
        this.viewAdapter = viewAdapter
        viewAdapter.itemClicked = { bluetoothDevice ->
//            if (historyFileItem.isOnDisk()) {
////                requestFromDisk(context.applicationContext, historyFileItem, result)
//            } else {
////                requestHistoryData(historyFileItem, result)
//            }
        }
    }
}
