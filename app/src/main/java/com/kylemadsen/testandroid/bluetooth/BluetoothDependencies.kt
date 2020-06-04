package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val bluetoothModule = module {

    single {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter
    }

    single {
        androidApplication().applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

}
