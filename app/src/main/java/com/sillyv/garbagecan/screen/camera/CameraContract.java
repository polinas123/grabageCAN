package com.sillyv.garbagecan.screen.camera;

import com.sillyv.garbagecan.core.BaseContract;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public interface CameraContract {
    interface View {

        void takePicture();

        @SuppressWarnings("unused")
        boolean isFlashOn();

        Observable<CameraEventModel> getSavedFile();

        void displayThankYouDialog();
    }


    interface Presenter extends BaseContract.Presenter {


    }

    interface Repo {
        Single<Double> getLocation();

        Single<String> uploadPhoto(CameraEventModel cameraEventModel);

        Completable saveNewRecord(CameraEventModel cameraEventModel);
    }
}
