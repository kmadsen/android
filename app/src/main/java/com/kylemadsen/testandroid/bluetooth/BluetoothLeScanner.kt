package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import com.kylemadsen.core.logger.L

class BluetoothLeScanner {

    val mutableMap: MutableMap<BluetoothDevice, ScanResult> = mutableMapOf()
    var listener: Listener? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            L.i("debug_bluetooth onScanResult ${result?.rssi}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                L.i("debug_bluetooth isConnectable ${result?.isConnectable}")
                L.i("debug_bluetooth txPower ${result?.txPower}")
            }
            result?.device?.let {
                mutableMap[it] = result
                listener?.onUpdate(mutableMap, it)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

            L.i("debug_bluetooth onBatchScanResults size = ${results?.size}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            L.i("debug_bluetooth onScanFailed errorCode = $errorCode")
        }
    }

    interface Listener {
        fun onUpdate(devices: Map<BluetoothDevice, ScanResult>, deviceUpdated: BluetoothDevice)
    }
}