package com.example.collegeconnect.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.activities.SignupActivity;
import com.example.collegeconnect.databinding.FragmentSignupTypeBinding;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;

public class SignupTypeFragment extends Fragment {

    public static final String TAG = "SignupTypeFragment";
    private FragmentSignupTypeBinding binding;

    public SignupTypeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TransitionInflater transitionInflater = TransitionInflater.from(requireContext());
        setEnterTransition(transitionInflater.inflateTransition(android.R.transition.slide_right));
        setExitTransition(transitionInflater.inflateTransition(android.R.transition.fade));

        binding = FragmentSignupTypeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignupActivity signupActivityInstance = (SignupActivity) getActivity();

        binding.btnHighSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupActivityInstance.setType(User.KEY_HIGHSCHOOL);
                signupActivityInstance.replaceFragment(SignupSchoolInfoFragment.class);
            }
        });

        binding.btnCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupActivityInstance.setType(User.KEY_COLLEGE);
                signupActivityInstance.replaceFragment(SignupSchoolInfoFragment.class);
            }
        });
    }
}