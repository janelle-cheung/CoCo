package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.SinchVoiceClient;
import com.example.collegeconnect.databinding.ActivityCallBinding;
import com.example.collegeconnect.fragments.ConversationsFragment;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.sinch.android.rtc.Sinch;

import org.parceler.Parcels;

public class CallActivity extends AppCompatActivity {

    private ActivityCallBinding binding;
    boolean outgoingCall;
    boolean callAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SinchVoiceClient.registerCallActivity(this);

        Intent i = getIntent();
        outgoingCall = i.hasExtra(ConversationActivity.KEY_OUTGOING_CALL);

        // Add click listener to Accept button if it's an incoming call
        if (outgoingCall) {
            displayOutgoingInfo(i);
        } else {
            displayIncomingInfo(i);
            binding.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SinchVoiceClient.answerCall();
                }
            });
        }

        binding.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SinchVoiceClient.hangupCall();
            }
        });
    }

    private void displayIncomingInfo(Intent i) {
        binding.tvCallStatus.setText(getString(R.string.incoming_call_from));
        binding.tvCallerName.setText(i.getStringExtra(Message.KEY_SENDER));

        if (i.hasExtra(User.KEY_PROFILEIMAGE)) {
            String userProfileImageUrl = i.getStringExtra(User.KEY_PROFILEIMAGE);
            Glide.with(this)
                    .load(userProfileImageUrl)
                    .circleCrop()
                    .into(binding.ivCallerProfileImage);
        } else {
            // Display placeholder image if user has no profile image
            Glide.with(this)
                    .load(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivCallerProfileImage);
        }
    }

    private void displayOutgoingInfo(Intent i) {
        binding.tvCallStatus.setText(getString(R.string.outgoing_calling));
        binding.tvCallerName.setText(i.getStringExtra(ConversationActivity.KEY_OUTGOING_USERNAME));
        binding.btnAccept.setVisibility(View.GONE);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.btnDecline.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        binding.btnDecline.setLayoutParams(lp);

        if (i.hasExtra(ConversationActivity.KEY_OUTGOING_PROFILE_IMAGE)) {
            Glide.with(this)
                    .load(i.getStringExtra(ConversationActivity.KEY_OUTGOING_PROFILE_IMAGE))
                    .circleCrop()
                    .into(binding.ivCallerProfileImage);
        } else {
            // Display placeholder image if user has no profile image
            Glide.with(this)
                    .load(R.mipmap.profile_placeholder_foreground)
                    .circleCrop()
                    .into(binding.ivCallerProfileImage);
        }
    }

    public void onCallEstablished() {
        if (!outgoingCall) {
            binding.btnAccept.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.btnDecline.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            binding.btnDecline.setLayoutParams(lp);
        }

        Toast.makeText(CallActivity.this, "You are now connected", Toast.LENGTH_SHORT).show();
        binding.tvCallStatus.setText("");
        callAnswered = true;
    }

    public void onCallEnded() {
        if (!outgoingCall && !callAnswered) {
            Toast.makeText(this, "Call declined", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CallActivity.this, "Call ended", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}