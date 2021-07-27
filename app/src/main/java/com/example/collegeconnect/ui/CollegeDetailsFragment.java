package com.example.collegeconnect.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeMediaActivity;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.adapters.CollegeStudentsAdapter;
import com.example.collegeconnect.databinding.FragmentCollegeDetailsBinding;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.ui.profile.ProfileFragment;
import com.example.collegeconnect.ui.search.SearchFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class CollegeDetailsFragment extends Fragment implements CollegeStudentsAdapter.OnCollegeStudentListener {

    public static final String TAG = "CollegeDetails";
    public static final String KEY_OTHER_PROFILE = "other profile";
    public static final String KEY_COLLEGE_DETAILS_FRAGMENT_COLLEGE_ID = "CollegeDetailsFragment collegeId";
    public static final int NUM_COLUMNS = 3;
    private FragmentCollegeDetailsBinding binding;
    private CollegeStudentsAdapter adapter;
    private String collegeId;
    private List<User> collegeStudents;
    College college;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollegeDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        collegeStudents = new ArrayList<>();
        adapter = new CollegeStudentsAdapter(getContext(), collegeStudents, this);
        binding.rvCollegeStudents.setAdapter(adapter);
        binding.rvCollegeStudents.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));

        collegeId = ((SearchResultActivity) getActivity()).collegeId;

        getCollegeInfo();
        queryCollegeStudents();

        binding.btnViewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CollegeMediaActivity.class);
                i.putExtra(KEY_COLLEGE_DETAILS_FRAGMENT_COLLEGE_ID, collegeId);
                startActivity(i);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCollegeInfo() {
        CollegeAIClient.getCollegeDetails(collegeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    String success = jsonObject.getString("success");
                    if (success.equals("false")) {
                        Log.e(TAG, "GET request returned but is unsuccessful in getting data");
                        return;
                    }
                    college = College.fromJSON(jsonObject.getJSONArray("colleges").getJSONObject(0));
                    displayCollegeInfo();
                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON exception ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.e(TAG, "GET request for college info failed ", e);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void displayCollegeInfo() {
        if (college.hasCampusImage()) {
            Glide.with(getContext())
                    .load(college.getCampusImageUrl())
                    .into(binding.ivCampusImage);
        } else {
            Glide.with(getContext())
                    .load(R.mipmap.university_image_placeholder_foreground)
                    .into(binding.ivCampusImage);
        }

        binding.tvName.setText(college.getName());
        if (college.getCityState() == null) {
            binding.tvLocation.setVisibility(View.GONE);
        } else {
            binding.tvLocation.setText(college.getCityState());
        }

        if (college.getAcceptanceRate() == -1) {
            binding.tvAcceptanceRatePrompt.setVisibility(View.GONE);
            binding.tvAcceptanceRateValue.setVisibility(View.GONE);
        } else {
            binding.tvAcceptanceRateValue.setText(String.format("%.1f%%", college.getAcceptanceRate()));
        }

        if (college.getUndergradSize() == null) {
            binding.tvUndergradSizePrompt.setVisibility(View.GONE);
            binding.tvUndergradSizeValue.setVisibility(View.GONE);
        } else {
            binding.tvUndergradSizeValue.setText(college.getUndergradSize());
        }

        if (college.getWebsite() == null) {
            binding.tvWebsitePrompt.setVisibility(View.GONE);
            binding.tvWebsiteValue.setVisibility(View.GONE);
        } else {
            binding.tvWebsiteValue.setText(college.getWebsite());
        }

        if (college.getShortDescription() == null) {
            binding.tvShortDescription.setVisibility(View.GONE);
        } else {
            binding.tvShortDescription.setText(college.getShortDescription());
        }
    }

    private void queryCollegeStudents() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_COLLEGE_ID, collegeId);

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem with querying college students ", e);
                    return;
                }

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