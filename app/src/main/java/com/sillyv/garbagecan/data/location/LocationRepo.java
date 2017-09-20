package com.sillyv.garbagecan.data.location;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;
import com.sillyv.garbagecan.data.RepositoryContract;

import io.reactivex.Single;

/**
 * Created by Vasili on 9/15/2017.
 *
 */

public class LocationRepo implements RepositoryContract.Location {


    private static LocationRepo instance;
    private double latitude;
    private double longitude;

    @SuppressLint("MissingPermission")
    private LocationRepo(Context context) {
        RxLocation rxLocation = new RxLocation(context);

        LocationRequest locationRequest = LocationRequest.create()
                                                         .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                                         .setInterval(5000);

        rxLocation.location().updates(locationRequest)
                  .flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
                  .subscribe(address -> {
                      latitude = address.getLatitude();
                      longitude = address.getLatitude();
                  });


    }

    public static LocationRepo getInstance(Context context) {
        if (instance == null) {
            instance = new LocationRepo(context);
        }
        return instance;
    }


    public Single<LatLonModel> getLocation() {
        return Single.fromCallable(() -> new LatLonModel(latitude, longitude));
    }


}
