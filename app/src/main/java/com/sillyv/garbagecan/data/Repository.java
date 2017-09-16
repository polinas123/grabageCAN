package com.sillyv.garbagecan.data;

import android.util.SparseArray;

import com.sillyv.garbagecan.core.BaseContract;
import com.sillyv.garbagecan.data.credentials.CredentialsRepo;
import com.sillyv.garbagecan.data.database.DataBaseRepo;
import com.sillyv.garbagecan.data.images.ImageRepo;
import com.sillyv.garbagecan.data.location.LocationRepo;
import com.sillyv.garbagecan.screen.camera.FileUploadEvent;

import java.util.Map;

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
    private RepositoryContract.Credentials credentialsRepo = new CredentialsRepo();

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
    public Single<String> uploadPhoto(FileUploadEvent fileUploadEvent) {
        return imageUploader.uploadPhotoRx(fileUploadEvent);
    }

    @Override
    public Completable saveNewRecord(FileUploadEvent fileUploadEvent) {
        return database.saveRecord(fileUploadEvent);
    }

    @Override
    public Single<SparseArray<String>> getCredentials() {return credentialsRepo.getCredentialsRx();}
}
