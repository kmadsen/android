package com.kylemadsen.core.localhost

import com.kylemadsen.core.logger.L
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LocalhostFileViewController {

    private val historyFilesApi = LocalhostFilesApi()

    var viewAdapter: LocalhostFileAdapter? = null

    fun attach(viewAdapter: LocalhostFileAdapter) {
        this.viewAdapter = viewAdapter

        viewAdapter.itemClicked = { historyFileItem ->
            requestHistoryData(historyFileItem)
        }
    }

    fun requestHistory(viewAdapter: LocalhostFileAdapter, connected: (Boolean) -> Unit) {
        L.i("requestHistory")
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
            connected.invoke(false)
        }
        CoroutineScope(Dispatchers.Main).launch(handler) {
            val task = async(Dispatchers.IO) {
                return@async historyFilesApi.requestHistory()
            }
            val drives = task.await()
            connected.invoke(true)
            viewAdapter.data = drives.toList()
            viewAdapter.notifyDataSetChanged()
        }
    }

    private fun requestHistoryData(filePath: FilePath) {
        L.i("requestHistoryFile")
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {
            val task = async(Dispatchers.IO) {
                return@async historyFilesApi.requestDataJson(filePath.path)
            }
            val data = task.await()
            L.i("requestHistoryFile result: $data")
        }
    }
}
