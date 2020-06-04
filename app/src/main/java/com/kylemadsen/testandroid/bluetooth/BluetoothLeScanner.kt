package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import com.kylemadsen.core.logger.L

class BluetoothLeScanner {
    private var listener: Listener? = null

    val mutableMap: MutableMap<BluetoothDevice, ScanResult> = mutableMapOf()

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

    fun startScanning(_bluetoothAdapter: BluetoothAdapter?, dataSetChanged: (Map<BluetoothDevice, ScanResult>) -> Unit) {
        val bluetoothAdapter = _bluetoothAdapter
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listener = object : Listener {
                override fun onUpdate(
                    devices: Map<BluetoothDevice, ScanResult>,
                    deviceUpdated: BluetoothDevice
                ) {
                    dataSetChanged(devices)
                }
            }
            bluetoothAdapter.bluetoothLeScanner?.startScan(scanCallback)
        } else {
            throw NotImplementedError("This bluetooth scanner is not implemented")
        }
    }

    fun stopScanning(_bluetoothAdapter: BluetoothAdapter?) {
        val bluetoothAdapter = _bluetoothAdapter
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        }
    }

    interface Listener {
        fun onUpdate(devices: Map<BluetoothDevice, ScanResult>, deviceUpdated: BluetoothDevice)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun prettyPrint(dataValue: ScanResult): String {
            return """
                rssi: ${dataValue.rssi}
                txPower: ${dataValue.txPower}
                isConnectable: ${dataValue.isConnectable}
                isLegacy: ${dataValue.isLegacy}
                name: ${dataValue.device.name}
                address: ${dataValue.device.address}
                bluetoothClass: ${dataValue.device.bluetoothClass}
                bondState: ${bondState(dataValue.device.bondState)}
                type: ${dataValue.device.type}
            """.trimIndent()
        }

        fun prettyPrint(dataValue: BluetoothDevice): String {
            return """
                name: ${dataValue.name}
                address: ${dataValue.address}
                bluetoothClass: ${dataValue.bluetoothClass}
                bondState: ${bondState(dataValue.bondState)}
                type: ${dataValue.type}
            """.trimIndent()
        }

        private fun bondState(bondState: Int): String {
            return when (bondState) {
                BluetoothDevice.BOND_NONE -> "NONE"
                BluetoothDevice.BOND_BONDED -> "BONDED"
                BluetoothDevice.BOND_BONDING -> "BONDING"
                else -> "unknown"
            }
        }
    }
}