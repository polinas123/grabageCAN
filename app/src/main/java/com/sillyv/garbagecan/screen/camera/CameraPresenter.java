package com.sillyv.garbagecan.screen.camera;

import android.util.Log;

import com.sillyv.garbagecan.core.BasePresenter;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

class CameraPresenter
        extends BasePresenter
        implements CameraContract.Presenter {
    private static final String TAG = "CameraPresenter";
    private CameraContract.View view;
    private CameraContract.Repo repo;

    CameraPresenter(CameraContract.View view,
                    CameraContract.Repo repo) {
        this.view = view;
        this.repo = repo;
        subscribeToEvents();
    }

    private void subscribeToEvents() {
        view.getSavedFile()
                .doOnSubscribe(this::registerDisposable)
                .flatMapSingle(cameraEventModel -> repo.getLocation()
                        .map(aDouble -> {
                            cameraEventModel.setLatitude(aDouble);
                            cameraEventModel.setLongitude(aDouble);
                            return cameraEventModel;
                        }))
                .flatMapSingle(cameraEventModel -> repo.uploadPhoto(cameraEventModel).map(s -> {
                    cameraEventModel.setUploadedFilePath(s);
                    return cameraEventModel;
                }))
                .flatMapSingle(cameraEventModel -> repo.saveNewRecord(cameraEventModel)
                        .toSingle(() -> cameraEventModel))
                .subscribeWith(new DisposableObserver<CameraEventModel>() {
                    @Override
                    public void onNext(CameraEventModel file) {
                        Log.d(TAG,
                                "onNext: FilePath: " + file.getFile().getPath() +
                                        ", Score " + file.getScore() +
                                        ", Lat: " + file.getLatitude() +
                                        ", Lon: " + file.getLongitude()
                        );
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
