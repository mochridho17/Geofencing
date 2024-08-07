package com.example.geofencing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.ActivitySpashScreenBinding;
import com.example.geofencing.services.NetworkChangeReceiver;
import com.example.geofencing.ui.parent.MainActivity;

public class SplashScreenActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {

    ActivitySpashScreenBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        networkChangeReceiver = new NetworkChangeReceiver(this);
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (isConnected) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        } else {
            Toast.makeText(SplashScreenActivity.this, "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
        }
    }
}
