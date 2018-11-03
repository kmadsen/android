package com.kylemadsen.testandroid.animation;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.ImageView;

import com.kylemadsen.testandroid.R;
import com.kylemadsen.core.view.ViewController;
import com.kylemadsen.testandroid.utils.ViewUtensil;

public class AnimationController implements ViewController {

    @Override
    public int getLayoutId() {
        return R.layout.animation_view;
    }

    @Override
    public void attach(final View view) {
        final ImageView imageView = ViewUtensil.findById(view, R.id.image_view);
        ((Animatable) imageView.getDrawable()).start();
    }

    @Override
    public void detach() {

    }
}
