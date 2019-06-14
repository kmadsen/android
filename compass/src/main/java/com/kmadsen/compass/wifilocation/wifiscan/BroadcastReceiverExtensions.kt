package com.kmadsen.compass.wifilocation.wifiscan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

fun Context.observe(intentFilter: IntentFilter): Observable<Intent> {
    return Observable.create { emitter ->
        val receiver = Receiver(emitter)
        val firstStickyIntent: Intent? = registerReceiver(receiver, intentFilter)
        emitter.setCancellable { unregisterReceiver(receiver) }
        if (firstStickyIntent != null) {
            receiver.onReceive(firstStickyIntent)
        }
    }
}

private class Receiver(
    private val emitter: ObservableEmitter<Intent>
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        onReceive(intent)
    }

    fun onReceive(intent: Intent) {
        if (emitter.isDisposed) return

        emitter.onNext(intent)
    }
}

