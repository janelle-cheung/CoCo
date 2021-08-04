package com.example.collegeconnect.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.MainActivity;
import com.example.collegeconnect.activities.SelectTypeActivity;
import com.example.collegeconnect.activities.SignupActivity;
import com.example.collegeconnect.databinding.FragmentSignupExtraInfoBinding;
import com.example.collegeconnect.databinding.FragmentSignupTypeBinding;
import com.example.collegeconnect.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class SignupExtraInfoFragment extends Fragment {

    public static final String TAG = "SignupExtraInfoFragment";
    private FragmentSignupExtraInfoBinding binding;

    public SignupExtraInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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