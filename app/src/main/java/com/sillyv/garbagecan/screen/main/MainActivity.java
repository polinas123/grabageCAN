package com.sillyv.garbagecan.screen.main;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.sillyv.garbagecan.R;
import com.sillyv.garbagecan.SavedSourceFileMessage;
import com.sillyv.garbagecan.screen.camera.CameraPreviewFragment;
import com.sillyv.garbagecan.screen.form.FormFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity
        extends AppCompatActivity {
    ;
    private CameraPreviewFragment fragment;
    private FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayCamera();
        bindButton();
    }

    private void displayCamera() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = Math.max(size.x, size.y);
        int height = Math.min(size.x, size.y);
        double maxRatio = (double) width / height;
        EventBus.getDefault().register(this);
        double minRatio = (double) (width) / height;
        fragment = CameraPreviewFragment.newInstance(
                minRatio, maxRatio);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment).commit();
    }

    private void bindButton() {
        button = findViewById(R.id.test_button);
        button.setOnClickListener(view -> {
            fragment.takePicture();
            button.hide();

        });
    }


    @Subscribe
    public void test(SavedSourceFileMessage test) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, FormFragment.newInstance(test.getFilePath())).commit();
    }

}
