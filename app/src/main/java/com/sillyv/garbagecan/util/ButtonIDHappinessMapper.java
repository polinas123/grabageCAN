package com.sillyv.garbagecan.util;

import android.util.SparseIntArray;

import com.sillyv.garbagecan.R;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

@SuppressWarnings("WeakerAccess")
public class ButtonIDHappinessMapper {
    private static final SparseIntArray myMap;

    public static final int HAPPY = 0;
    public static final int MEH = 1;
    public static final int SAD = 2;

    static {
        SparseIntArray array = new SparseIntArray();
        array.put(R.id.happy_button, HAPPY);
        array.put(R.id.meh_button, MEH);
        array.put(R.id.sad_button, SAD);
        myMap = array.clone();
    }


    public static int getHappinessFromButton(int buttonId) {
        return myMap.get(buttonId);
    }


}
