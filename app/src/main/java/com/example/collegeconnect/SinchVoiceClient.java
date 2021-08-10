package com.example.collegeconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.collegeconnect.activities.CallActivity;
import com.example.collegeconnect.activities.ConversationActivity;
import com.example.collegeconnect.activities.MainActivity;
import com.example.collegeconnect.firebase.FirebaseNotificationService;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.ManagedPush;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.UserController;
import com.sinch.android.rtc.UserRegistrationCallback;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SinchVoiceClient {

    public static final String TAG = "SinchVoiceClient";
    private static final String APPLICATION_KEY = "f383db41-f7a5-444f-970c-0bada0d11e75";
    private static final String APPLICATION_SECRET = "WqTcse+Ej0OP9Z98V7A46g==";
    private static final String ENVIRONMENT_HOST = "clientapi.sinch.com";
    private static UserController userController;
    private static SinchClient sinchClient;
    private static Call call;
    private static CallActivity callActivity;
    private static boolean callActivityStarted = false;

    public static void instantiateClient(Context context, String userId) {
//        userController = Sinch.getUserControllerBuilder()
//                .context(context)
//                .userId(userId)
//                .applicationKey(APPLICATION_KEY)
//                .environmentHost(ENVIRONMENT_HOST)
//                .build();
//
//        userController.registerUser(new UserRegistrationCallback() {
//            @Override
//            public void onCredentialsRequired(ClientRegistration clientRegistration) {}
//
//            @Override
//            public void onUserRegistered() {
//                Log.i(TAG, "User registered");
//            }
//
//            @Override
//            public void onUserRegistrationFailed(SinchError sinchError) {
//                Log.e(TAG, "User registration failed " + sinchError.getCode() + " " + sinchError.getMessage());
//            }
//
//        }, new PushTokenRegistrationCallback() {
//            @Override
//            public void tokenRegistered() {
//                Log.i(TAG, "Token registered");
//            }
//
//            @Override
//            public void tokenRegistrationFailed(SinchError sinchError) {
//                Log.e(TAG, "Token registration failed " + sinchError.getCode() + " " + sinchError.getMessage());
//            }
//        });

        sinchClient = Sinch.getSinchClientBuilder()
                .context(context)
                .userId(userId)
                .applicationKey(APPLICATION_KEY)
                .applicationSecret(APPLICATION_SECRET)
                .environmentHost(ENVIRONMENT_HOST)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.setSupportManagedPush(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call incomingCall) {
                Log.i("MainActivity", "SinchVoiceClient: on incoming call");
                if (call == null) {
                    call = incomingCall;
                    call.addCallListener(new SinchCallListener());
                }
            }
        });
        sinchClient.start();
    }

    public static void startCall(Context context, String otherUserId, User currUser) {
        if (call == null) {
            Log.i("MainActivity", "start call");
            Map<String,String> headers = new HashMap<>();
            headers.put(Message.KEY_SENDER, currUser.getUsername());
            headers.put(Message.KEY_BODY, context.getString(R.string.incoming_call_body));
            if (currUser.hasProfileImage()) {
                headers.put(User.KEY_PROFILEIMAGE, currUser.getProfileImageUrl());
            }
            call = sinchClient.getCallClient().callUser(otherUserId, headers);
            call.addCallListener(new SinchCallListener());
        }
    }

    public static class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {}

        @Override
        public void onCallEstablished (Call establishedCall) {
            callActivity.onCallEstablished();
        }

        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            if (callActivityStarted) {
                callActivity.onCallEnded();
                callActivity = null;
            } else {
                FirebaseNotificationService.cancelSinchCallNotification();
            }
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {}
    }

    public static void hangupCall() {
        call.hangup();
        call = null;
    }

    public static void answerCall() {
        call.answer();
    }

    public static void terminateClient() {
        sinchClient.stopListeningOnActiveConnection();
        // userController.unregisterPushToken();
        // sinchClient.unregisterPushNotificationData();
        sinchClient.terminateGracefully();
    }

    public static void registerCallActivity(CallActivity activity) {
        callActivity = activity;
        callActivityStarted = true;
    }
}
