package com.example.collegeconnect.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.StartConversationActivity;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.ui.CollegeDetailsFragment;
import com.example.collegeconnect.databinding.FragmentProfileBinding;
import com.example.collegeconnect.models.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final int START_CONVERSATION_REQUEST_CODE = 20;
    public static final String KEY_COLLEGE_USER = "college user";
    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    User userShown;
    User currUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Check if bundle has arguments - if so, we are displaying another user's profile
        // If bundle is null, we're displaying the current user's profile
        Bundle bundle = this.getArguments();
        currUser = (User) ParseUser.getCurrentUser();
        if (bundle != null) {
            userShown = Parcels.unwrap(getArguments().getParcelable(CollegeDetailsFragment.KEY_OTHER_PROFILE));
        } else {
            userShown = currUser;
        }

        displayInfo();

        return binding.getRoot();
    }

    public void displayInfo() {
        binding.tvName.setText(userShown.getUsername());
        String currSchool;
        if (userShown.isInHighSchool()) { currSchool = userShown.getHighSchool(); }
        else { currSchool = userShown.getCollege(); }
        binding.tvGradeAndSchool.setText(
                String.format("%s at %s", userShown.getGrade(), currSchool));
        binding.tvFromValue.setText(userShown.getFrom());
        binding.tvExtracurricularsValue.setText(userShown.getExtracurriculars());
        binding.tvAcademicsValue.setText(userShown.getAcademics());

        if (userShown.hasProfileImage()) {
            Glide.with(getContext())
                    .load(userShown.getProfileImageUrl())
                    .placeholder(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        } else {
            // Display placeholder image if user has no profile image
            Glide.with(getContext())
                    .load(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        }

        // Hide high school-related text views if userShown is in HS, set value otherwise
        if (userShown.isInHighSchool()) {
            binding.tvHighSchoolPrompt.setVisibility(View.GONE);
            binding.tvHighSchoolValue.setVisibility(View.GONE);
        } else {
            binding.tvHighSchoolValue.setText(userShown.getHighSchool());
        }

        if (currUser.isInHighSchool() && !userShown.isInHighSchool()) {
            checkForExistingConversation();
        }

        binding.btnStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), StartConversationActivity.class);
                i.putExtra(KEY_COLLEGE_USER, Parcels.wrap(userShown));
                startActivityForResult(i, START_CONVERSATION_REQUEST_CODE);
            }
        });
    }

    // Only checked when currUser is in HS and userShown is in C
    // StartConversation button needs to be shown
    // If a conversation already exists between them, disable button and notify HS user
    private void checkForExistingConversation() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_COLLEGE_STUDENT, userShown);
        query.whereEqualTo(Conversation.KEY_HIGHSCHOOL_STUDENT, currUser);
        query.findInBackground(new FindCallback<Conversation>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem with querying for existing conversation ", e);
                } else {
                    // If conversation exists, disable button and notify HS user
                    if (objects.size() > 0) {
                        binding.btnStartConversation.setEnabled(false);
                        binding.btnStartConversation.setText(getString(R.string.existing_conversation));
                    }
                    // Show StartConversation button and set on click listener
                    binding.btnStartConversation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        binding.btnStartConversation.setEnabled(false);
        binding.btnStartConversation.setText(getString(R.string.existing_conversation));
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}