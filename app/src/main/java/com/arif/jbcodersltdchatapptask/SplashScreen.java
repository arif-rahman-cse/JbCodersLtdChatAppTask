package com.arif.jbcodersltdchatapptask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arif.jbcodersltdchatapptask.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setupFirebaseAuth();
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


    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                Toast.makeText(SplashScreen.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
                db.setFirestoreSettings(settings);

                DocumentReference userRef = db.collection(getString(R.string.collection_users)).document(user.getUid());

                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        //User user = task.getResult().toObject(User.class);
                        Users users = task.getResult().toObject(Users.class);
                        ((UserClient) (this.getApplicationContext())).setUser(users);

                    }
                });

                Intent intent = new Intent(SplashScreen.this, ChatRoomActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //Toast.makeText(SplashScreen.this, "Login to: " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
        };
    }


}