package com.example.collegeconnect.ui.search;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeDetailsActivity;
import com.example.collegeconnect.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    public static final String KEY_COLLEGE = "college";
    private SearchViewModel mViewModel;
    private FragmentSearchBinding binding;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        binding.ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = binding.etCollege.getText().toString();
                if (input.isEmpty()) { return; }
                // TO-DO: check if college actually exists with API
                launchDetailsActivity(input);
            }
        });

        return binding.getRoot();
    }

    private void launchDetailsActivity(String college) {
        Intent i = new Intent(getContext(), CollegeDetailsActivity.class);
        i.putExtra(KEY_COLLEGE, college);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}