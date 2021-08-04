package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivitySelectTypeBinding;

public class SelectTypeActivity extends AppCompatActivity {

    private ActivitySelectTypeBinding binding;
    public static final String KEY_TYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}