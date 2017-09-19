package com.sillyv.garbagecan.screen.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding2.view.RxView;
import com.sillyv.garbagecan.R;
import com.sillyv.garbagecan.data.Repository;
import com.sillyv.garbagecan.util.ButtonIDHappinessMapper;
import com.sillyv.garbagecan.util.FilesUtils;
import com.sillyv.garbagecan.util.camera.Camera2BasicFragment;
import com.sillyv.garbagecan.util.camera.CameraOldBasicFragment;

import java.io.File;
import java.util.concurrent.Semaphore;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Vasili on 09/11/2016.
 */
public abstract class CameraFragment
        extends Fragment
        implements CameraContract.View {

    static final String SAMSUNG = "samsung";
    @SuppressWarnings("unused")
    private static final String TAG = "SB.CameraPreviewFrag";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String PREFIX_ARG = "FILE_PREFIX";
    private static final String BACK_CAMERA_ARG = "BACK_CAMERA";
    private static final String MINIMUM_RATIO = "MINIMUM_PREVIEW_RATIO";
    private static final String MAXIMUM_RATIO = "MAXIMUM_PREVIEW_RATIO";
    /*
     * This will be prefixed to the filenames of the captured photos, located in the external storage
     * (not the public one), with the date and time, in the jpeg format
     */
    protected String filePrefix;

    /*
     * A boolean flag to determine whether the back-facing camera (true) will be used or the
     * front-facing camera
     */
    protected boolean useCameraBackFacing;

    protected double minimumPreviewRatio;

    protected double maximumPreviewRatio;

    /*
     * This is the output file for the captured picture.
     */
    protected File mFile;

    /*
     * This is a semaphore to make the use of the file object mFile thread-safe
     * acquire this semaphore before saving file using mFile and release it after using the saved file
     */
    protected Semaphore fileUsed = new Semaphore(1, true);

    private boolean flashOn;
    private int score = 0;
    private DisposableObserver<Integer> buttonsObservable;
    private CameraContract.Presenter presenter;
    private ProgressBar progressBar;
    private PublishSubject<FileUploadEvent> subject = PublishSubject.create();

    public static CameraFragment newInstance(
            double minPreviewRatio,
            double maxPreviewRatio) {
        CameraFragment newFragment;
        if (Build.VERSION.SDK_INT < 21) { //old Camera api
            newFragment = CameraOldBasicFragment.newInstance();
        } else { //new camera2 api
            newFragment = Camera2BasicFragment.newInstance();
        }
        Bundle args = new Bundle();
        args.putString(PREFIX_ARG, "garbageCANPhoto");
        args.putBoolean(BACK_CAMERA_ARG, true);
        args.putDouble(MINIMUM_RATIO, minPreviewRatio);
        args.putDouble(MAXIMUM_RATIO, maxPreviewRatio);
        assert newFragment != null;
        newFragment.setArguments(args);
        return newFragment;
    }

    private void toggleFlash() {
        flashOn = !flashOn;
        setFlashStatus();
    }

    protected void setFlashStatus() {
        if (Build.MANUFACTURER.equals(SAMSUNG)) {
            if (flashOn) {
                turnFlashOn();
            } else {
                turnFlashOff();
            }
        } else {
            setFlashAuto();
        }
    }

    protected void bindViewElements(View view) {
        buttonsObservable = getClicks(view, R.id.meh_button)
                .mergeWith(getClicks(view, R.id.happy_button))
                .mergeWith(getClicks(view, R.id.sad_button))
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        score = integer;
                        takePicture();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        progressBar = view.findViewById(R.id.progress_bar);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        buttonsObservable.dispose();
        presenter.detach();
    }

    @NonNull
    private Observable<Integer> getClicks(View view, int viewID) {
        return RxView.clicks(view.findViewById(viewID))
                     .map(o -> viewID)
                     .map(ButtonIDHappinessMapper::getHappinessFromButton);
    }

    protected abstract void setFlashAuto();

    protected abstract void turnFlashOn();

    protected abstract void turnFlashOff();

    @Override
    public boolean isFlashOn() {
        return flashOn;
    }

    @SuppressWarnings("unused")
    protected void bindFlashToggle(ImageView flashToggle) {
        flashOn = false;
        if (Build.MANUFACTURER.equals(SAMSUNG)) {
            flashToggle.setVisibility(View.VISIBLE);
            flashToggle.setOnClickListener(v -> {
                toggleFlash();
                if (flashOn) {
                    flashToggle.setImageResource(R.drawable.ic_flash_on_yellow);
                } else {
                    flashToggle.setImageResource(R.drawable.ic_flash_off_gray);
                }
            });
        } else {
            flashToggle.setVisibility(View.GONE);
        }
    }

    /**
     * Create a File for saving an image
     */
    protected File getOutputMediaFile() {
        return FilesUtils.getFile(getContext(), FilesUtils.PICTURES_STRING_BEAN_TEMP, filePrefix);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                // TODO: 9/15/2017 Handle What to do if permissions are denied
                //                ErrorDialog.newInstance(getString(R.string.request_permission_camera)) //TODO: v4
                // error dialog
                //                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
//                EventBus.getDefault()
//                        .post(
//                                new PermissionsDeniedEvent(
//                                        "Camera and Storage")); //// FIXME: 19/02/2017 if permission denied - endless loop although should not be
            } else { //permission granted
                initializeCameraPreview();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CameraPresenter(this, Repository.getInstance(getContext()));
        Bundle args = getArguments();
        filePrefix = (args != null) ? args.getString(PREFIX_ARG) : "";
        useCameraBackFacing = args == null || args.getBoolean(BACK_CAMERA_ARG);
        minimumPreviewRatio = (args != null) ? args.getDouble(MINIMUM_RATIO) : -1;
        maximumPreviewRatio = (args != null) ? args.getDouble(MAXIMUM_RATIO) : -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initializeCameraPreview();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    protected abstract void initializeCameraPreview();

    protected void notifyPhotoSaved() {
        subject.onNext(new FileUploadEvent(mFile, score));
    }

    @Override
    public Observable<FileUploadEvent> getSavedFile() {
        return subject;
    }

    @Override
    public void displayThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.thank_you_message)
               .setPositiveButton(R.string.restart, (dialog, id) -> {

               })
               .setNegativeButton(R.string.exit, (dialog, id) -> getActivity().finish());
        // Create the AlertDialog object and return it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void activateProgressBar(int happinessFromButton) {
        progressBar.setVisibility(View.VISIBLE);

        progressBar.getIndeterminateDrawable().setColorFilter(happinessFromButton, android.graphics.PorterDuff.Mode.MULTIPLY);

//        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress));
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
