package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentCollegeDetailsBinding;
import com.example.collegeconnect.ui.profile.ProfileFragment;
import com.example.collegeconnect.ui.search.SearchFragment;

public class SearchResultActivity extends AppCompatActivity {

    public static final String TAG = "SearchResultActivity";
    public static final String KEY_COLLEGE_2 = "college2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_COLLEGE_2, getIntent().getStringExtra(SearchFragment.KEY_COLLEGE));

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, CollegeDetailsFragment.class, bundle)
                .commit();
    }

    public void replaceFragmentWithOtherProfile(Bundle bundle) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, ProfileFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }
}