package com.kylemadsen.testandroid.animation

import android.graphics.drawable.Animatable
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.kylemadsen.core.logger.L
import com.kylemadsen.testandroid.R
import com.kylemadsen.core.view.ViewController

import io.reactivex.Observable

class TextToSpeechController : ViewController {

    private var textToSpeech: TextToSpeech? = null

    override val layoutId: Int
        get() = R.layout.animation_text_to_speech

    override fun attach(view: View) {
        textToSpeech = TextToSpeech(view.context)

        val imageView: ImageView = view.find(R.id.audio_speaker)
        (imageView.drawable as Animatable).start()

        val withDashView: TextView = view.find(R.id.with_dash_button)
        val withPeriodView: TextView = view.find(R.id.with_period_button)

        L.i("set click listeners")
        withDashView.setOnClickListener {
            L.i("dash clicked")
            observeSpeaking("This is a test message - do you hear it?")
                    .subscribe { isSpeaking -> L.i("is speaking with dash %s", isSpeaking.toString()) }
        }

        withPeriodView.setOnClickListener {
            L.i("period clicked")
            observeSpeaking("This is a test message. Do you hear it?")
                    .subscribe { isSpeaking -> L.i("is speaking with period %s", isSpeaking.toString()) }
        }
    }

    override fun detach() {
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    private fun observeSpeaking(message: String): Observable<Boolean> {
        return textToSpeech!!.observeSpeak(message)
                .map { textToSpeechResult -> !textToSpeechResult.isDone }
                .distinctUntilChanged()
    }
}
