package com.example.collegeconnect.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.OnDoubleTapListener;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeMediaActivity;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.adapters.CollegeStudentsAdapter;
import com.example.collegeconnect.databinding.FragmentCollegeDetailsBinding;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.Save;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.fragments.ProfileFragment;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
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
    private College college;
    private User user;
    private boolean collegeSaved;
    private boolean deleteSave = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollegeDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        collegeStudents = new ArrayList<>();
        adapter = new CollegeStudentsAdapter(getContext(), collegeStudents, this);
        binding.rvCollegeStudents.setAdapter(adapter);
        binding.rvCollegeStudents.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));

        collegeId = ((SearchResultActivity) getActivity()).collegeId;
        user = (User) ParseUser.getCurrentUser();

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

        // High school students have the option to save the college to their list
        if (user.isInHighSchool()) {
            binding.ibSaveToList.setEnabled(false); // Disable button while we access Parse
            getSaveFromParse(); // Check if button is saved in Parse, and fill button if yes
            binding.ibSaveToList.setEnabled(true);
            binding.ibSaveToList.setOnTouchListener(new OnDoubleTapListener(getContext()) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    binding.ibSaveToList.setEnabled(false); // Disable button while we access Parse
                    if (collegeSaved) {
                        deleteSave = true;
                        getSaveFromParse();
                    } else {
                        createSave();
                    }
                    collegeSaved = !collegeSaved;
                    binding.ibSaveToList.setEnabled(true);
                }
            });
        } else {
            binding.ibSaveToList.setVisibility(View.GONE);
        }
    }

    private void getSaveFromParse() {
        ParseQuery<Save> query = ParseQuery.getQuery(Save.class);
        query.whereEqualTo(Save.KEY_USER, user);
        query.whereEqualTo(Save.KEY_COLLEGE_UNIT_ID, collegeId);
        query.findInBackground(new FindCallback<Save>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void done(List<Save> objects, ParseException e) {
                if (objects.size() == 0) {
                    collegeSaved = false;
                } else {
                    if (deleteSave) {
                        deleteSave(objects.get(0));
                        return;
                    }
                    collegeSaved = true;
                    binding.ibSaveToList.setImageResource(R.mipmap.ic_save_filled_foreground);
                }
            }
        });
    }

    private void createSave() {
        Save save = new Save();
        save.setUser(user);
        save.setCollegeName(college.getName());
        save.setCollegeUnitId(collegeId);
        save.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error saving college", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error creating save ", e);
                } else {
                    collegeSaved = true;
                    binding.ibSaveToList.setImageResource(R.mipmap.ic_save_filled_foreground); // Re-enable button
                }
            }
        });
    }

    private void deleteSave(Save save) {
        save.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error unsaving college", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting save ", e);
                    return;
                }
                collegeSaved = false;
                binding.ibSaveToList.setImageResource(R.mipmap.ic_save_unfilled_foreground);
                binding.ibSaveToList.setEnabled(true); // Re-enable button
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