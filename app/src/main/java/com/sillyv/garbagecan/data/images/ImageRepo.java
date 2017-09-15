package com.sillyv.garbagecan.data.images;

import com.sillyv.garbagecan.data.RepositoryContract;
import com.sillyv.garbagecan.screen.camera.CameraEventModel;

import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class ImageRepo implements RepositoryContract.Image {
    public Single<String> uploadPhoto(CameraEventModel cameraEventModel) {
        return Single.fromCallable(() -> cameraEventModel.getFile().getPath() + "HTTP");
    }

}
