package com.sillyv.garbagecan.util.camera;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.sillyv.garbagecan.R;
import com.sillyv.garbagecan.screen.camera.CameraFragment;
import com.sillyv.garbagecan.util.AutoFitTextureView;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Vasili on 09/11/2016.
 *
 */
@SuppressWarnings("SuspiciousNameCombination")
public class CameraOldBasicFragment extends CameraFragment
        implements FragmentCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "SB.CameraOldB..Fragment";

    int mCameraId = -1;

    private Camera mCamera;

    private AutoFitTextureView mTextureView;

    private Camera.Size mPreviewSize;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                    try {
                        setUpCameraOutputs(width, height);
                        configureTransform(width, height);
                        mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
                        startPreview();
                    } catch (IOException e) {
                        stopActivityForException(e);
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                    configureTransform(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                }
            };

    private Camera.PictureCallback mPicture =
            (data, camera) -> {

                try {
                    fileUsed.acquire();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrupted");
                    e.printStackTrace();
                    return;
                }
                mFile = getOutputMediaFile();
                if (mFile == null) {
                    Log.d(TAG, "Error creating media file");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(mFile);
                    fos.write(data);
                    fos.close();
                    notifyPhotoSaved();
                    Log.d(TAG, "saved:" + mFile.toString());
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                } finally {
                    fileUsed.release();
                }

                startPreview();
            };

    public static CameraOldBasicFragment newInstance() {
        return new CameraOldBasicFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create an instance of the Camera fragment layout
        View view = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        bindViewElements(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get our Preview view
        mTextureView = view.findViewById(R.id.texture);
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }

    /*
     * Release the camera device
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public void takePicture() {
        setFlashStatus();
        mCamera.autoFocus((success, camera) -> mCamera.takePicture(null, null, mPicture));
    }

    @Override
    protected void setFlashAuto() {
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(params);
        }
    }

    @Override
    protected void turnFlashOn() {
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
        }

    }

    @Override
    protected void turnFlashOff() {
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
        }

    }

    @Override
    protected void initializeCameraPreview() {
        try {
            if (mCamera == null) {
                mCamera = getCameraInstance();
                setCameraId();
            }
            if (mTextureView.isAvailable()) {
                int width = mTextureView.getWidth();
                int height = mTextureView.getHeight();
                setUpCameraOutputs(width, height);
                configureTransform(width, height);
                mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
                startPreview();
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        } catch (Exception e) {
            stopActivityForException(e);
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() throws Exception {
        Camera c;
        c = Camera.open(); // attempt to get a Camera instance, throw Exception if can't

        return c;
    }

    /*
     * sets the mCameraId to the right camera id (front or back -facing, depending on the wanted kind)
     */
    private void setCameraId() {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (((info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) && !useCameraBackFacing)
                    || ((info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) && useCameraBackFacing)) {
                Log.d(TAG, "Camera found");
                mCameraId = i;
                break;
            }
        }
    }

    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();

        Camera.Parameters params = mCamera.getParameters();
        // For still image captures, we use the largest available size.
        Camera.Size largest =
                Collections.max(
                        (params.getSupportedPictureSizes()),
                        new CompareSizesByArea(minimumPreviewRatio, maximumPreviewRatio));
        params.setPictureSize(largest.width, largest.height);

        // Find out if we need to swap dimension to get the preview size relative to sensor
        // coordinate.
        int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        //Inquire the sensor's orientation relative to the natural phone's orientation
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int mSensorOrientation = info.orientation;
        boolean swappedDimensions = false;
        switch (displayRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                    swappedDimensions = true;
                }
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                    swappedDimensions = true;
                }
                break;
            default:
                Log.e(TAG, "Display rotation is invalid: " + displayRotation);
        }

        int rotatedPreviewWidth = width;
        int rotatedPreviewHeight = height;

        if (swappedDimensions) {
            rotatedPreviewWidth = height;
            rotatedPreviewHeight = width;
        }

        // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
        // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
        // garbage capture data.
        mPreviewSize =
                chooseOptimalSize(
                        params.getSupportedPreviewSizes(), rotatedPreviewWidth, rotatedPreviewHeight, largest);
        params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

        // We fit the aspect ratio of TextureView to the size of preview we picked.
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTextureView.setAspectRatio(mPreviewSize.width, mPreviewSize.height);
        } else {
            mTextureView.setAspectRatio(mPreviewSize.height, mPreviewSize.width);
        }

        mCamera.setDisplayOrientation(mSensorOrientation);
        params.set("rotation", mSensorOrientation);
        //TODO: set the taken picture's orientation as well - now it's being saved on its side

        mCamera.setParameters(params);
    }

    /**
     * TODO: move/delete Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in setUpCameraOutputs
     * and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.height, mPreviewSize.width);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale =
                    Math.max(
                            (float) viewHeight / mPreviewSize.height, (float) viewWidth / mPreviewSize.width);
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void startPreview() {
        mCamera.startPreview();
        mCamera.cancelAutoFocus();
        Camera.Parameters params = mCamera.getParameters();
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
        }
        setFlashStatus();
    }

    /*
     * Close the current activity and log the given exception
     */
    private void stopActivityForException(Exception e) {
        Toast.makeText(getActivity(), getString(R.string.camera_general_error), Toast.LENGTH_LONG)
             .show();
        e.printStackTrace();
        getActivity().finish();
    }

    private static Camera.Size chooseOptimalSize(
            List<Camera.Size> choices,
            int textureViewWidth,
            int textureViewHeight,
            Camera.Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Camera.Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Camera.Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.width;
        int h = aspectRatio.height;
        for (Camera.Size option : choices) {
            if (option.height == option.width * h / w) {
                if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices.get(0);
        }
    }

    /**
     * Compares two {@code Size}s based on their areas, prefers areas that have ratios within the
     * range og the given minRatio,maxRatio (given at construction). To ignore the ratios - pass -1
     * for both pr use the default constructor.
     */
    static class CompareSizesByArea implements Comparator<Camera.Size> {

        private double minimumRatio;

        private double maximumRatio;

        //constructs a comparator that compares based on the areas alone
        CompareSizesByArea() {
            minimumRatio = -1;
            maximumRatio = -1;
        }

        //constructs a comparator that prefers areas that are within the range of the given minRatio,maxRatio
        // and compares based on area between those that are out of that range or within that range
        CompareSizesByArea(double minRatio, double maxRatio) {
            minimumRatio = minRatio;
            maximumRatio = maxRatio;
        }

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (minimumRatio > 0 && maximumRatio > 0) {
                double leftRatio = (double) lhs.width / lhs.height;
                double rightRatio = (double) rhs.width / rhs.height;
                if ((leftRatio < minimumRatio || leftRatio > maximumRatio)
                        && rightRatio > minimumRatio
                        && rightRatio < maximumRatio) {
                    return -1;
                } else if ((rightRatio < minimumRatio || rightRatio > maximumRatio)
                        && leftRatio > minimumRatio
                        && leftRatio < maximumRatio) {
                    return 1;
                }
            }
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
        }
    }
}
