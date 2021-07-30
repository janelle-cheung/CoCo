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
import cz.msebera.android.httpclient.entity.StringEntity;
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

    public static void postNotificationVolley(Context context, JSONObject notification) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REST_URL, notification, response -> {
            Log.i(TAG, "post notification " + response);
        }, error -> {
            Log.i(TAG, "post notification error " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization:", "key=" + SERVER_KEY);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public static void postNotificationAsyncHTTPClient(JSONObject notification) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("Content-Type", "application/json");
        params.put("Authorization:", "key=" + SERVER_KEY);
        client.post(REST_URL, params, String.valueOf(notification), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Success posting notification to FCM");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Error posting notification to FCM " + statusCode + response);
                Log.i(TAG, String.valueOf(throwable));
            }
        });
    }
}
