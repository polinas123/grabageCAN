package com.sillyv.garbagecan.screen.camera;

import android.util.SparseArray;

import com.sillyv.garbagecan.core.BaseContract;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public interface CameraContract {
    interface View {

        void takePicture();

        @SuppressWarnings("unused")
        boolean isFlashOn();

        Observable<FileUploadEvent> getSavedFile();

        void displayThankYouDialog();
    }


    interface Presenter extends BaseContract.Presenter {


    }

    interface Repo {
        Single<Double> getLocation();

        Single<String> uploadPhoto(FileUploadEvent fileUploadEvent);

        Completable saveNewRecord(FileUploadEvent fileUploadEvent);

        Single<SparseArray<String>> getCredentials();
    }
}
