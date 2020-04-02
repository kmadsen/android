package com.kylemadsen.core.localhost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kylemadsen.core.R
import com.kylemadsen.core.logger.L

typealias LocalhostFileItemClicked = (FilePath) -> Unit

class LocalhostFileAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<FilePath> = listOf()
    var itemClicked: LocalhostFileItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.localhost_files_list_item, parent, false)
        return LocalhostFileViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataValue = data[position]
        val historyHolder = holder as LocalhostFileViewHolder
        historyHolder.textViewTop.text = dataValue.title
        historyHolder.textViewBottom.text = dataValue.path
        historyHolder.itemView.setOnClickListener {
            L.i("History item tapped")
            itemClicked?.invoke(dataValue)
        }
    }

    override fun getItemCount() = data.size
}

class LocalhostFileViewHolder(topView: View) : RecyclerView.ViewHolder(topView) {
    val textViewTop: TextView = topView.findViewById(R.id.textViewTop)
    val textViewBottom: TextView = topView.findViewById(R.id.textViewBottom)
}
