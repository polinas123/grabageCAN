package com.sillyv.garbagecan.util;

import android.util.SparseIntArray;

import static com.sillyv.garbagecan.util.ButtonIDHappinessMapper.HAPPY;
import static com.sillyv.garbagecan.util.ButtonIDHappinessMapper.MEH;
import static com.sillyv.garbagecan.util.ButtonIDHappinessMapper.SAD;

/**
 * Created by Vasili on 9/19/2017.
 */

public class HappinessColorMapper {


    private static final SparseIntArray myMap;

    static {
        SparseIntArray array = new SparseIntArray();
        array.put(HAPPY, 0xFF00FF00);
        array.put(MEH, 0xFFFFFF00);
        array.put(SAD, 0xFFFF0000);
        myMap = array.clone();
    }


    public static int getHappinessFromButton(int buttonId) {
        return myMap.get(buttonId);
    }


}
