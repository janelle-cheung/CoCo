package com.example.collegeconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.collegeconnect.activities.ConversationActivity;
import com.example.collegeconnect.activities.MainActivity;
import com.example.collegeconnect.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class SinchVoiceClient {

    public static final String TAG = "SinchVoiceClient";
    private static SinchClient sinchClient;
    private static Call call;
    private static AlertDialog incomingCallAlertDialog;
    private static AlertDialog outgoingCallAlertDialog;
    private static AlertDialog ongoingCallAlertDialog;
    private static String otherUserName;

    public static void instantiateClient(Context context, String userId) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(context)
                .userId(userId)
                .applicationKey("f383db41-f7a5-444f-970c-0bada0d11e75")
                .applicationSecret("WqTcse+Ej0OP9Z98V7A46g==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call incomingCall) {
                if (call == null && incomingCallAlertDialog == null) {
                    call = incomingCall;
                    call.addCallListener(new SinchCallListener(context));
                    getIncomingUserName(context, incomingCall.getRemoteUserId());
                }
            }
        });
        sinchClient.start();
    }

    private static void getIncomingUserName(Context context, String remoteUserId) {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.KEY_OBJECT_ID, remoteUserId);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (e == null) {
                    otherUserName = users.get(0).getUsername();
                    showIncomingCallAlertDialog(context);
                } else {
                    Log.e(TAG, "Error querying incoming user ", e);
                }
            }
        });
    }

    private static void showIncomingCallAlertDialog(Context context) {
        incomingCallAlertDialog = new AlertDialog.Builder(context).create();
        incomingCallAlertDialog.setTitle("Incoming call");
        incomingCallAlertDialog.setMessage("Call from " + otherUserName);
        incomingCallAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                incomingCallAlertDialog = null;
                call.hangup();
            }
        });
        incomingCallAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Answer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                incomingCallAlertDialog = null;
                call.answer();
            }
        });
        incomingCallAlertDialog.show();
    }

    public static void startCall(Context context, String otherUserId, String outgoingUserName) {
        if (call == null && incomingCallAlertDialog == null) {
            call = sinchClient.getCallClient().callUser(otherUserId);
            call.addCallListener(new SinchCallListener(context));
            otherUserName = outgoingUserName;
            showOutgoingCallAlertDialog(context);
        }
    }

    private static void showOutgoingCallAlertDialog(Context context) {
        outgoingCallAlertDialog = new AlertDialog.Builder(context).create();
        outgoingCallAlertDialog.setTitle("Outgoing call");
        outgoingCallAlertDialog.setMessage("Calling " + otherUserName + "...");
        outgoingCallAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "End call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                outgoingCallAlertDialog = null;
                call.hangup();
                call = null;
            }
        });
        outgoingCallAlertDialog.show();
    }

    public static class SinchCallListener implements CallListener {

        Context context;

        public SinchCallListener(Context context) {
            this.context = context;
        }

        @Override
        public void onCallProgressing(Call call) {}

        @Override
        public void onCallEstablished(Call establishedCall) {
            Toast.makeText(context, "You are now connected!", Toast.LENGTH_SHORT).show();

            // Dismiss the outgoing dialog for the caller when receiver picks up
            if (outgoingCallAlertDialog != null) {
                outgoingCallAlertDialog.dismiss();
                outgoingCallAlertDialog = null;
            }

            showEstablishedCallAlertDialog(context, call);
        }

        @Override
        public void onCallEnded(Call endedCall) {
            Toast.makeText(context, "Call ended", Toast.LENGTH_SHORT).show();
            call = null;

            // Dismiss outgoing dialog for the caller when receiver rejects the call
            if (outgoingCallAlertDialog != null) {
                outgoingCallAlertDialog.dismiss();
                outgoingCallAlertDialog = null;
            }

            // Dismiss ongoing dialog for the caller or receiver who gets hung up on
            if (ongoingCallAlertDialog != null) {
                ongoingCallAlertDialog.dismiss();
                ongoingCallAlertDialog = null;
            }

            // Dismiss incoming dialog if caller ends call before receiver picks up
            if (incomingCallAlertDialog != null) {
                incomingCallAlertDialog.dismiss();
                incomingCallAlertDialog = null;
            }
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {}
    }

    private static void showEstablishedCallAlertDialog(Context context, Call establishedCall) {
        ongoingCallAlertDialog = new AlertDialog.Builder(context).create();
        ongoingCallAlertDialog.setTitle("Ongoing call");
        ongoingCallAlertDialog.setMessage("Calling " + otherUserName);
        ongoingCallAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "End call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ongoingCallAlertDialog = null;
                call.hangup();
                call = null;
            }
        });
        ongoingCallAlertDialog.show();
    }
}
