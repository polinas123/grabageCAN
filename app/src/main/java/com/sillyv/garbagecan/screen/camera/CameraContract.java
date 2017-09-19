package com.sillyv.garbagecan.screen.camera;

import android.util.SparseArray;

import com.sillyv.garbagecan.core.BaseContract;
import com.sillyv.garbagecan.data.location.LatLonModel;

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

        void activateProgressBar(int happinessFromButton);
    }


    interface Presenter extends BaseContract.Presenter {


    }

    interface Repo {
        Single<LatLonModel> getLocation();

        Single<String> uploadPhoto(FileUploadEvent fileUploadEvent);

        Completable saveNewRecord(FileUploadEvent fileUploadEvent);

        Single<SparseArray<String>> getCredentials();
    }
}
