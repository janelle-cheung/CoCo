package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.collegeconnect.R;
import com.example.collegeconnect.ui.CollegeAlbumsFragment;
import com.example.collegeconnect.ui.CollegeDetailsFragment;
import com.example.collegeconnect.ui.search.SearchFragment;

public class CollegeMediaActivity extends AppCompatActivity {

    public static final String TAG = "CollegeMediaActivity";
    public String collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_media);

        collegeId = getIntent().getStringExtra(CollegeDetailsFragment.KEY_COLLEGE_DETAILS_FRAGMENT_COLLEGE_ID);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, CollegeAlbumsFragment.class, new Bundle())
                .commit();
    }

    public void changeFragment(Class fragmentClass, Bundle bundle) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, fragmentClass, bundle)
                .addToBackStack(null)
                .commit();
    }
}