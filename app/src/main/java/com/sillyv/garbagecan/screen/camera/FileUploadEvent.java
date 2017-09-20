package com.sillyv.garbagecan.screen.camera;

import android.util.SparseArray;

import java.io.File;

/**
 * Created by Vasili on 01/12/2016.
 *
 */
public class FileUploadEvent {

    private final File file;
    private final int score;
    private double longitude;
    private double latitude;
    private String uploadedFilePath;
    private SparseArray<String> credentialsMap;


    FileUploadEvent(File file, int score) {
        this.file = file;
        this.score = score;
    }

    int getScore() {
        return score;
    }

    public File getFile() {
        return file;
    }

    public double getLongitude() {
        return longitude;
    }

    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUploadedFilePath() {
        return uploadedFilePath;
    }

    void setUploadedFilePath(String uploadedFilePath) {
        this.uploadedFilePath = uploadedFilePath;
    }

    void setCredentialsMap(SparseArray<String> credentialsMap) {
        this.credentialsMap = credentialsMap;
    }

    public SparseArray<String> getCredentialsMap() {
        return credentialsMap;
    }
}
