package com.example.collegeconnect;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.collegeconnect.models.College;

import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;

public class CollegeAIClient {

    public static final String TAG = "CollegeAIClient";
    private static final String REST_URL = "https://api.collegeai.com/v1/api/";
    private static final String REST_API_KEY = "free_1440b86cce92aeb0e7d79ccffe";
    private static final String REST_COLLEGE_INFO_ENDPOINT = "college/info?";
    private static final String REST_AUTOCOMPLETE_ENDPOINT = "autocomplete/colleges";
    private static final String REST_COLLEGE_LIST_ENDPOINT = "college-list";

    public CollegeAIClient() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getCollegeDetails(String collegeUnitId, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        List<String> infoIds= Arrays.asList(College.KEY_CAMPUS_IMAGE, College.KEY_NAME,
                                            College.KEY_CITY, College.KEY_STATE_ABBR,
                                            College.KEY_ACCEPTANCE_RATE, College.KEY_UNDERGRAD_SIZE,
                                            College.KEY_WEBSITE, College.KEY_SHORT_DESCRIPTION,
                                            College.KEY_SAT25, College.KEY_SAT75,
                                            College.KEY_AVG_NET_PRICE);
        String infoIdsCommaSeparated = String.join(",", infoIds);
        params.put("api_key", REST_API_KEY);
        params.put("college_unit_ids", collegeUnitId);
        params.put("info_ids", infoIdsCommaSeparated);
        client.get(REST_URL + REST_COLLEGE_INFO_ENDPOINT, params, handler);
    }

    public static void getAutoComplete(String collegeInput, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", REST_API_KEY);
        params.put("query", collegeInput);
        params.put("limit", 5);
        client.get(REST_URL + REST_AUTOCOMPLETE_ENDPOINT, params, handler);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getCollegeSuggestionsByCategory(String category, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        List<String> infoIds= Arrays.asList(College.KEY_CAMPUS_IMAGE, College.KEY_NAME,
                                            College.KEY_CITY, College.KEY_STATE_ABBR,
                                            College.KEY_ACCEPTANCE_RATE, College.KEY_UNDERGRAD_SIZE,
                                            College.KEY_WEBSITE, College.KEY_SHORT_DESCRIPTION,
                                            College.KEY_SAT25, College.KEY_SAT75,
                                            College.KEY_AVG_NET_PRICE);
        String infoIdsCommaSeparated = String.join(",", infoIds);
        params.put("api_key", REST_API_KEY);
        params.put("sort_order", category);
        params.put("info_ids", infoIdsCommaSeparated);
        client.get(REST_URL + REST_COLLEGE_LIST_ENDPOINT, params, handler);
    }
}
