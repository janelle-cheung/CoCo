package com.example.collegeconnect.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentCollegeMediaDetailsBinding;
import com.example.collegeconnect.models.CollegeMedia;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

public class CollegeMediaDetailsFragment extends Fragment {

    private FragmentCollegeMediaDetailsBinding binding;

    public CollegeMediaDetailsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCollegeMediaDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        CollegeMedia clickedMedia = Parcels.unwrap(bundle.getParcelable(AlbumFragment.KEY_CLICKED_MEDIA));
        User author = clickedMedia.getUser();

        String caption = clickedMedia.getCaption();
        if (caption.equals("")) { binding.tvCaption.setVisibility(View.GONE); }
        else { binding.tvCaption.setText(clickedMedia.getCaption()); }
        binding.tvUsername.setText(author.getUsername());
        binding.tvDate.setText(clickedMedia.getFormattedDate());
        Glide.with(getContext())
                .load(clickedMedia.getFile().getUrl())
                .into(binding.ivCollegeMedia);
        if (author.hasProfileImage()) {
            Glide.with(getContext())
                    .load(author.getProfileImageUrl())
                    .circleCrop()
                    .into(binding.ivUserProfileImage);
        } else {
            // Display placeholder image if user has no profile image
            Glide.with(getContext())
                    .load(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivUserProfileImage);
        }
    }
}