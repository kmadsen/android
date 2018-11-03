package com.kylemadsen.testandroid.animation;

import java.util.Objects;

public class TextToSpeechResult {
    private static final int RESULT_KIND_START = 1;
    private static final int RESULT_KIND_SUCCESS = 2;
    private static final int RESULT_KIND_ERROR = 3;

    private final int resultKind;
    private final String id;

    TextToSpeechResult(int resultKind, String id) {
        this.resultKind = resultKind;
        this.id = id;
    }

    static TextToSpeechResult start(String id) {
        return new TextToSpeechResult(RESULT_KIND_START, id);
    }

    static TextToSpeechResult error(String id) {
        return new TextToSpeechResult(RESULT_KIND_ERROR, id);
    }

    static TextToSpeechResult success(String id) {
        return new TextToSpeechResult(RESULT_KIND_SUCCESS, id);
    }

    String getUtteranceId() {
        return id;
    }

    public boolean isStart() {
        return resultKind == RESULT_KIND_START;
    }

    public boolean isSuccess() {
        return resultKind == RESULT_KIND_SUCCESS;
    }

    public boolean isError() {
        return resultKind == RESULT_KIND_ERROR;
    }

    public boolean isDone() {
        return resultKind == RESULT_KIND_SUCCESS || resultKind == RESULT_KIND_ERROR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TextToSpeechResult that = (TextToSpeechResult) o;
        return resultKind == that.resultKind &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultKind, id);
    }
}
