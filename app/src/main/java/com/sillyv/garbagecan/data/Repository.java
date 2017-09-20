package com.sillyv.garbagecan.data;

import android.content.Context;
import android.util.SparseArray;

import com.sillyv.garbagecan.core.BaseContract;
import com.sillyv.garbagecan.data.credentials.CredentialsManager;
import com.sillyv.garbagecan.data.database.DataBaseRepo;
import com.sillyv.garbagecan.data.images.ImageRepo;
import com.sillyv.garbagecan.data.location.LatLonModel;
import com.sillyv.garbagecan.data.location.LocationRepo;
import com.sillyv.garbagecan.screen.camera.FileUploadEvent;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class Repository
        implements BaseContract.Repo {

    private static Repository instance;
    private Context context;
    private RepositoryContract.Location locationManager = LocationRepo.getInstance(context);
    private RepositoryContract.Image imageUploader = new ImageRepo();
    private RepositoryContract.Database database = new DataBaseRepo();
    private RepositoryContract.Credentials credentialsRepo = new CredentialsManager();

    private Repository(Context context) {
        this.context = context;
    }

    public static Repository getInstance(Context context) {
        if (instance == null) {
            instance = new Repository(context);
        }
        return instance;
    }

    @Override
    public Single<LatLonModel> getLocation() {
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
    public Single<SparseArray<String>> getCredentials() {
        return credentialsRepo.getCredentialsRx();
    }
}
