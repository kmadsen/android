package com.kylemadsen.core.localhost

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kylemadsen.core.R
import com.kylemadsen.core.logger.L


typealias LocalhostFileItemClicked = (LocalhostFileItem) -> Unit
typealias AuthTextChanged = (text: String) -> Unit

class LocalhostFileAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var authHistorySource: AuthSourceItem? = null
    var authTextChanged: AuthTextChanged? = null

    var data: List<LocalhostFileItem> = listOf()
    var itemClicked: LocalhostFileItemClicked? = null

    private var authHolder: AuthSourceViewHolder? = null

    fun authClearFocus() {
        authHolder?.linearLayout?.requestFocus()
        authHolder?.textInputEditText?.clearFocus()
        authHolder?.textInputEditText?.hideKeyboard()
        authHolder?.textInputLayout?.clearFocus()
    }

    private fun View.hideKeyboard() {
        getSystemService(this.context, InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AUTH_ITEM_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.localhost_auth_list_item, parent, false)
                if (authHolder != null) {
                    authHolder!!.textInputEditText.removeTextChangedListener(internalTextWatcher)
                }
                authHolder = AuthSourceViewHolder(view)
                authHolder!!.textInputEditText.addTextChangedListener(internalTextWatcher)
                authHolder!!
            }
            DATA_ITEM_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.localhost_files_list_item, parent, false)
                LocalhostFileViewHolder(view)
            }
            else -> throw NotImplementedError("This view type is not supported: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isAuthView(position)) {
            val dataValue = authHistorySource
            val authHolder = holder as AuthSourceViewHolder
            val parameter = Editable.Factory.getInstance().newEditable(dataValue?.parameter)
            authHolder.textInputEditText.text = parameter

        } else {
            val realPosition = if (authHistorySource == null) position else position - 1
            val dataValue = data[realPosition]
            val historyHolder = holder as LocalhostFileViewHolder
            historyHolder.textViewTop.text = dataValue.filename
            historyHolder.textViewBottom.text = dataValue.value
            historyHolder.itemView.setOnClickListener {
                L.i("History item tapped")
                itemClicked?.invoke(dataValue)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAuthView(position)) {
            AUTH_ITEM_TYPE
        } else {
            DATA_ITEM_TYPE
        }
    }

    override fun getItemCount() = if (hasAuthView()) data.size + 1 else data.size

    private val internalTextWatcher: TextWatcher = object : TextWatcher {
        var lastValue: String = ""
        override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
            val text = charSequence.toString()
            if (text != lastValue) {
                this.lastValue = text
                authTextChanged?.invoke(text)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // don't care
        }

        override fun afterTextChanged(s: Editable?) {
            // don't care
        }
    }

    private fun hasAuthView() = authHistorySource != null
    private fun isAuthView(position: Int) = position == 0 && hasAuthView()

    companion object {
        const val AUTH_ITEM_TYPE = 10
        const val DATA_ITEM_TYPE = 20
    }
}

data class AuthSourceItem(
    val parameter: String
)

class AuthSourceViewHolder(topView: View) : RecyclerView.ViewHolder(topView) {
    val textInputLayout: TextInputLayout = topView.findViewById(R.id.textInputLayout)
    val textInputEditText: TextInputEditText = topView.findViewById(R.id.textInputEditText)
    val linearLayout: LinearLayout = topView.findViewById(R.id.linearLayout)
}

data class LocalhostFileItem(
    val filename: String,
    val value: String?
) {
    fun display(): String = "key: $value"
}

class LocalhostFileViewHolder(topView: View) : RecyclerView.ViewHolder(topView) {
    val textViewTop: TextView = topView.findViewById(R.id.textViewTop)
    val textViewBottom: TextView = topView.findViewById(R.id.textViewBottom)
}
