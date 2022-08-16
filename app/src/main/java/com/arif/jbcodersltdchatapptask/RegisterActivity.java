package com.arif.jbcodersltdchatapptask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arif.jbcodersltdchatapptask.databinding.ActivityRegisterBinding;
import com.arif.jbcodersltdchatapptask.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    ActivityRegisterBinding binding;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mDb = FirebaseFirestore.getInstance();

        binding.btnRegister.setOnClickListener(view -> {
            if (isValid()){
                Utils.hideKeyboard(this);
                registerNewEmail(binding.inputName.getText().toString(), binding.inputEmail.getText().toString(), binding.inputPassword.getText().toString());
            }
        });
    }

    private void registerNewEmail(String name, String email, String password) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                        //insert some default data
                        Users users = new Users();
                        users.setName(name);
                        users.setEmail(email);
                        users.setUsername(email);
                        users.setPhone_number("01721305021");

                        //users.setUsername(email.substring(0, email.indexOf("@")));
                        users.setUser_id(FirebaseAuth.getInstance().getUid());

                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
                        mDb.setFirestoreSettings(settings);

                        DocumentReference newUserRef = mDb
                                .collection(getString(R.string.collection_users))
                                .document(FirebaseAuth.getInstance().getUid());

                        newUserRef.set(users).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                //Got Login activity
                                redirectLoginScreen();

                            } else {
                                Log.d(TAG, "onComplete: Error: ");
                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                    }

                    // ...
                });

    }

    private void redirectLoginScreen() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValid() {
        if (binding.inputName.getText().toString().isEmpty()){
            binding.inputName.setError("Enter Name");
            binding.inputName.requestFocus();
            return false;
        }  else if (binding.inputEmail.getText().toString().isEmpty()){
            binding.inputEmail.setError("Enter Email");
            binding.inputEmail.requestFocus();
            return false;

        } else if (binding.inputPassword.getText().toString().isEmpty()){
            binding.inputPassword.setError("Enter Password");
            binding.inputPassword.requestFocus();
            return false;

        }else if (binding.inputConfirmPassword.getText().toString().isEmpty()){
            binding.inputConfirmPassword.setError("Enter Confirm Password");
            binding.inputConfirmPassword.requestFocus();
            return false;
        }
        else if (!Utils.doStringsMatch(Objects.requireNonNull(binding.inputPassword.getText()).toString(), binding.inputConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}