package com.sillyv.garbagecan;

/**
 * Created by Vasili on 9/2/2017.
 *
 */

public class PermissionsDeniedEvent {
    private String s;

    public PermissionsDeniedEvent(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
