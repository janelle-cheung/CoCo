package com.example.collegeconnect.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentProfileBinding;
import com.example.collegeconnect.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        User user = (User) ParseUser.getCurrentUser();

        // Display data
        binding.tvName.setText(user.getUsername());
        binding.tvGradeAndSchool.setText(
                String.format("%s at %s", user.getGrade(), user.getSchool()));
        binding.tvFromValue.setText(user.getFrom());
        binding.tvExtracurricularsValue.setText(user.getExtracurriculars());
        binding.tvAcademicsValue.setText(user.getAcademics());

        if (user.hasProfileImage()) {
            Glide.with(getContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        } else {
            // Display placeholder image if user has no profile image
            Glide.with(getContext())
                    .load(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        }

        // Hide high school text if it's a high school student
        if (user.isInHighSchool()) {
            binding.tvHighSchoolPrompt.setVisibility(View.GONE);
            binding.tvHighSchoolValue.setVisibility(View.GONE);
        } else {
            binding.tvHighSchoolValue.setText(user.getHighSchool());
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}