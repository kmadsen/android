package com.kylemadsen.testandroid.ar

import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.ar.core.ArCoreApk
import com.kylemadsen.testandroid.R
import com.kylemadsen.core.view.ViewController
import com.kylemadsen.testandroid.utils.RetryWithDelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables

class ArAvailabilityController : ViewController {

    private var disposable = Disposables.empty()

    override val layoutId: Int
        get() = R.layout.ar_availability_view

    override fun attach(view: View) {
        val textView: TextView = view.find(R.id.connection_message)

        connectToArCore(view.context, textView)
    }

    private fun connectToArCore(context: Context, textView: TextView) {
        disposable.dispose()
        val retryWithDelay = RetryWithDelay(10, 200)
        disposable = Observable
                .fromCallable { ArCoreApk.getInstance().checkAvailability(context) }
                .retryWhen(retryWithDelay)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { availability ->
                    if (availability.isTransient) {
                        if (retryWithDelay.retriesRemaining > 0) {
                            textView.setText(R.string.ar_availability_checking)
                            throw TransientConnectionException()
                        } else {
                            textView.setText(R.string.ar_availability_unknown)
                        }
                    } else if (availability.isSupported) {
                        textView.setText(R.string.ar_availability_supported)
                    } else if (availability.isUnknown) {
                        textView.setText(R.string.ar_availability_unsupported)
                    } else {
                        textView.setText(R.string.ar_availability_unknown)
                    }
                }
                .map { it.isSupported }
                .onErrorReturn { false }
                .subscribe()
    }

    private inner class TransientConnectionException : RuntimeException()

    override fun detach() {
        disposable.dispose()
    }
}
