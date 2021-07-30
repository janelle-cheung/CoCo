package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.R;
import com.example.collegeconnect.adapters.ConversationAdapter;
import com.example.collegeconnect.databinding.ActivityConversationBinding;
import com.example.collegeconnect.firebase.FirebaseClient;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.fragments.ConversationsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Headers;

public class ConversationActivity extends AppCompatActivity {

    public static final int NUM_MESSAGES_TO_QUERY = 10;
    public static final String TAG = "ConversationActivity";
    public static final String KEY_CONVERSATION_2 = "conversation2";
    public static final int LOCATION_REQUEST_CODE = 40;
    private ConversationAdapter adapter;
    private ActivityConversationBinding binding;
    private List<Message> messages;
    private Conversation conversation;
    private User user;
    private User otherUser;
    private boolean firstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversation = Parcels.unwrap(getIntent().getParcelableExtra(ConversationsFragment.KEY_CONVERSATION_1));
        user = (User) ParseUser.getCurrentUser();
        if (user.isInHighSchool()) {
            otherUser = conversation.getCollegeStudent();
        } else {
            otherUser = conversation.getHighSchoolStudent();
        }

        messages = new ArrayList<>();
        adapter = new ConversationAdapter(this, user, otherUser, messages);
        binding.rvMessages.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        binding.rvMessages.setLayoutManager(linearLayoutManager);
        firstLoad = true;

        queryMessages();
        configureLiveMessageRefresh();

        binding.ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private void sendMessage() {
        String body = binding.etMessage.getText().toString();
        if (body.isEmpty()) { return; }
        Message message = new Message();
        message.setSender(user);
        message.setBody(body);
        message.setConversation(conversation);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(ConversationActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to save message", e);
                    return;
                }

                if (otherUser.hasFCMToken()) {
                    Log.i(TAG, "other user has active token. creating notification");
                    createAndSendJSONNotification(message);
                } else {
                    Log.i(TAG, "other user doesn't have active token. not creating notification");
                }
            }
        });
        binding.etMessage.setText(null);
    }

    private void createAndSendJSONNotification(Message message) {
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put(Message.KEY_SENDER, user.getUsername());
            data.put(Message.KEY_BODY, message.getBody());
            if (user.hasProfileImage()) {
                data.put(User.KEY_PROFILEIMAGE, message.getSender().getProfileImageUrl());
            }
            data.put(Message.KEY_CONVERSATION, message.getConversation());

            notification.put("to", otherUser.getFCMToken());
            notification.put("data", data);

            // TODO: Get the client to successfully send request!
//            FirebaseClient.postNotificationAsyncHTTPClient(notification);
//            FirebaseClient.postNotificationVolley(this, notification);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON notification ", e );
        }
    }

    private void configureLiveMessageRefresh() {
        String webSocketUrl = "https://collegeconnect.b4a.io";
        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(webSocketUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        // Listen for CREATE events on the Message class
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
            messages.add(0, object);

            // RecyclerView updates need to be run on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    binding.rvMessages.scrollToPosition(0);
                }
            });
        });
    }

    public void queryMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(NUM_MESSAGES_TO_QUERY);
        query.whereEqualTo(Message.KEY_CONVERSATION, conversation);
        query.include(Message.KEY_CONVERSATION);
        query.include(Message.KEY_SENDER);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> objects, ParseException e) {
                if (e == null) {
                    messages.clear();
                    messages.addAll(objects);
                    adapter.notifyDataSetChanged();

                    // Scroll to the bottom of the list on initial load
                    if (firstLoad) {
                        binding.rvMessages.scrollToPosition(0);
                        firstLoad = false;
                    }
                } else {
                    Log.e(TAG, "Error loading messages " + e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        if (user.isInHighSchool() && !conversation.meetLocationSet()) {
            menu.findItem(R.id.miLocationSharing).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miLocationSharing) {
            if (user.isInHighSchool()) {
                Intent i = new Intent(this, HighSchoolLocationShareActivity.class);
                i.putExtra(KEY_CONVERSATION_2, Parcels.wrap(conversation));
                startActivityForResult(i, LOCATION_REQUEST_CODE);
            } else {
                Intent i = new Intent(this, CollegeLocationShareActivity.class);
                i.putExtra(KEY_CONVERSATION_2, Parcels.wrap(conversation));
                startActivityForResult(i, LOCATION_REQUEST_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == LOCATION_REQUEST_CODE  && resultCode  == RESULT_OK) {
                Conversation newConversation = Parcels.unwrap(data.getParcelableExtra(CollegeLocationShareActivity.KEY_NEW_CONVERSATION));
                this.conversation = newConversation;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error on activity result");
        }
    }
}