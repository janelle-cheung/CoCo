package com.example.collegeconnect.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.SelectTypeActivity;
import com.example.collegeconnect.activities.SignupActivity;
import com.example.collegeconnect.databinding.FragmentSignupRegistrationBinding;
import com.example.collegeconnect.databinding.FragmentSignupTypeBinding;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupRegistrationFragment extends Fragment {

    public static final String TAG = "SignupRegistrationFragment";
    private FragmentSignupRegistrationBinding binding;

    public SignupRegistrationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TransitionInflater transitionInflater = TransitionInflater.from(requireContext());
        setExitTransition(transitionInflater.inflateTransition(android.R.transition.fade));

        binding = FragmentSignupRegistrationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignupActivity signupActivityInstance = (SignupActivity) getActivity();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!checkForValidInputs()) return;
//
//                signupActivityInstance.setUsername(capitalizeName(binding.etName.getText().toString()));
//                signupActivityInstance.setEmail(binding.etEmail.getText().toString());
//                signupActivityInstance.setPassword(binding.etPassword.getText().toString());
                signupActivityInstance.replaceFragment(SignupTypeFragment.class);
            }
        });
    }

    private boolean checkForValidInputs() {
        // Check for empty fields
        if (binding.etName.getText().toString().isEmpty() ||
                binding.etEmail.getText().toString().isEmpty() ||
                binding.etPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check for valid email format
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.etEmail.getText().toString());
        if (!matcher.find()) {
            Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String capitalizeName(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}