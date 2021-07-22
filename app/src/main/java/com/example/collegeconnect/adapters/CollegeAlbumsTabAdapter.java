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
    private List<String> allAlbums;

    public CollegeAlbumsTabAdapter(@NonNull FragmentManager fragmentManager, List<String> allAlbums, int tabCount) {
        super(fragmentManager);
        this.allAlbums = allAlbums;
        this.tabCount = tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return allAlbums.get(position);
    }

    // Recreates fragment upon notifyDataSetChanged()
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return new AlbumFragment(allAlbums.get(position));
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
