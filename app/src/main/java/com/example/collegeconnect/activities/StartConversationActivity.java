package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityStartConversationBinding;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.ui.profile.ProfileFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class StartConversationActivity extends AppCompatActivity {

    public static final String TAG = "StartConversationActivity";
    ActivityStartConversationBinding binding;
    User user;
    User otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) ParseUser.getCurrentUser();
        otherUser = (User) Parcels.unwrap(getIntent().getParcelableExtra(ProfileFragment.KEY_COLLEGE_USER));
        displayInfo();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String introInput = binding.etIntroInput.getText().toString();
                String inquiriesInput = binding.etInquiriesInput.getText().toString();

                // Check if any fields are empty
                if (introInput.isEmpty() || inquiriesInput.isEmpty()) {
                    Toast.makeText(StartConversationActivity.this, "Don't leave any fields empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                createConversation(introInput, inquiriesInput);
            }
        });
    }

    private void displayInfo() {
        String otherUserName = otherUser.getUsername();

        binding.tvName.setText(otherUserName);
        binding.tvGradeAndSchool.setText(String.format("%s at %s", otherUser.getGrade(), otherUser.getCollege()));
        if (otherUser.hasProfileImage()) {
            Glide.with(this)
                    .load(otherUser.getProfileImageUrl())
                    .placeholder(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        } else {
            // Display placeholder image if user has no profile image
            Glide.with(this)
                    .load(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivProfileImage);
        }
    }

    private void createConversation(String introInput, String inquiriesInput) {
        Conversation conversation = new Conversation();
        conversation.setHighSchoolStudent(user);
        conversation.setCollegeStudent(otherUser);
        conversation.saveInBackground(new SaveCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Successfully saved Conversation");
                    createMessage(introInput, inquiriesInput, conversation);
                } else {
                    Toast.makeText(StartConversationActivity.this,
                            "Something went wrong saving the conversation", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to save Conversation", e);
                }
            }
        });
    }

    private void createMessage(String introInput, String inquiriesInput, Conversation conversation) {
        Message message = new Message();
        String body = introInput + "\n\n" + inquiriesInput;
        message.setBody(body);
        message.setSender(user);
        message.setConversation(conversation);
        message.saveInBackground(new SaveCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(StartConversationActivity.this,
                            "Conversation started", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Successfully saved Message");
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(StartConversationActivity.this,
                            "Something went wrong saving the message", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to save Message", e);
                }
            }
        });
    }
}