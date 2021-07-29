package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityLoginBinding;
import com.example.collegeconnect.databinding.ActivitySignupBinding;
import com.example.collegeconnect.notifications.FirebaseNotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) { // Successful log-in
                    launchMainActivity();
                } else { // Unsuccessful log-in
                    Toast.makeText(LoginActivity.this, "Error with logging in", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error logging in");
                    return;
                }
            }
        });
    }

    private void launchMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void retrieveToken() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "retrieveToken: Fetching FCM token failed");
                        return;
                    }
                    // Get and save new FCM registration token in Parse
                    String token = task.getResult();
                    Log.i(TAG, "retrieveToken: " + token);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "retrieveToken: Error fetching FCM token ", e);
        }
    }
}