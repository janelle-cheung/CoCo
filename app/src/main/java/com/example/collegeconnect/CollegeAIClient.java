package com.example.collegeconnect;


import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;

import okhttp3.Headers;

public class CollegeAIClient {

    public static final String TAG = "CollegeAIClient";
    private static final String REST_URL = "https://api.collegeai.com/v1/api/";
    private static final String REST_API_KEY = "free_1440b86cce92aeb0e7d79ccffe";
    private static final String REST_COLLEGE_INFO_ENDPOINT = "college/info?";
    private static final String REST_AUTOCOMPLETE_ENDPOINT = "autocomplete/colleges";

    public CollegeAIClient() {}

    public static void getCollegeDetails(String collegeUnitId, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String infoIds = "campusImage,name,city,stateAbbr,majorGraduates,acceptanceRate,undergraduateSize,website,shortDescription";
        params.put("api_key", REST_API_KEY);
        params.put("college_unit_ids", collegeUnitId);
        params.put("info_ids", infoIds);
        client.get(REST_URL + REST_COLLEGE_INFO_ENDPOINT, params, handler);
    }

    public static void getAutoComplete(String collegeInput, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", REST_API_KEY);
        params.put("query", collegeInput);
        client.get(REST_URL + REST_AUTOCOMPLETE_ENDPOINT, params, handler);
    }
}