package com.sillyv.garbagecan.screen.camera;

import java.io.File;

/**
 * a class used to raise and listen/catch saving files events using EventBus
 * <p>
 * <p>Created by Vasili on 01/12/2016.
 */
public class CameraEventModel {


    private final File file;
    private int score;


    CameraEventModel(File file, int score) {
        this.file = file;
        this.score = score;
    }


    int getScore() {
        return score;
    }

    public File getFile() {
        return file;
    }

}
