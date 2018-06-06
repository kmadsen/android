package com.kylemadsen.testandroid.audio;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.kylemadsen.testandroid.R;
import com.kylemadsen.testandroid.ViewController;
import com.kylemadsen.testandroid.utils.ViewUtensil;

import java.util.HashMap;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

public class TextToSpeechController implements ViewController {

    private TextToSpeech textToSpeech;

    @Override
    public int getLayoutId() {
        return R.layout.text_to_speech_view;
    }

    @Override
    public void attach(final View view) {
        final ImageView imageView = ViewUtensil.findById(view, R.id.audio_speaker);
        ((Animatable) imageView.getDrawable()).start();

        textToSpeech = new TextToSpeech(view.getContext(), status -> { });
        TextView withDashView = ViewUtensil.findById(view, R.id.with_dash_button);
        TextView withPeriodView = ViewUtensil.findById(view, R.id.with_period_button);

        withDashView.setOnClickListener(v -> {
            speakMessage("This is a Shared ride. You might have multiple pickups on the same route. Just follow the app. it will determine the pickup and dropoff order and direct you to each stop.");
        });

        withPeriodView.setOnClickListener(v -> {
            speakMessage("This is a Shared ride. You might have multiple pickups on the same route. Just follow the app. It will determine the pickup and dropoff order and direct you to each stop.");
        });
    }

    @Override
    public void detach() {
        textToSpeech.stop();
        textToSpeech.shutdown();
        textToSpeech = null;
    }

    private void speakMessage(String message) {
        final String utteranceId = UUID.randomUUID().toString();
        Bundle params = new Bundle();
        textToSpeech.speak(message, QUEUE_FLUSH, params, utteranceId);
    }
}
