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

    public static College fromJSON(JSONObject jsonObject) throws JSONException {
        College college = new College();
        college.name = jsonObject.getString("name");
        college.campusImageUrl = jsonObject.has("campusImage") ?
                jsonObject.getString("campusImage") : null;
        college.city = jsonObject.has("city") ?
                jsonObject.getString("city") : null;
        college.stateAbbr = jsonObject.has("stateAbbr") ?
                jsonObject.getString("stateAbbr") : null;
        college.acceptanceRate = jsonObject.has("acceptanceRate") ?
                jsonObject.getDouble("acceptanceRate") * 100 : -1;
        college.undergradSize = jsonObject.has("undergraduateSize") ?
                jsonObject.getString("undergraduateSize") : null;
        college.website = jsonObject.has("website") ?
                jsonObject.getString("website") : null;
        college.shortDescription = jsonObject.has("shortDescription") ?
                jsonObject.getString("shortDescription") : null;
        college.SAT25 = jsonObject.has("satCompositePercentile25") && !jsonObject.isNull("satCompositePercentile25")?
                jsonObject.getInt("satCompositePercentile25") : -1;
        college.SAT75 = jsonObject.has("satCompositePercentile75") && !jsonObject.isNull("satCompositePercentile75")?
                jsonObject.getInt("satCompositePercentile75") : -1;
        college.averageNetPrice = jsonObject.has("avgNetPrice") ?
                jsonObject.getInt("avgNetPrice") : -1;
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
