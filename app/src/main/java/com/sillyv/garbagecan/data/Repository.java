package com.sillyv.garbagecan.data;

import com.sillyv.garbagecan.core.BaseContract;
import com.sillyv.garbagecan.screen.camera.CameraEventModel;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class Repository
        implements BaseContract.Repo {


    private static Repository instance;
    private RepositoryContract.Location locationManager;
    private RepositoryContract.Image imageUploader;
    private RepositoryContract.Database database;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    private Repository() {
    }

    @Override
    public Single<Double> getLocation() {
        return locationManager.getLocation();
    }

    @Override
    public Single<String> uploadPhoto(CameraEventModel cameraEventModel) {
        return imageUploader.uploadPhoto(cameraEventModel);
    }

    @Override
    public Completable saveNewRecord(CameraEventModel cameraEventModel) {
        return database.saveRecord(cameraEventModel);
    }
}
