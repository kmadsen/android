package com.kylemadsen.testandroid.ar;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.google.ar.core.ArCoreApk;
import com.kylemadsen.testandroid.R;
import com.kylemadsen.core.view.ViewController;
import com.kylemadsen.testandroid.utils.RetryWithDelay;
import com.kylemadsen.testandroid.utils.ViewUtensil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class ArAvailabilityController implements ViewController {

    private Disposable disposable = Disposables.empty();

    @Override
    public int getLayoutId() {
        return R.layout.ar_availability_view;
    }

    @Override
    public void attach(View view) {
        TextView textView = ViewUtensil.findById(view, R.id.connection_message);

        disposable = connectToArCore(view.getContext(), textView);
    }

    private Disposable connectToArCore(Context context, TextView textView) {
        disposable.dispose();
        final RetryWithDelay retryWithDelay = new RetryWithDelay(10, 200);
        return Observable
                .fromCallable(() -> ArCoreApk.getInstance().checkAvailability(context))
                .retryWhen(retryWithDelay)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(availability -> {
                    if (availability.isTransient()) {
                        if (retryWithDelay.getRetriesRemaining() > 0) {
                            textView.setText(R.string.ar_availability_checking);
                            throw new TransientConnectionException();
                        } else {
                            textView.setText(R.string.ar_availability_unknown);
                        }
                    } else if (availability.isSupported()) {
                        textView.setText(R.string.ar_availability_supported);
                    } else if (availability.isUnknown()) {
                        textView.setText(R.string.ar_availability_unsupported);
                    } else {
                        textView.setText(R.string.ar_availability_unknown);
                    }
                })
                .map(ArCoreApk.Availability::isSupported)
                .onErrorReturn(throwable -> false)
                .subscribe();
    }

    private final class TransientConnectionException extends RuntimeException {

    }

    @Override
    public void detach() {
        disposable.dispose();
    }
}
