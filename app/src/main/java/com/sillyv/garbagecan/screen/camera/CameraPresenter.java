package com.sillyv.garbagecan.screen.camera;

import android.util.Log;

import com.sillyv.garbagecan.core.BasePresenter;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by Vasili on 9/15/2017.
 */

class CameraPresenter
        extends BasePresenter
        implements CameraContract.Presenter {
    private static final String TAG = "CameraPresenter";
    private CameraContract.View view;

    CameraPresenter(CameraContract.View view) {
        this.view = view;
        subscribeToEvents();
    }

    private void subscribeToEvents() {
        view.getSavedFile()
                .doOnSubscribe(this::registerDisposable)
                .subscribeWith(new DisposableObserver<CameraEventModel>() {

                    @Override
                    public void onNext(CameraEventModel file) {
                        Log.d(TAG,
                                "onNext: " + file.getFile()
                                        .getPath() + ", Score " + file.getScore());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
