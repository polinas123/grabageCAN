package com.sillyv.garbagecan.data.database;

import android.util.Log;

import com.sillyv.garbagecan.data.RepositoryContract;
import com.sillyv.garbagecan.screen.camera.CameraEventModel;

import io.reactivex.Completable;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class DataBaseRepo implements RepositoryContract.Database{
    private static final String TAG = "DataBaseRepo";

    public Completable saveRecord(CameraEventModel cameraEventModel) {
        Log.d(TAG, "saveRecord: " + cameraEventModel.getUploadedFilePath());
        Log.d(TAG, "saveRecord: " + cameraEventModel.getLatitude());
        Log.d(TAG, "saveRecord: " + cameraEventModel.getLongitude());
        return Completable.complete();
    }
}
