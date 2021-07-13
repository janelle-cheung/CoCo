package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivitySignupBinding;
import com.example.collegeconnect.databinding.ActivityStartBinding;
import com.parse.ParseUser;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "StartActivity";
    ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            launchMainActivity();
        }

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, SelectTypeActivity.class);
                startActivity(i);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginActivity();
            }
        });
    }

    private void launchLoginActivity() {
        Intent i = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void launchMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}