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

        RadioGroup rGroupType = binding.rGroupType;
        rGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String type;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioHighSchool) {
                    type = "high school";
                } else {
                    type = "college";
                }

                Intent i = new Intent(SelectTypeActivity.this, SignupActivity.class);
                i.putExtra(KEY_TYPE, type);
                startActivity(i);
            }
        });
    }
}