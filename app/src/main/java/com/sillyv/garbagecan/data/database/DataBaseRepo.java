package com.sillyv.garbagecan.data.database;

import android.util.Log;

import com.sillyv.garbagecan.data.RepositoryContract;
import com.sillyv.garbagecan.screen.camera.FileUploadEvent;

import io.reactivex.Completable;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class DataBaseRepo implements RepositoryContract.Database{
    private static final String TAG = "DataBaseRepo";

    public Completable saveRecord(FileUploadEvent fileUploadEvent) {
        Log.d(TAG, "saveRecord: " + fileUploadEvent.getUploadedFilePath());
        Log.d(TAG, "saveRecord: " + fileUploadEvent.getLatitude());
        Log.d(TAG, "saveRecord: " + fileUploadEvent.getLongitude());
        return Completable.complete();
    }
}
