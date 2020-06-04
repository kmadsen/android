package com.kylemadsen.testandroid.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kylemadsen.testandroid.R
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules

class BluetoothDevicesActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by inject()
    private var filesViewController: BluetoothDevicesViewController? = null
    private val viewAdapter = BluetoothDevicesAdapter()
    private val bluetoothLeScanner = BluetoothLeScanner()

    override fun onCreate(savedInstanceState: Bundle?) {
        loadKoinModules(bluetoothModule)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.bluetooth_select_activity)

        if (bluetoothAdapter == null) {
            throw NotImplementedError("This device does not have bluetooth")
        }

        if (bluetoothAdapter?.isEnabled == false) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
            )
        } else {
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            val viewManager = LinearLayoutManager(recyclerView.context)

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

            filesViewController = BluetoothDevicesViewController()
            filesViewController!!.attach(this, viewAdapter) { bluetoothDevice ->
    //                launchRideReplay(historyDataResponse)
            }

            val fab: FloatingActionButton = findViewById(R.id.fab)
            fab.setOnClickListener {
                // TODO
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothLeScanner.listener = object : BluetoothLeScanner.Listener {
                    override fun onUpdate(
                        devices: Map<BluetoothDevice, ScanResult>,
                        deviceUpdated: BluetoothDevice
                    ) {
                        viewAdapter.data = devices.values.toList()
                        viewAdapter.notifyDataSetChanged()
                    }
                }
                bluetoothAdapter?.bluetoothLeScanner?.startScan(bluetoothLeScanner.scanCallback)
            } else {
                throw NotImplementedError("This bluetooth scanner is not implemented")
            }
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(bluetoothLeScanner.scanCallback)
        }

        super.onDestroy()
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }
}
