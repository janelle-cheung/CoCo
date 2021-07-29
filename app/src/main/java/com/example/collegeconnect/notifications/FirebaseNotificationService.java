package com.example.collegeconnect.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.StartActivity;
import com.example.collegeconnect.fragments.ConversationsFragment;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {

    public static final String TAG = "FirebaseNotificationService";

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
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "data payload: " + remoteMessage.getData());
            // showNotification(remoteMessage.getData.get("message"));
        }
        super.onMessageReceived(remoteMessage);
    }

    public void showNotification(Message message) {
        Intent intent = new Intent(this, ConversationsFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        User sender = message.getSender();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(sender.getUsername())
            .setContentText(message.getBody())
    //        .setLargeIcon(bitmap)
            .setPriority(Notification.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}
