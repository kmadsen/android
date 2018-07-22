package com.kylemadsen.testandroid.audio;

import android.content.Context;
import android.speech.tts.UtteranceProgressListener;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.kylemadsen.core.logger.L;
import com.kylemadsen.testandroid.utils.Unit;

import java.util.HashMap;
import java.util.UUID;

import io.reactivex.Observable;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static android.speech.tts.TextToSpeech.SUCCESS;

public class TextToSpeech {

    private final android.speech.tts.TextToSpeech textToSpeech;

    private final BehaviorRelay<Unit> textToSpeechInitializedSubject = BehaviorRelay.create();
    private final BehaviorRelay<TextToSpeechResult> utteranceProgressRelay = BehaviorRelay.create();

    public TextToSpeech(Context context) {
        this.textToSpeech = new android.speech.tts.TextToSpeech(context, onInitListener);
        this.textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
    }

    public Observable<TextToSpeechResult> observeSpeak(final String phrase) {
        final String utteranceId = UUID.randomUUID().toString();
        final HashMap<String, String> params = new HashMap<>(1);
        params.put(KEY_PARAM_UTTERANCE_ID, utteranceId);
        return Observable.just(utteranceId)
                .doOnNext(value -> textToSpeech.speak(phrase, QUEUE_FLUSH, params))
                .flatMap(this::observeResultsAndInterruptions)
                .distinctUntilChanged();
    }

    public void speak(final String phrase) {
        final String utteranceId = UUID.randomUUID().toString();
        final HashMap<String, String> params = new HashMap<>(1);
        params.put(KEY_PARAM_UTTERANCE_ID, utteranceId);
        textToSpeech.speak(phrase, QUEUE_FLUSH, params);
    }

    private Observable<TextToSpeechResult> observeResultsAndInterruptions(String utteranceId) {
        return utteranceProgressRelay
                .doOnNext(textToSpeechResult -> {
                    if (textToSpeechResult.isStart() && !utteranceId.equals(textToSpeechResult.getUtteranceId())) {
                        utteranceProgressRelay.accept(TextToSpeechResult.error(utteranceId));
                    }
                })
                .filter(result -> utteranceId.equals(result.getUtteranceId()));
    }

    private final android.speech.tts.TextToSpeech.OnInitListener onInitListener = status -> {
        if (status == SUCCESS) {
            textToSpeechInitializedSubject.accept(Unit.create());
        } else {
            L.w("Initialization of TextToSpeech failed with status : %s", status);
        }
    };

    private final UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            utteranceProgressRelay.accept(TextToSpeechResult.start(utteranceId));
        }

        @Override
        public void onDone(String utteranceId) {
            utteranceProgressRelay.accept(TextToSpeechResult.success(utteranceId));
        }

        @Override
        public void onError(String utteranceId) {
            utteranceProgressRelay.accept(TextToSpeechResult.error(utteranceId));
        }
    };

    public void shutdown() {
        textToSpeech.stop();
        textToSpeech.shutdown();
    }
}
