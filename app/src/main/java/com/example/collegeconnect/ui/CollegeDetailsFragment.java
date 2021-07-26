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

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeMediaActivity;
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
                    JSONArray colleges = jsonObject.getJSONArray("colleges");
                    String name = colleges.getJSONObject(0).getString("name");
                    String campusImage = colleges.getJSONObject(0).has("campusImage") ?
                            colleges.getJSONObject(0).getString("campusImage") : null;
                    String city = colleges.getJSONObject(0).has("city") ?
                            colleges.getJSONObject(0).getString("city") : null;
                    String stateAbbr = colleges.getJSONObject(0).has("stateAbbr") ?
                            colleges.getJSONObject(0).getString("stateAbbr") : null;
                    double acceptanceRate = colleges.getJSONObject(0).has("acceptanceRate") ?
                            colleges.getJSONObject(0).getDouble("acceptanceRate") * 100 : -1;
                    String undergradSize = colleges.getJSONObject(0).has("undergraduateSize") ?
                            colleges.getJSONObject(0).getString("undergraduateSize") : null;
                    String website = colleges.getJSONObject(0).has("website") ?
                            colleges.getJSONObject(0).getString("website") : null;
                    String shortDescription = colleges.getJSONObject(0).has("shortDescription") ?
                            colleges.getJSONObject(0).getString("shortDescription") : null;
                    displayCollegeInfo(campusImage, name, city, stateAbbr, acceptanceRate, undergradSize, website, shortDescription);
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

    private void displayCollegeInfo(String campusImage, String name, String city,
                                    String stateAbbr, double acceptanceRate,
                                    String undergradSize, String website, String shortDescription) {
        if (campusImage != null) {
            Glide.with(getContext())
                    .load(campusImage)
                    .into(binding.ivCampusImage);
        } else {
            Glide.with(getContext())
                    .load(R.mipmap.university_image_placeholder_foreground)
                    .into(binding.ivCampusImage);
        }

        binding.tvName.setText(name);
        if (city != null && stateAbbr != null) {
            binding.tvLocation.setText(String.format("%s, %s", city, stateAbbr));
        } else {
            binding.tvLocation.setVisibility(View.GONE);
        }

        if (acceptanceRate != -1) {
            binding.tvAcceptanceRateValue.setText(String.format("%s%%", String.valueOf(acceptanceRate)));
        } else {
            binding.tvAcceptanceRatePrompt.setVisibility(View.GONE);
            binding.tvAcceptanceRateValue.setVisibility(View.GONE);
        }

        if (undergradSize != null) {
            binding.tvUndergradSizeValue.setText(undergradSize);
        } else {
            binding.tvUndergradSizePrompt.setVisibility(View.GONE);
            binding.tvUndergradSizeValue.setVisibility(View.GONE);
        }

        if (website != null) {
            binding.tvWebsiteValue.setText(website);
        } else {
            binding.tvWebsitePrompt.setVisibility(View.GONE);
            binding.tvWebsiteValue.setVisibility(View.GONE);
        }

        if (shortDescription != null) {
            binding.tvShortDescription.setText(shortDescription);

        } else {
            binding.tvShortDescription.setVisibility(View.GONE);
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