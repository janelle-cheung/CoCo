package com.example.collegeconnect.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.OnDoubleTapListener;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeMediaActivity;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.adapters.CollegeDetailsTabAdapter;
import com.example.collegeconnect.adapters.CollegeStudentsAdapter;
import com.example.collegeconnect.adapters.ProfileTabAdapter;
import com.example.collegeconnect.databinding.FragmentCollegeDetailsBinding;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.Save;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.fragments.ProfileFragment;
import com.google.android.material.tabs.TabLayout;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Headers;

public class CollegeDetailsFragment extends Fragment {

    public static final String TAG = "CollegeDetails";
    public static final String KEY_COLLEGE_DETAILS_FRAGMENT_COLLEGE_ID = "CollegeDetailsFragment collegeId";
    private FragmentCollegeDetailsBinding binding;
    private String collegeId;
    private College college;
    private User user;
    private boolean collegeSaved;
    private boolean deleteSave = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollegeDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        collegeId = ((SearchResultActivity) getActivity()).collegeId;
        user = (User) ParseUser.getCurrentUser();

        getCollegeInfo();

        binding.btnViewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CollegeMediaActivity.class);
                i.putExtra(KEY_COLLEGE_DETAILS_FRAGMENT_COLLEGE_ID, collegeId);
                startActivity(i);
            }
        });
    }

    private void configureTabAdapter() {
        List<String> tabs = Arrays.asList(getResources().getStringArray(R.array.college_details_tabs_array));
        final CollegeDetailsTabAdapter tabAdapter = new CollegeDetailsTabAdapter(this.getChildFragmentManager(),
                                                    tabs, tabs.size(), college, user);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCollegeInfo() {
        CollegeAIClient.getCollegeDetails(collegeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    String success = jsonObject.getString("success");
                    if (success.equals("false")) {
                        Log.e(TAG, "GET request returned but is unsuccessful in getting data");
                        return;
                    }
                    college = College.fromJSON(jsonObject.getJSONArray("colleges").getJSONObject(0));
                    displayCollegeInfo();
                    configureTabAdapter();
                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON exception ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.e(TAG, "GET request for college info failed ", e);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void displayCollegeInfo() {
        if (college.hasCampusImage()) {
            Glide.with(getContext())
                    .load(college.getCampusImageUrl())
                    .into(binding.ivCampusImage);
        } else {
            Glide.with(getContext())
                    .load(R.mipmap.university_image_placeholder_foreground)
                    .into(binding.ivCampusImage);
        }

        binding.tvName.setText(college.getName());
        if (college.getCityState() == null) {
            binding.tvLocation.setVisibility(View.GONE);
        } else {
            binding.tvLocation.setText(college.getCityState());
        }
    }
}