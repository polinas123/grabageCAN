package com.sillyv.garbagecan.screen.camera;

import android.util.Log;

import com.sillyv.garbagecan.core.BasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

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
                .doOnNext(cameraEventModel -> Log.d("TAG", "TEST"))
                .doOnSubscribe(this::registerDisposable)
                .observeOn(Schedulers.io())
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CameraEventModel>() {
                    @Override
                    public void onNext(CameraEventModel file) {
                        view.displayThankYouDialog();
                        Log.d(TAG,
                                "onNext: FilePath: " + file.getFile().getPath() +
                                        ", Score " + file.getScore() +
                                        ", Lat: " + file.getLatitude() +
                                        ", Lon: " + file.getLongitude()
                        );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ",e );
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
