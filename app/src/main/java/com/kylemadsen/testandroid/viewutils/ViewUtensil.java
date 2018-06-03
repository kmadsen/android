package com.kylemadsen.testandroid.viewutils;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

public class ViewUtensil {

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" })
    @CheckResult
    @NonNull
    public static <T extends View> T findById(@NonNull View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    @SuppressWarnings({ "unchecked", "UnusedDeclaration" })
    @CheckResult
    @NonNull
    public static <T extends View> T findById(@NonNull Activity activity, @IdRes int id) {
        return (T) activity.findViewById(id);
    }
}
