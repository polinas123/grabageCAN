package com.sillyv.garbagecan.data.location;

import com.sillyv.garbagecan.data.RepositoryContract;

import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class LocationRepo implements  RepositoryContract.Location{
    public Single<Double> getLocation() {
        return Single.fromCallable(() -> 4d);
    }
}
