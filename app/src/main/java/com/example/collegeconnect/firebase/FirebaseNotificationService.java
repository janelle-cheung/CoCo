package com.example.collegeconnect.firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.collegeconnect.R;
import com.example.collegeconnect.SinchVoiceClient;
import com.example.collegeconnect.activities.CallActivity;
import com.example.collegeconnect.activities.ConversationActivity;
import com.example.collegeconnect.fragments.ConversationsFragment;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {

    public static final String TAG = "FirebaseNotificationService";
    public static NotificationManagerCompat sinchNotificationManager;
    public static int sinchCallNotificationId;

    @Override
    // Automatically called when a new token is generated for this app instance / device
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("Firebase Token", s).apply();
    }

    public static boolean newTokenGenerated(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("Firebase Token", "").equals("");
    }

    public static String getNewToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("Firebase Token", "");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Log.i("MainActivity", "onMessageReceived");
        if (SinchHelpers.isSinchPushPayload(remoteMessage.getData())) {
            // This is a Sinch notification for an incoming call. onIncomingCall() is automatically called
            NotificationResult result = SinchHelpers.queryPushNotificationPayload(getApplicationContext(), remoteMessage.getData());
            if (result.isCall()) {
                CallNotificationResult callResult = result.getCallResult();
                Map<String, String> map = callResult.getHeaders();
                // First get the user profile bitmap in background, then show notification
                getSenderProfileImageBitmap(map, true);
            }
        } else {
            // This is a chat notification that we created
            super.onMessageReceived(remoteMessage);
            // First get the user profile bitmap in background, then get Parse conversation, then show notification
            getSenderProfileImageBitmap(remoteMessage.getData(), false);
        }
    }

    // To set notification large icon, the image must be a Bitmap
    @SuppressLint("LongLogTag")
    private void getSenderProfileImageBitmap(Map<String, String> map, boolean isSinch) {
        if (map.containsKey(User.KEY_PROFILEIMAGE)) {
            Glide.with(this)
                    .asBitmap()
                    .load(map.get(User.KEY_PROFILEIMAGE))
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap bitmap, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            if (isSinch) {
                                showSinchCallNotification(map, bitmap, true);
                            } else {
                                getParseConversation(map, bitmap);
                            }
                        }
                    });
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.profile_placeholder_foreground);
            if (isSinch) {
                showSinchCallNotification(map, bitmap, false);
            } else {
                getParseConversation(map, bitmap);
            }
        }
    }

    @SuppressLint("LongLogTag")
    private void getParseConversation(Map<String, String> map, Bitmap bitmap) {
        ParseQuery query = ParseQuery.getQuery(Conversation.class);
        query.whereEqualTo(Conversation.KEY_OBJECT_ID, map.get(Message.KEY_CONVERSATION));
        query.include(Conversation.KEY_COLLEGE_STUDENT);
        query.include(Conversation.KEY_HIGHSCHOOL_STUDENT);
        query.findInBackground(new FindCallback<Conversation>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying conversation from notification ", e);
                    return;
                }
                showNotification(map, bitmap, objects.get(0));
            }
        });
    }

    private void showNotification(Map<String, String> map, Bitmap bitmap, Conversation conversation) {
        Intent i = new Intent(this, ConversationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(ConversationsFragment.KEY_CONVERSATION_1, Parcels.wrap(conversation));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(bitmap)
                .setContentTitle(map.get(Message.KEY_SENDER))
                .setContentText(map.get(Message.KEY_BODY))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private void showSinchCallNotification(Map<String, String> map, Bitmap bitmap, boolean hasProfileImage) {
        Intent pendingI = new Intent(this, CallActivity.class);
        pendingI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingI.putExtra(Message.KEY_SENDER, map.get(Message.KEY_SENDER));
        if (hasProfileImage) {
            pendingI.putExtra(User.KEY_PROFILEIMAGE, map.get(User.KEY_PROFILEIMAGE));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, pendingI, PendingIntent.FLAG_UPDATE_CURRENT);

        // For phones that allow users to dismiss notifications, not tested right now
//        Intent deleteI = new Intent(this, MyBroadcastReceiver.class);
//        PendingIntent deleteIntent = PendingIntent.getBroadcast(this, 0, deleteI, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(bitmap)
                .setContentTitle(map.get(Message.KEY_SENDER))
                .setContentText(map.get(Message.KEY_BODY))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                // .setDeleteIntent(deleteIntent)
                .setAutoCancel(true);

        sinchNotificationManager = NotificationManagerCompat.from(this);
        sinchCallNotificationId = new Random().nextInt();
        sinchNotificationManager.notify(sinchCallNotificationId, builder.build());
    }

    // For phones that allow users to dismiss notifications, not tested right now
    // If receiver dismisses the call notification, end the call for the caller
    public static class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Firebase", "hanging up call after notification dismissed");
            SinchVoiceClient.hangupCall();
        }

    }

    public static void cancelSinchCallNotification() {
        sinchNotificationManager.cancel(sinchCallNotificationId);
    }
}
