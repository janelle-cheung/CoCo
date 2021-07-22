package com.example.collegeconnect.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.CollegeMediaActivity;
import com.example.collegeconnect.adapters.CollegeAlbumAdapter;
import com.example.collegeconnect.databinding.FragmentAlbumBinding;
import com.example.collegeconnect.models.CollegeMedia;
import com.example.collegeconnect.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {

    public static final String TAG = "AlbumFragment";
    private FragmentAlbumBinding binding;
    private String collegeId;
    private String albumName;
    private List<CollegeMedia> allMedia;
    private CollegeAlbumAdapter adapter;
    public static final int NUM_COLUMNS = 2;

    public AlbumFragment() {}

    public AlbumFragment(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlbumBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        collegeId = ((CollegeMediaActivity) getActivity()).collegeId;
        Log.i(TAG, albumName);

        allMedia = new ArrayList<>();
        adapter = new CollegeAlbumAdapter(getContext(), allMedia);
        binding.rvMedia.setAdapter(adapter);
        binding.rvMedia.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));

        queryCollegeMedia();
    }

    private void queryCollegeMedia() {
        ParseQuery<CollegeMedia> query = ParseQuery.getQuery(CollegeMedia.class);
        query.whereEqualTo(CollegeMedia.KEY_COLLEGE_UNIT_ID, collegeId);

        if (!albumName.equals(getString(R.string.album_all))) {
            query.whereEqualTo(CollegeMedia.KEY_ALBUM_NAME, albumName);
        }

        query.findInBackground(new FindCallback<CollegeMedia>() {
            @Override
            public void done(List<CollegeMedia> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem with querying college media ", e);
                    return;
                }
                Log.i(TAG, "Success querying college media");

                Log.i(TAG, String.valueOf(objects.size()));
                if (objects.size() > 0) {
                    allMedia.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    // Hide RV and show message if there are no photos/videos for this album
                    binding.noMediaMessage.setVisibility(View.VISIBLE);
                    binding.rvMedia.setVisibility(View.GONE);
                }
            }
        });
    }
}