package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.util.Log;

import com.example.collegeconnect.R;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.ui.CollegeDetailsFragment;
import com.example.collegeconnect.ui.profile.ProfileFragment;
import com.example.collegeconnect.ui.search.SearchFragment;

public class SearchResultActivity extends AppCompatActivity {

    public static final String TAG = "SearchResultActivity";
    public String collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        collegeId = getIntent().getStringExtra(SearchFragment.KEY_SEARCH_FRAG_COLLEGE_ID);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, CollegeDetailsFragment.class, new Bundle())
                .commit();
    }

    public void replaceFragment(Class fragmentClass, Bundle bundle) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, fragmentClass, bundle)
                .addToBackStack(null)
                .commit();
    }
}