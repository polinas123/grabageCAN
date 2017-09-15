package com.sillyv.garbagecan.data;

import com.sillyv.garbagecan.core.BaseContract;
import com.sillyv.garbagecan.data.database.DataBaseRepo;
import com.sillyv.garbagecan.data.images.ImageRepo;
import com.sillyv.garbagecan.data.location.LocationRepo;
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
    private RepositoryContract.Location locationManager = new LocationRepo();
    private RepositoryContract.Image imageUploader = new ImageRepo();
    private RepositoryContract.Database database= new DataBaseRepo();

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
        return imageUploader.uploadPhotoRx(cameraEventModel);
    }

    @Override
    public Completable saveNewRecord(CameraEventModel cameraEventModel) {
        return database.saveRecord(cameraEventModel);
    }
}
