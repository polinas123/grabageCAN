package com.sillyv.garbagecan.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vasili on 9/2/2017.
 *
 */

public class FilesUtils {
    private static final String TAG = "FilesUtils";
    public static final String PICTURES_STRING_BEAN_TEMP = "temp";

    public static File getFile(Context context, String folderName, String
            filenamePrefix) {
        File mediaStorageDir = new File(context.getFilesDir(), folderName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir, filenamePrefix + timeStamp + ".jpg");
    }
}
