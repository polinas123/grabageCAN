package com.sillyv.garbagecan.screen.main;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sillyv.garbagecan.R;
import com.sillyv.garbagecan.screen.camera.CameraFragment;

public class MainActivity
        extends AppCompatActivity {
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayCamera();
    }

    private void displayCamera() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = Math.max(size.x, size.y);
        int height = Math.min(size.x, size.y);
        double maxRatio = (double) width / height;
        double minRatio = (double) (width) / height;
        CameraFragment fragment = CameraFragment.newInstance(
                minRatio * .1 , maxRatio );
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment).commit();
    }


}
