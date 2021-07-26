package com.example.collegeconnect.models;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class College {

    public static final String TAG = "College";
    public static final String KEY_NAME = "name";
    public static final String KEY_CAMPUS_IMAGE = "campusImage";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE_ABBR = "stateAbbr";
    public static final String KEY_ACCEPTANCE_RATE = "acceptanceRate";
    public static final String KEY_UNDERGRAD_SIZE = "undergraduateSize";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    public static final String KEY_AVG_NET_PRICE = "avgNetPrice";
    public static final String KEY_SAT25 = "satCompositePercentile25";
    public static final String KEY_SAT75 = "satCompositePercentile75";
    private String name;
    private String campusImageUrl;
    private String city;
    private String stateAbbr;
    private double acceptanceRate;
    private String undergradSize;
    private String website;
    private String shortDescription;
    private int averageNetPrice;
    private int SAT25;
    private int SAT75;

    public College() {}

    public static List<College> fromJSONArray(JSONArray jsonArray) throws JSONException {
        List<College> colleges = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            colleges.add(fromJSON(jsonArray.getJSONObject(i)));
        }
        return colleges;
    }

    // For each variable, check if the object has a non-null value for it
    public static College fromJSON(JSONObject jsonObject) throws JSONException {
        College college = new College();
        college.name = jsonObject.getString("name");
        college.campusImageUrl = jsonObject.has(KEY_CAMPUS_IMAGE) && !jsonObject.isNull(KEY_CAMPUS_IMAGE) ?
                jsonObject.getString(KEY_CAMPUS_IMAGE) : null;
        college.city = jsonObject.has(KEY_CITY) && !jsonObject.isNull(KEY_CITY) ?
                jsonObject.getString(KEY_CITY) : null;
        college.stateAbbr = jsonObject.has(KEY_STATE_ABBR) && !jsonObject.isNull(KEY_STATE_ABBR) ?
                jsonObject.getString(KEY_STATE_ABBR) : null;
        college.acceptanceRate = jsonObject.has(KEY_ACCEPTANCE_RATE) && !jsonObject.isNull(KEY_ACCEPTANCE_RATE) ?
                jsonObject.getDouble(KEY_ACCEPTANCE_RATE) * 100 : -1;
        college.undergradSize = jsonObject.has(KEY_UNDERGRAD_SIZE) && !jsonObject.isNull(KEY_UNDERGRAD_SIZE) ?
                jsonObject.getString(KEY_UNDERGRAD_SIZE) : null;
        college.website = jsonObject.has(KEY_WEBSITE) && !jsonObject.isNull(KEY_WEBSITE) ?
                jsonObject.getString(KEY_WEBSITE) : null;
        college.shortDescription = jsonObject.has(KEY_SHORT_DESCRIPTION) && !jsonObject.isNull(KEY_SHORT_DESCRIPTION)?
                jsonObject.getString(KEY_SHORT_DESCRIPTION) : null;
        college.SAT25 = jsonObject.has(KEY_SAT25) && !jsonObject.isNull(KEY_SAT25)?
                jsonObject.getInt(KEY_SAT25) : -1;
        college.SAT75 = jsonObject.has(KEY_SAT75) && !jsonObject.isNull(KEY_SAT75)?
                jsonObject.getInt(KEY_SAT75) : -1;
        college.averageNetPrice = jsonObject.has(KEY_AVG_NET_PRICE) && !jsonObject.isNull(KEY_AVG_NET_PRICE) ?
                jsonObject.getInt(KEY_AVG_NET_PRICE) : -1;
        return college;
    }

    public String getName() {
        return name;
    }
    public boolean hasCampusImage() { return campusImageUrl != null; }
    public String getCampusImageUrl() { return campusImageUrl; }
    public String getCityState() {
        if (city == null || stateAbbr == null) {
            return null;
        } else {
            return city + ", " + stateAbbr;
        }
    }
    public String getCity() { return city; }
    public String getStateAbbr() { return stateAbbr; }
    public double getAcceptanceRate() { return acceptanceRate; }
    public String getUndergradSize() { return undergradSize; }
    public String getWebsite() { return website; }
    public String getShortDescription() { return shortDescription; }
    public int getAverageNetPrice() { return averageNetPrice; }
    public String getSATRange() {
        if (SAT25 == -1 || SAT75 == -1) {
            return null;
        } else {
            return SAT25 + " - " + SAT75;
        }
    }
    public int getSAT25() { return SAT25; }
    public int getSAT75() { return SAT75; }
}
