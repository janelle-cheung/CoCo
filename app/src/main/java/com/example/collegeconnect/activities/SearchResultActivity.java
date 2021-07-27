package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.collegeconnect.R;
import com.example.collegeconnect.fragments.CollegeDetailsFragment;
import com.example.collegeconnect.fragments.SearchFragment;

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