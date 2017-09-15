package com.sillyv.garbagecan.screen.camera;

import com.sillyv.garbagecan.core.BaseContract;

import io.reactivex.Observable;

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
    }


    interface Presenter extends BaseContract.Presenter {


    }
}
