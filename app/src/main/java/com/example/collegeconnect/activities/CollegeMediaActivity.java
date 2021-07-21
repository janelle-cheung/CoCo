package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.collegeconnect.R;
import com.example.collegeconnect.ui.CollegeAlbumsFragment;
import com.example.collegeconnect.ui.CollegeDetailsFragment;

public class CollegeMediaActivity extends AppCompatActivity {

    public static final String TAG = "CollegeMediaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_media);

        Bundle bundle = new Bundle();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, CollegeAlbumsFragment.class, bundle)
                .commit();
    }
}