package com.kylemadsen.core.localhost

import com.kylemadsen.core.logger.L
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

interface ipAddressStorage {
    var serverIpAddress: String
}

class LocalhostFileViewController(
    private val ipAddressStorage: ipAddressStorage
) {

    var ipAddress = ipAddressStorage.serverIpAddress
    var viewAdapter: LocalhostFileAdapter? = null
    val historyFilesApi = LocalhostFilesApi()

    fun attach(viewAdapter: LocalhostFileAdapter) {
        val lastSavedIpAddress = ipAddressStorage.serverIpAddress
        this.viewAdapter = viewAdapter
        viewAdapter.authHistorySource = AuthSourceItem(lastSavedIpAddress)
        viewAdapter.authTextChanged = { ipAddress: String ->
            this.ipAddress = ipAddress
        }
        viewAdapter.itemClicked = { historyFileItem ->
            requestHistoryData(historyFileItem)
        }
    }

    fun connectToLocalhost(connectedCallback: (Boolean) -> Unit) {
        viewAdapter?.let {
            it.authClearFocus()
            val saveIpAddress: (Boolean) -> Unit = { connected ->
                if (connected) saveIpAddress()
                connectedCallback.invoke(connected)
            }
            requestHistory(it, saveIpAddress)
        }
        connectedCallback.invoke(false)
    }

    fun saveIpAddress() {
        ipAddressStorage.serverIpAddress = ipAddress
    }

    private fun requestHistoryData(localhostFileItem: LocalhostFileItem) {
        L.i("requestHistoryFile")
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            val task = async(Dispatchers.IO) {
                return@async historyFilesApi.requestDataJson(ipAddress, localhostFileItem.filename)
            }
            val data = task.await()
            L.i("requestHistoryFile result: $data")
        }
    }

    private fun requestHistory(viewAdapter: LocalhostFileAdapter, connected: (Boolean) -> Unit) {
        L.i("requestHistory")
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
            connected.invoke(false)
        }
        CoroutineScope(Dispatchers.Main).launch(handler) {
            val task = async(Dispatchers.IO) {
                return@async historyFilesApi.requestHistory(ipAddressStorage.serverIpAddress)
            }
            val drives = task.await()
            connected.invoke(true)
            viewAdapter.data = drives.mapIndexed { index, filename ->
                LocalhostFileItem(filename, index.toString())
            }
            viewAdapter.notifyDataSetChanged()
        }
    }
}
