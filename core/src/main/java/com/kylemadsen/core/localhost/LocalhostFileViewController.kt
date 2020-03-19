package com.kylemadsen.core.localhost

import com.kylemadsen.core.logger.L
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
    val historyFilesApi = LocalhostFilesApi()

    fun attach(viewAdapter: LocalhostFileAdapter) {
        val lastSavedIpAddress = ipAddressStorage.serverIpAddress
        viewAdapter.authHistorySource = AuthSourceItem(lastSavedIpAddress)
        viewAdapter.authTextChanged = { ipAddress: String ->
            this.ipAddress = ipAddress
            if (ipAddress.length == AUTH_SUCCESS_TEXT_LENGTH) {
                if (ipAddress.matches(ipAddressRegex)) {
                    ipAddressStorage.serverIpAddress = ipAddress
                    viewAdapter.authClearFocus()
                    requestHistory(viewAdapter)
                } else {
                    L.e("Auth text is failing $ipAddress")
                }
            }
        }
        viewAdapter.itemClicked = { historyFileItem ->
            requestHistoryData(historyFileItem)
        }
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

    private fun requestHistory(viewAdapter: LocalhostFileAdapter) {
        L.i("requestHistory")
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            val task = async(Dispatchers.IO) {
                return@async historyFilesApi.requestHistory(ipAddressStorage.serverIpAddress)
            }
            val drives = task.await()
            viewAdapter.data = drives.mapIndexed { index, filename ->
                LocalhostFileItem(filename, index.toString())
            }
            viewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val AUTH_SUCCESS_TEXT_LENGTH = "xxx.xxx.xxx.xxx".length // 15
        val ipAddressRegex: Regex = Regex("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")
    }
}
