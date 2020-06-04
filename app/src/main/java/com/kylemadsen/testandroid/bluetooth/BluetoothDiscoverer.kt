package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.Closeable

class BluetoothDiscoverer(
    private val listener: Listener
) : BroadcastReceiver(), Closeable {

    private var context: Context? = null

    fun start(context: Context) {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        context.registerReceiver(this, filter)
        this.context = context
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    listener.onDeviceDiscovered(device)
                }
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                listener.onDeviceDiscoveryEnd()
            }
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                listener.onStateChanged()
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                listener.onDevicePairingEnded()
            }
        }
    }

    override fun close() {
        context?.unregisterReceiver(this);
    }

    interface Listener {
        fun onDeviceDiscovered(device: BluetoothDevice)
        fun onDeviceDiscoveryEnd()
        fun onStateChanged()
        fun onDevicePairingEnded()
    }
}
