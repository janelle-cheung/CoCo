package com.example.collegeconnect.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegeconnect.OnDoubleTapListener;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentCollegeStatisticsBinding;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.Save;
import com.example.collegeconnect.models.User;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollegeStatisticsFragment extends Fragment {

    public static final String TAG = "CollegeStatisticsFragment";
    private FragmentCollegeStatisticsBinding binding;
    private College college;
    private String collegeId;
    private User currUser;
    private boolean collegeSaved;
    private boolean deleteSave = false;

    public CollegeStatisticsFragment() {}

    public CollegeStatisticsFragment(College college, User currUser) {
        this.college = college;
        collegeId = this.college.getCollegeUnitId();
        this.currUser = currUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCollegeStatisticsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint({"LongLogTag", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayStatistics();

        // High school students have the option to save the college to their list
        if (currUser.isInHighSchool()) {
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
        query.whereEqualTo(Save.KEY_USER, currUser);
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
        save.setUser(currUser);
        save.setCollegeName(college.getName());
        save.setCollegeUnitId(collegeId);
        save.setColumn(Save.COLUMN_SAVED); // Default column is "Saved"
        save.saveInBackground(new SaveCallback() {
            @SuppressLint("LongLogTag")
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
            @SuppressLint("LongLogTag")
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

    @SuppressLint("LongLogTag")
    private void displayStatistics() {
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
}