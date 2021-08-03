package com.example.collegeconnect;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.models.College;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import java.util.Arrays;
import java.util.List;

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
    public static void getCollegeSuggestionsByCategoryAndFilters(String category, JSONObject jsonFilters, JsonHttpResponseHandler handler) {
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
        params.put("filters", String.valueOf(jsonFilters));
        Log.i(TAG, String.valueOf(jsonFilters));
        client.get(REST_URL + REST_COLLEGE_LIST_ENDPOINT, params, handler);
    }

    public static JSONObject createFiltersJSONObject(boolean filterPublic, boolean filterPrivate,
                                                     boolean filter4Year, boolean filter2Year,
                                                     boolean filterSmall, boolean filterMedium, boolean filterLarge,
                                                     int maxNetCost, int SAT, int ACT) {
        JSONObject filtersJSON = new JSONObject();
        JSONArray fundingTypeArray = new JSONArray();
        JSONArray degreeLengthArray = new JSONArray();
        JSONArray schoolSizeArray = new JSONArray();
        if (filterPublic) { fundingTypeArray.put("public"); }
        if (filterPrivate) { fundingTypeArray.put("private"); }
        if (filter4Year) { degreeLengthArray.put("4year"); }
        if (filter2Year) { degreeLengthArray.put("2year"); }
        if (filterSmall) { schoolSizeArray.put("small"); }
        if (filterMedium) { schoolSizeArray.put("medium"); }
        if (filterLarge) { schoolSizeArray.put("large"); }

        try {
            filtersJSON.put("fundingType", fundingTypeArray);
            filtersJSON.put("degreeLength", degreeLengthArray);
            filtersJSON.put("schoolSize", schoolSizeArray);
            if (maxNetCost != -1) filtersJSON.put("maxNetCost", maxNetCost);
            if (SAT != -1) filtersJSON.put("satOverall", SAT);
            if (ACT != -1) filtersJSON.put("actComposite", ACT);
            Log.i(TAG, filtersJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return filtersJSON;
    }
}
