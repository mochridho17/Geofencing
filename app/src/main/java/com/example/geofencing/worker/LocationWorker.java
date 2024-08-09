package com.example.geofencing.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.geofencing.services.LocationService;
import com.google.firebase.auth.FirebaseAuth;

public class LocationWorker extends Worker {
    FirebaseAuth mAuth;
    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        LocationService locationService = new LocationService();
//        locationService.getPolygons();
//        locationService.getFcmToken();
        return Result.success();
    }
}