package com.example.collegeconnect.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.allyants.boardview.BoardView;
import com.example.collegeconnect.adapters.CustomBoardAdapter;
import com.example.collegeconnect.databinding.FragmentProfileCollegeListBinding;
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
                sortAndShowCollegeList();
            }
        });
    }

    private void sortAndShowCollegeList() {
        ArrayList<CustomBoardAdapter.CustomColumn> data = new ArrayList<>();
        ArrayList<Object> savedList = new ArrayList<>();
        ArrayList<Object> safetyList = new ArrayList<>();
        ArrayList<Object> matchList = new ArrayList<>();
        ArrayList<Object> reachList = new ArrayList<>();

        for (Save save : collegeList) {
            String column = save.getColumn();
            if (column.equals(Save.COLUMN_SAVED)) {
                savedList.add(save);
            } else if (column.equals(Save.COLUMN_SAFETY)) {
                safetyList.add(save);
            } else if (column.equals(Save.COLUMN_MATCH)) {
                matchList.add(save);
            } else if (column.equals(Save.COLUMN_REACH)) {
                reachList.add(save);
            }
        }

        data.add(new CustomBoardAdapter.CustomColumn(Save.COLUMN_SAVED, savedList));
        data.add(new CustomBoardAdapter.CustomColumn(Save.COLUMN_SAFETY, safetyList));
        data.add(new CustomBoardAdapter.CustomColumn(Save.COLUMN_MATCH, matchList));
        data.add(new CustomBoardAdapter.CustomColumn(Save.COLUMN_REACH, reachList));
        CustomBoardAdapter boardAdapter = new CustomBoardAdapter(getContext(), data);
        binding.bvCollegeList.setAdapter(boardAdapter);
        binding.bvCollegeList.setOnDragItemListener(new BoardView.DragItemStartCallback() {
            @Override
            public void startDrag(View itemView, int originalPosition, int originalColumn) {}

            @Override
            public void changedPosition(View itemView, int originalPosition, int originalColumn, int newPosition, int newColumn) {}

            @Override
            public void dragging(View itemView, MotionEvent event) {}

            @Override
            public void endDrag(View itemView, int originalPosition, int originalColumn, int newPosition, int newColumn) {
                if (originalColumn != newColumn) {
                    Save save = (Save) itemView.getTag();
                    updateParseCollegeList(save, newColumn);
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void updateParseCollegeList(Save save, int newColumn) {
        String newColumnName = Save.COLUMNS_ARRAY[newColumn];
        save.setColumn(newColumnName);
        save.saveInBackground();
    }
}