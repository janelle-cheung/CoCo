package com.example.collegeconnect.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.collegeconnect.fragments.CoCoUsersFragment;
import com.example.collegeconnect.fragments.CollegeStatisticsFragment;
import com.example.collegeconnect.fragments.ProfileCollegeListFragment;
import com.example.collegeconnect.fragments.ProfileDetailsFragment;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;

import java.text.CollationElementIterator;
import java.util.List;

public class CollegeDetailsTabAdapter extends FragmentPagerAdapter {

    public static final String TAG = "CollegeDetailsTabAdapter";
    private int tabCount;
    private List<String> tabs;
    private List<String> profileTabs;
    private College collegeShown;
    private User currUser;

    public CollegeDetailsTabAdapter(@NonNull @NotNull FragmentManager fm, List<String> tabs, int tabCount, College collegeShown, User currUser) {
        super(fm);
        this.tabCount = tabCount;
        this.tabs = tabs;
        this.collegeShown = collegeShown;
        this.currUser = currUser;
    }

    @Nullable @org.jetbrains.annotations.Nullable @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }

    @NonNull @NotNull @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new CollegeStatisticsFragment(collegeShown, currUser);
        } else if (position == 1) {
            return new CoCoUsersFragment(collegeShown.getCollegeUnitId());
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}