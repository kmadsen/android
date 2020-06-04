package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.Context


class BluetoothConnectedDevices {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothProfile: BluetoothProfile? = null

    fun connect(context: Context, bluetoothAdapter: BluetoothAdapter?, callback: (BluetoothProfile) -> Unit) {
        this.bluetoothAdapter = bluetoothAdapter ?: return

        bluetoothAdapter.getProfileProxy(context.applicationContext, object : ServiceListener {
            override fun onServiceDisconnected(profile: Int) {
                bluetoothProfile = null
            }

            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                bluetoothProfile = proxy
                proxy?.let(callback)
            }

        }, BluetoothProfile.A2DP)
    }

    fun close() {
        bluetoothProfile?.let {
            bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, it)
        }
        bluetoothProfile = null
        bluetoothAdapter = null
    }
}