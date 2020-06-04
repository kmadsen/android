package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothAdapter
import org.koin.dsl.module

val bluetoothModule = module {

    single {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter
    }

}
