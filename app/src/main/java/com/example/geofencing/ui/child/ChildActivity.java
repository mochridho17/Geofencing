package com.example.geofencing.ui.child;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geofencing.Config;
import com.example.geofencing.Contstants;
import com.example.geofencing.R;
import com.example.geofencing.databinding.ActivityChildBinding;
import com.example.geofencing.dialog.ChildCodeDialog;
import com.example.geofencing.dialog.ChildInfo;
import com.example.geofencing.services.LocationService;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChildActivity extends AppCompatActivity {


    private static final String TAG = "ChildActivity";
    ActivityChildBinding binding;
    private GoogleMap mMap;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private DatabaseReference DB;
    SharedPreferencesUtil sf = new SharedPreferencesUtil(ChildActivity.this);
    List<LatLng> latLngList;
    FirebaseAuth Auth;
    private String childCode = "";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            enableUserLocation();
            getPolygons(Auth.getUid());

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Initialization Firebase Auth and Firebase Database
        Auth = FirebaseAuth.getInstance();
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();

        getChildInfo();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        binding.fabInfo.setOnClickListener(v -> {
            createInfoDialog();
        });

    }

    private void getChildInfo() {
        DB.child("childs").child(Auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChildInfo childData = snapshot.getValue(ChildInfo.class);
                childCode = childData.getPairKey();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(childData.getUsername());
                }
                binding.fabInfo.setOnClickListener(v -> {
                    createInfoDialog();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createInfoDialog() {
        ChildCodeDialog dialog = new ChildCodeDialog(childCode);
        dialog.show(getSupportFragmentManager(), "ChildCodeDialog");
    }

    private void getPolygons(String childId) {
        DatabaseReference polygonRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("childs").child(childId).child("polygons");

        polygonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> polygonList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    i++;
                    String value = snapshot.getValue(String.class);
                    polygonList.add(value);
                }

                getPolygonData(polygonList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void getPolygonData(List<String> polygonList) {

        for (int i = 0; i < polygonList.size(); i++) {
            String polygonName = polygonList.get(i);
            getLatLng(polygonName);

        }

    }

    private void getLatLng(String polygonName) {

        DatabaseReference latLngRef = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("polygons").child(polygonName);

        latLngRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                latLngList = new ArrayList<>();

                for (DataSnapshot latLngSnapshot : dataSnapshot.getChildren()) {
                    Double latitude = latLngSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = latLngSnapshot.child("longitude").getValue(Double.class);

                    LatLng latLng = new LatLng(latitude, longitude);
                    latLngList.add(latLng);

                }
                drawPolygon(latLngList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawPolygon(List<LatLng> points) {
        PolygonOptions polygon = new PolygonOptions();
        for (LatLng point : points) {
            polygon.add(point);
        }
        polygon.fillColor(R.color.red_transparent);
        polygon.strokeColor(Color.RED);
        mMap.addPolygon(polygon);
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(ChildActivity.this, LocationService.class);
            intent.setAction(Contstants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(ChildActivity.this, "Location service started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChildActivity.this, "Location service is already running", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(ChildActivity.this, LocationService.class);
            intent.setAction(Contstants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(ChildActivity.this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(ChildActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getLastLocation();
            startLocationService();
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChildActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(ChildActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(ChildActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                    Toast.makeText(this, "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}