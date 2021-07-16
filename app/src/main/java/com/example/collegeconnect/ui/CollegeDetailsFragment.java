package com.example.collegeconnect.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.adapters.CollegeStudentsAdapter;
import com.example.collegeconnect.databinding.FragmentCollegeDetailsBinding;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.ui.profile.ProfileFragment;
import com.example.collegeconnect.ui.search.SearchFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CollegeDetailsFragment extends Fragment implements CollegeStudentsAdapter.OnCollegeStudentListener {

    public static final String TAG = "CollegeDetails";
    public static final String KEY_OTHER_PROFILE = "other profile";
    public static final int NUM_COLUMNS = 3;
    private FragmentCollegeDetailsBinding binding;
    private CollegeStudentsAdapter adapter;
    private String college;
    private List<User> collegeStudents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollegeDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        collegeStudents = new ArrayList<>();
        adapter = new CollegeStudentsAdapter(getContext(), collegeStudents, this);
        binding.rvCollegeStudents.setAdapter(adapter);
        binding.rvCollegeStudents.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            college = bundle.getString(SearchResultActivity.KEY_COLLEGE_2);
        } else {
            Log.e(TAG, "Bundle empty");
        }

        queryCollegeStudents();
    }

    private void queryCollegeStudents() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_SCHOOL, college);

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem with querying college students ", e);
                    return;
                }
                Log.i(TAG, "Success querying college students");

                if (objects.size() > 0) {
                    binding.tvNoStudents.setVisibility(View.GONE);
                    binding.rvCollegeStudents.setVisibility(View.VISIBLE);
                    adapter.clear();
                    collegeStudents.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    // Hide RV and show message if there are no CoCo users at the college
                    binding.tvNoStudents.setVisibility(View.VISIBLE);
                    binding.rvCollegeStudents.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCollegeStudentClick(int position) {
        User collegeStudent = collegeStudents.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_OTHER_PROFILE, Parcels.wrap(collegeStudent));
        ((SearchResultActivity) getActivity()).replaceFragment(ProfileFragment.class, bundle);
    }
}