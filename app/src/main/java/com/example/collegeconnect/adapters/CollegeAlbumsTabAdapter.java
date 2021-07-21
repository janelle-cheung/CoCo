package com.example.collegeconnect.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.collegeconnect.ui.AlbumFragment;
import com.example.collegeconnect.ui.CollegeAlbumsFragment;

import org.jetbrains.annotations.NotNull;

public class CollegeAlbumsTabAdapter extends FragmentPagerAdapter {

    private int tabCount;

    public CollegeAlbumsTabAdapter(@NonNull FragmentManager fragmentManager, int tabCount) {
        super(fragmentManager);
        this.tabCount = tabCount;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return new AlbumFragment(CollegeAlbumsFragment.albums.get(position));
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
