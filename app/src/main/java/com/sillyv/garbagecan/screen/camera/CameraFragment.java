package com.sillyv.garbagecan.screen.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.sillyv.garbagecan.PermissionsDeniedEvent;
import com.sillyv.garbagecan.R;
import com.sillyv.garbagecan.data.Repository;
import com.sillyv.garbagecan.screen.camera.camera.Camera2BasicFragment;
import com.sillyv.garbagecan.screen.camera.camera.CameraOldBasicFragment;
import com.sillyv.garbagecan.util.ButtonIDHappinessMapper;
import com.sillyv.garbagecan.util.FilesUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.Semaphore;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Vasili on 09/11/2016.
 *
 */
public abstract class CameraFragment
        extends Fragment
        implements CameraContract.View {

    @SuppressWarnings("unused")
    private static final String TAG = "SB.CameraPreviewFrag";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static final String PREFIX_ARG = "FILE_PREFIX";

    private static final String BACK_CAMERA_ARG = "BACK_CAMERA";

    private static final String MINIMUM_RATIO = "MINIMUM_PREVIEW_RATIO";

    private static final String MAXIMUM_RATIO = "MAXIMUM_PREVIEW_RATIO";

    static final String SAMSUNG = "samsung";

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


    protected void bindScoreButtons(View view) {
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
                //                ErrorDialog.newInstance(getString(R.string.request_permission_camera)) //TODO: v4
                // error dialog
                //                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
                EventBus.getDefault()
                        .post(
                                new PermissionsDeniedEvent(
                                        "Camera and Storage")); //// FIXME: 19/02/2017 if permission denied - endless loop although should not be
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
        presenter = new CameraPresenter(this, Repository.getInstance());
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
        subject.onNext(new CameraEventModel(mFile, score));
    }

    private PublishSubject<CameraEventModel> subject = PublishSubject.create();

    @Override
    public Observable<CameraEventModel> getSavedFile() {
        return subject;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @OnClick({R.id.meh_button, R.id.happy_button, R.id.sad_button})
    public void onViewClicked(View view) {

    }
}
