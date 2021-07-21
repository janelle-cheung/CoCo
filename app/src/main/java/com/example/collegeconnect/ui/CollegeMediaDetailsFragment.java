package com.example.collegeconnect.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.databinding.FragmentCollegeMediaBinding;

public class CollegeMediaDetailsFragment extends Fragment {

    private FragmentCollegeMediaBinding binding;

    public CollegeMediaDetailsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCollegeMediaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}