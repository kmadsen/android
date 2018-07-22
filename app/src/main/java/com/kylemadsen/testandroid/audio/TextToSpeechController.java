package com.kylemadsen.testandroid.audio;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kylemadsen.core.logger.L;
import com.kylemadsen.testandroid.R;
import com.kylemadsen.testandroid.ViewController;
import com.kylemadsen.testandroid.utils.ViewUtensil;

import io.reactivex.Observable;

public class TextToSpeechController implements ViewController {

    private TextToSpeech textToSpeech;

    @Override
    public int getLayoutId() {
        return R.layout.animation_text_to_speech;
    }

    @Override
    public void attach(final View view) {
        textToSpeech = new TextToSpeech(view.getContext());

        final ImageView imageView = ViewUtensil.findById(view, R.id.audio_speaker);
        ((Animatable) imageView.getDrawable()).start();

        TextView withDashView = ViewUtensil.findById(view, R.id.with_dash_button);
        TextView withPeriodView = ViewUtensil.findById(view, R.id.with_period_button);

        L.i("set click listeners");
        withDashView.setOnClickListener(v -> {
            L.i("dash clicked");
            observeSpeaking("This is a test message - do you hear it?")
                    .subscribe(isSpeaking -> {
                        L.i("is speaking with dash %s", String.valueOf(isSpeaking));
                    });
        });

        withPeriodView.setOnClickListener(v -> {
            L.i("period clicked");
            observeSpeaking("This is a test message. Do you hear it?")
                    .subscribe(isSpeaking -> {
                        L.i("is speaking with period %s", String.valueOf(isSpeaking));
                    });
        });
        L.i("complete it what the fuck");

    }

    @Override
    public void detach() {
        textToSpeech.shutdown();
        textToSpeech = null;
    }

    private Observable<Boolean> observeSpeaking(String message) {
        return textToSpeech.observeSpeak(message)
                .map(textToSpeechResult -> !textToSpeechResult.isDone())
                .distinctUntilChanged();
    }
}
