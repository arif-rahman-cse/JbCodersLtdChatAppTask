package com.arif.jbcodersltdchatapptask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arif.jbcodersltdchatapptask.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.btnLogin.setOnClickListener(view -> {
            String phone = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            if (isValid(phone, password)){
                Utils.hideKeyboard(LoginActivity.this);
                login(phone, password);
            }
        });

        binding.btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }


    private boolean isValid(String phone, String password) {
        if (phone.isEmpty()) {
            binding.etEmail.setError("Enter Email Address");
            binding.etEmail.requestFocus();
            return false;

        }  else if (password.isEmpty()) {
            binding.etPassword.setError("Enter Password");
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void login(String phone, String password) {
        binding.loginPb.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(phone, password)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "signIn: onComplete called");
                    if (task.isSuccessful()) {
                        binding.loginPb.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }

                }).addOnFailureListener(e ->{
                    binding.loginPb.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar
                            .make(binding.mainView, "Login Failed! "+e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", view -> {
                                binding.loginPb.setVisibility(View.GONE);
                                login(phone, password);
                            });

                    snackbar.show();
                });

    }
}