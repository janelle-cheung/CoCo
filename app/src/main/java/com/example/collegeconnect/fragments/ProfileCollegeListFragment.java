package com.example.collegeconnect.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentProfileCollegeListBinding;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.Save;
import com.example.collegeconnect.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileCollegeListFragment extends Fragment {

    public static final String TAG = "ProfileCollegeListFragment";
    private FragmentProfileCollegeListBinding binding;
    private User user;
    private List<Save> collegeList;

    public ProfileCollegeListFragment() {}

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileCollegeListBinding.inflate(getLayoutInflater());
        user = (User) ParseUser.getCurrentUser();
        collegeList = new ArrayList<>();
        queryCollegeList();
        return binding.getRoot();
    }

    private void queryCollegeList() {
        ParseQuery<Save> query = ParseQuery.getQuery(Save.class);
        query.whereEqualTo(Save.KEY_USER, user);
        query.findInBackground(new FindCallback<Save>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Save> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error getting your list", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Error querying saved college list");
                    return;
                } else if (objects.size() == 0) {
                    binding.noCollegesSavedMessage.setVisibility(View.VISIBLE);
                    binding.list.setVisibility(View.GONE);
                    return;
                }
                collegeList.addAll(objects);
                showCollegeList();
            }
        });
    }

    private void showCollegeList() {
        for (Save s : collegeList) {
            binding.list.setText(String.format("%s\n%s", binding.list.getText(), s.getCollegeName()));
        }
    }
}