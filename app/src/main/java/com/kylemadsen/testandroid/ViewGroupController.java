package com.kylemadsen.testandroid;

import android.support.annotation.MainThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewGroupController {

    private final List<ViewController> viewControllers = new ArrayList<>();

    private ViewGroup viewGroup;
    private LayoutInflater layoutInflater;

    private ViewGroupController(ViewGroup viewGroup, LayoutInflater layoutInflater) {
        this.viewGroup = viewGroup;
        this.layoutInflater = layoutInflater;
    }

    @MainThread
    public static ViewGroupController onCreate(ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new ViewGroupController(viewGroup, layoutInflater);
    }

    @MainThread
    public <ViewControllerType extends ViewController> void attach(ViewControllerType viewController) {
        View view = layoutInflater.inflate(viewController.getLayoutId(), viewGroup, true);
        viewController.attach(view);
        viewControllers.add(viewController);
    }

    @MainThread
    public void onDestroy() {
        for (ViewController viewController : viewControllers) {
            viewController.detach();
        }
        viewControllers.clear();
        viewGroup.removeAllViews();
        viewGroup = null;
    }
}
