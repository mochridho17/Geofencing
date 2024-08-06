package com.example.geofencing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geofencing.auth.ChildLoginActivity;
import com.example.geofencing.auth.LoginActivity;
import com.example.geofencing.databinding.ActivityWelcomeBinding;
import com.example.geofencing.dialog.AgreementDialog;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.ui.parent.MainActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WelcomeActivity extends AppCompatActivity {

    private static final Log log = LogFactory.getLog(WelcomeActivity.class);
    private static final String TAG = "WelcomeActivity";
    ActivityWelcomeBinding binding;
    SharedPreferencesUtil sf;
    private FirebaseAuth Auth;
    private DatabaseReference DB;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();
        sf = new SharedPreferencesUtil(WelcomeActivity.this);
        android.util.Log.d(TAG, "account_type: "+sf.getPref("account_type", WelcomeActivity.this));
//        sf.setPref("agreement_accepted", null, WelcomeActivity.this);

        if (!checkAgreementCache()) {
            AgreementDialog.showAgreementDialog(WelcomeActivity.this, new AgreementDialog.AgreementDialogListener() {
                @Override
                public void onAgreementAccepted() {
                    sf.setPref("agreement_accepted", "true", WelcomeActivity.this);
                    requestLocationPermission();
                }

                @Override
                public void onAgreementRejected() {
                    finish();
                }
            });
        }

        // Check if user is logged in
        if (Auth.getCurrentUser() != null) {

            if (sf.getPref("account_type", WelcomeActivity.this) != null){
                if (sf.getPref("account_type", WelcomeActivity.this).equals("parent")){
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (sf.getPref("account_type", WelcomeActivity.this).equals("child")){
                    Intent intent = new Intent(WelcomeActivity.this, ChildActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

//            Toast.makeText(WelcomeActivity.this, "Already logged in",
//                    Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
        }

        binding.btnLoginAsParent.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.btnLoginAsChild.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, ChildLoginActivity.class);
            startActivity(intent);
        });

    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean checkAgreementCache() {
        return sf.getPref("agreement_accepted", WelcomeActivity.this) != null;
    }
}