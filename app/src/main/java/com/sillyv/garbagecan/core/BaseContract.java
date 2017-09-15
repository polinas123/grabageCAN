package com.sillyv.garbagecan.core;

import com.sillyv.garbagecan.screen.camera.CameraContract;

import io.reactivex.disposables.Disposable;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public interface BaseContract {

    interface Presenter {

        void registerDisposable(Disposable disposable);

        void detach();
    }

    interface Repo extends CameraContract.Repo {
    }
}
