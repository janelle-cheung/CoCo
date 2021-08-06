package com.example.collegeconnect.firebase;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.R;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class FirebaseClient {

    public static final String TAG = "FirebaseClient";
    private static final String REST_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY =
            "AAAAw883Zv4:APA91bHTENiUwV43Qkc41kS-K9VzU4hzXN2FvuOMcdgZJqw_P1KoL5EhMulsbAoi9BidpN4ddPadMCqixfmDwa5-SBe7D-qvXWnW5Gd6a_EjxnTkV1fIScNRUmCDTEf2NGQ_nQ7vydUK";

    public FirebaseClient() {}

    public static void createAndSendNotification(Context context, User sender, String FCMToken, String body, String conversationId) {
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put(Message.KEY_SENDER, sender.getUsername());
            data.put(Message.KEY_BODY, body);
            if (sender.hasProfileImage()) {
                data.put(User.KEY_PROFILEIMAGE, sender.getProfileImageUrl());
            }
            data.put(Message.KEY_CONVERSATION, conversationId);

            notification.put("to", FCMToken);
            notification.put("data", data);

            postNotification(context, notification);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON notification ", e );
        }
    }

    public static void postNotification(Context context, JSONObject notification) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REST_URL, notification,
            response -> {
                Log.i("MainActivity", "Successful sent notification");
            },
            error -> {
                Log.i(TAG, "Post notification error " + error);
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=" + SERVER_KEY);
                    return headers;
                }
            };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
        Log.i("MainActivity", notification.toString());
    }
}
