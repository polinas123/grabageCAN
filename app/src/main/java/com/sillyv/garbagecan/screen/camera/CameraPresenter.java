package com.sillyv.garbagecan.screen.camera;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.sillyv.garbagecan.core.BasePresenter;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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
                .flatMapSingle(fileUploadEvent -> repo.getLocation()
                        .map(injectLocationIntoUploadModel(fileUploadEvent)))
                .flatMapSingle(fileUploadEvent -> repo.getCredentials()
                        .map(injectCredentialsIntoUploadModel(fileUploadEvent)))
                .flatMapSingle(fileUploadEvent -> repo.uploadPhoto(fileUploadEvent)
                        .map(injectRemotePathIntoUploadModel(fileUploadEvent)))
                .flatMapSingle(fileUploadEvent -> repo.saveNewRecord(fileUploadEvent)
                        .toSingle(() -> fileUploadEvent))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FileUploadEvent>() {
                    @Override
                    public void onNext(FileUploadEvent file) {
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
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Function<SparseArray<String>, FileUploadEvent> injectCredentialsIntoUploadModel(
            FileUploadEvent fileUploadEvent) {
        return integerStringMap -> {
            fileUploadEvent.setCredentialsMap(integerStringMap);
            return fileUploadEvent;
        };
    }

    @NonNull
    private Function<String, FileUploadEvent> injectRemotePathIntoUploadModel(FileUploadEvent cameraEventModel) {
        return remotePath -> {
            cameraEventModel.setUploadedFilePath(remotePath);
            return cameraEventModel;
        };
    }

    @NonNull
    private Function<Double, FileUploadEvent> injectLocationIntoUploadModel(FileUploadEvent cameraEventModel) {
        return aDouble -> {
            cameraEventModel.setLatitude(aDouble);
            cameraEventModel.setLongitude(aDouble);
            return cameraEventModel;
        };
    }
}
