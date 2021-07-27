package com.example.collegeconnect.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.collegeconnect.R;
import com.example.collegeconnect.adapters.CollegeAlbumsTabAdapter;
import com.example.collegeconnect.databinding.FragmentCollegeAlbumsBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;

public class CollegeAlbumsFragment extends Fragment {

    public static final String TAG = "CollegeAlbumsFragment";
    private FragmentCollegeAlbumsBinding binding;

    public CollegeAlbumsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCollegeAlbumsBinding.inflate(getLayoutInflater());
        configureTabAdapter();
        return binding.getRoot();
    }

    private void configureTabAdapter() {
        List<String> allAlbums = Arrays.asList(getResources().getStringArray(R.array.all_albums_array));
        final CollegeAlbumsTabAdapter tabAdapter = new CollegeAlbumsTabAdapter(
                this.getActivity().getSupportFragmentManager(),
                allAlbums, allAlbums.size());
        binding.viewPager.setAdapter(tabAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
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

    public void onMediaUploaded() {
        binding.viewPager.getAdapter().notifyDataSetChanged();
    }
}