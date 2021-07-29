package com.example.collegeconnect;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.collegeconnect.models.CollegeMedia;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.Save;
import com.example.collegeconnect.models.User;
import com.google.android.libraries.places.api.Places;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Conversation.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(CollegeMedia.class);
        ParseObject.registerSubclass(Save.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("cTxaFE96P54cuwItOi4DNBHWXVSZkfS30pmIEhdE")
                .clientKey("U9JvyI3nvaHAjxMgGRP8FXLfRjx4UAOt2UdXAQfs")
                .server("https://collegeconnect.b4a.io")
                .build()
        );

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.channel_id);
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
