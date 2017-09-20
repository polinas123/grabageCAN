package com.sillyv.garbagecan.screen.main;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sillyv.garbagecan.R;
import com.sillyv.garbagecan.screen.camera.CameraFragment;
import com.sillyv.garbagecan.util.RxDialog;

public class MainActivity
        extends AppCompatActivity
        implements MainContract.View {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainContract.Presenter presenter = new MainPresenter(this);
        presenter.init(this);


    }

    public void displayCamera() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = Math.max(size.x, size.y);
        int height = Math.min(size.x, size.y);
        double maxRatio = (double) width / height;
        double minRatio = (double) (width) / height;
        CameraFragment fragment = CameraFragment.newInstance(
                minRatio * .1, maxRatio);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment).commit();
    }

    @Override
    public void displayApology() {
        RxDialog.newBuilder(this)
                .withTitle("Permissions denied")
                .withMessage(
                        "All permissions are required to use this app, we are sorry for the inconvenience.")
                .withPositiveMessage("Goodbye")
                .build()
                .show()
                .doOnEvent((aBoolean, throwable) -> finish())
                .subscribe();


    }


}
