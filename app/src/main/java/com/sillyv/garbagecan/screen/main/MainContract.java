package com.sillyv.garbagecan.screen.main;

import android.app.Activity;

/**
 * Created by Vasili on 9/20/2017.
 */

public interface MainContract {


    interface View  {
        void displayCamera();

        void displayApology();
    }


    interface Presenter {
        void init(Activity mainActivity);
    }
}
