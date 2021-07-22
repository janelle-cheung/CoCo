package com.example.collegeconnect.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeMediaActivity;
import com.example.collegeconnect.adapters.CollegeAlbumsTabAdapter;
import com.example.collegeconnect.databinding.FragmentCollegeAlbumsBinding;
import com.example.collegeconnect.ui.search.SearchFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollegeAlbumsFragment extends Fragment {

    public static final String TAG = "CollegeAlbumsFragment";
    private FragmentCollegeAlbumsBinding binding;
    public List<String> albums;

    public CollegeAlbumsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCollegeAlbumsBinding.inflate(getLayoutInflater());
        albums = Arrays.asList(
                getString(R.string.album_all), getString(R.string.album_campus),
                getString(R.string.album_dorms), getString(R.string.album_food), getString(R.string.album_student_life));
        configureTabAdapter();
        Log.i(TAG, "onCreateView");
        return binding.getRoot();
    }

    private void configureTabAdapter() {
        Log.i(TAG, "configureTabAdapter");
        final CollegeAlbumsTabAdapter tabAdapter = new CollegeAlbumsTabAdapter(
                this.getActivity().getSupportFragmentManager(),
                albums, albums.size());
        binding.viewPager.setAdapter(tabAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        for (int i = 0; i < albums.size(); i++) {
            binding.tabLayout.getTabAt(i).setText(albums.get(i));
        }
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}