package com.kylemadsen.testandroid;

import android.support.annotation.LayoutRes;
import android.view.View;

public interface ViewController {

    @LayoutRes
    int getLayoutId();

    void attach(View view);

    void detach();
}
