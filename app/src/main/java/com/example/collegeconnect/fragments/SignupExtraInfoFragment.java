package com.example.collegeconnect.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegeconnect.activities.SignupActivity;
import com.example.collegeconnect.databinding.FragmentSignupExtraInfoBinding;

import org.jetbrains.annotations.NotNull;

public class SignupExtraInfoFragment extends Fragment {

    public static final String TAG = "SignupExtraInfoFragment";
    private FragmentSignupExtraInfoBinding binding;

    public SignupExtraInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TransitionInflater transitionInflater = TransitionInflater.from(requireContext());
        setEnterTransition(transitionInflater.inflateTransition(android.R.transition.slide_right));
        setExitTransition(transitionInflater.inflateTransition(android.R.transition.fade));

        binding = FragmentSignupExtraInfoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignupActivity signupActivityInstance = (SignupActivity) getActivity();

        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForValidInputs()) return;
                signupActivityInstance.setFrom(binding.etFrom.getText().toString());
                signupActivityInstance.setAcademicInterests(binding.etAcademics.getText().toString());
                signupActivityInstance.setExtracurricularInterests(binding.etExtracurriculars.getText().toString());

                signupActivityInstance.createUserAccount();
            }
        });
    }

    private boolean checkForValidInputs() {
        if (binding.etFrom.getText().toString().isEmpty() ||
                binding.etAcademics.getText().toString().isEmpty() ||
                binding.etExtracurriculars.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}