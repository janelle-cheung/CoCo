package com.example.collegeconnect.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.databinding.FragmentAlbumBinding;

public class AlbumFragment extends Fragment {

    private FragmentAlbumBinding binding;
    private String albumName;

    public AlbumFragment() {}

    public AlbumFragment(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlbumBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
}