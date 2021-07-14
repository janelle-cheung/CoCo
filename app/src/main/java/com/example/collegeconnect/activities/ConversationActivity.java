package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityConversationBinding;
import com.example.collegeconnect.databinding.FragmentConversationsBinding;
import com.example.collegeconnect.ui.conversations.ConversationsFragment;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ConversationActivity extends AppCompatActivity {

    private ActivityConversationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ParseUser otherStudent = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra(ConversationsFragment.KEY_OTHER_STUDENT));
        Toast.makeText(this, otherStudent.getUsername(), Toast.LENGTH_SHORT).show();
        binding.test.setText(otherStudent.getUsername());
    }
}