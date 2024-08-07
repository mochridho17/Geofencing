package com.example.geofencing.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geofencing.Config;
import com.example.geofencing.databinding.ActivityChildLoginBinding;
import com.example.geofencing.dialog.ChildInfo;
import com.example.geofencing.ui.child.ChildActivity;
import com.example.geofencing.util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChildLoginActivity extends AppCompatActivity {

    private static final String TAG = "ChildLoginActivity";
    SharedPreferencesUtil sf;
    private DatabaseReference DB;
    ActivityChildLoginBinding binding;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();

        sf = new SharedPreferencesUtil(ChildLoginActivity.this);

        binding.login.setOnClickListener(v -> {
            if (!validateForm()) return;
            login();
        });

        binding.register.setOnClickListener(v -> {
            Intent intent = new Intent(ChildLoginActivity.this, RegisterChildActivity.class);
            startActivity(intent);
        });

    }

    private void login() {
        if (!validateForm()) return;

        String email = binding.txtEmail.getText().toString();
        String password = binding.txtPassword.getText().toString();

        Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        isChild(Auth.getUid(), new ChildCheckCallback() {
                            @Override
                            public void onResult(boolean isChild) {
                                if (isChild) {
                                    onAuthSuccess(task.getResult().getUser());
                                } else {
                                    Auth.signOut();
                                    Toast.makeText(ChildLoginActivity.this, "Akun ini bukan akun anak",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(ChildLoginActivity.this, "Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Gagal masuk",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void isChild(String uuId, ChildCheckCallback callback) {
        DB.child("childs").child(uuId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
                callback.onResult(false);
            }
        });
    }
    public interface ChildCheckCallback {
        void onResult(boolean isChild);
        void onError();
    }
    private void onAuthSuccess(FirebaseUser user) {
        // Create User If Not Exist
//        DBHelper.saveUser(DB, user.getUid(), name, user.getEmail());
        sf.setPref("account_type", "child", ChildLoginActivity.this);

        // Make alert
        Toast.makeText(this, "Berhasil masuk",
                Toast.LENGTH_SHORT).show();

        // Move to Main Activity
        startActivity(new Intent(ChildLoginActivity.this, ChildActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(binding.txtEmail.getText().toString())) {
            binding.txtEmail.setError("Email harus diisi");
            result = false;
        } else {
            binding.txtEmail.setError(null);
        }

        if (TextUtils.isEmpty(binding.txtPassword.getText().toString())) {
            binding.txtPassword.setError("Silahkan masukkan email valid");
            result = false;
        } else {
            binding.txtPassword.setError(null);
        }

        // Min 6
        if (binding.txtPassword.getText().toString().length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter",
                    Toast.LENGTH_SHORT).show();
        }

        // Must contain @
        if (!binding.txtEmail.getText().toString().contains("@")) {
            Toast.makeText(this, "Email harus mengandung @",
                    Toast.LENGTH_SHORT).show();
        }

        return result;
    }

}