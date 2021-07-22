package com.example.collegeconnect.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.collegeconnect.ui.AlbumFragment;
import com.example.collegeconnect.ui.CollegeAlbumsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollegeAlbumsTabAdapter extends FragmentPagerAdapter {

    public static final String TAG = "CollegeAlbumsTabAdapter";
    private int tabCount;
    private List<String> albums;

    public CollegeAlbumsTabAdapter(@NonNull FragmentManager fragmentManager, List<String> albums, int tabCount) {
        super(fragmentManager);
        this.albums = albums;
        this.tabCount = tabCount;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, String.valueOf(position));
        return new AlbumFragment(albums.get(position));
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
