package com.example.collegeconnect.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.collegeconnect.fragments.ProfileCollegeListFragment;
import com.example.collegeconnect.fragments.ProfileDetailsFragment;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileTabAdapter extends FragmentPagerAdapter {

    public static final String TAG = "ProfileTabAdapter";
    private int tabCount;
    private List<String> profileTabs;
    private User userShown;

    public ProfileTabAdapter(@NonNull @NotNull FragmentManager fm, List<String> profileTabs, int tabCount, User userShown) {
        super(fm);
        this.tabCount = tabCount;
        this.profileTabs = profileTabs;
        this.userShown = userShown;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return profileTabs.get(position);
    }

    @NonNull @NotNull @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ProfileDetailsFragment(userShown);
        } else if (position == 1) {
            return new ProfileCollegeListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
