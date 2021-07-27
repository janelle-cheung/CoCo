package com.example.collegeconnect.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentProfileCollegeListBinding;
import com.example.collegeconnect.databinding.FragmentProfileDetailsBinding;
import com.example.collegeconnect.models.User;

public class ProfileDetailsFragment extends Fragment {

    private FragmentProfileDetailsBinding binding;
    private User userShown;

    public ProfileDetailsFragment() {}

    public ProfileDetailsFragment(User user) {
        this.userShown = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileDetailsBinding.inflate(getLayoutInflater());
        displayInfo();
        return binding.getRoot();
    }

    private void displayInfo() {
        binding.tvFromValue.setText(userShown.getFrom());
        binding.tvExtracurricularsValue.setText(userShown.getExtracurriculars());
        binding.tvAcademicsValue.setText(userShown.getAcademics());

        // Hide high school-related text views if userShown is in HS, set value otherwise
        if (userShown.isInHighSchool()) {
            binding.tvHighSchoolPrompt.setVisibility(View.GONE);
            binding.tvHighSchoolValue.setVisibility(View.GONE);
        } else {
            binding.tvHighSchoolValue.setText(userShown.getHighSchool());
        }
    }
}