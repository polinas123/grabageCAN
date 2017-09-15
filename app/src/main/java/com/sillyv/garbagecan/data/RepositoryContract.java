package com.sillyv.garbagecan.data;

import com.sillyv.garbagecan.screen.camera.CameraEventModel;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public interface RepositoryContract {
    interface Database {
        Completable saveRecord(CameraEventModel cameraEventModel);
    }

    interface Image {
        Single<String> uploadPhotoRx(CameraEventModel cameraEventModel);
    }

    interface Location {
        Single<Double> getLocation();
    }
}
