package com.example.collegeconnect.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.databinding.FragmentSearchBinding;

import okhttp3.Headers;

public class SearchFragment extends Fragment {

    public static final String KEY_SEARCH_FRAG_COLLEGE_ID = "SearchFrag collegeId";
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
                launchSearchResultActivity(input);
            }
        });

        return binding.getRoot();
    }

    private void launchSearchResultActivity(String collegeId) {
        Intent i = new Intent(getContext(), SearchResultActivity.class);
        i.putExtra(KEY_SEARCH_FRAG_COLLEGE_ID, collegeId);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}