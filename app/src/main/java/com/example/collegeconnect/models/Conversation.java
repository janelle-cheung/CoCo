package com.example.collegeconnect.models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

@Parcel(analyze = Conversation.class)
@ParseClassName("Conversation")
public class Conversation extends ParseObject {

    public Conversation() {}

    public static final String KEY_HIGHSCHOOL_STUDENT = "highSchoolStudent";
    public static final String KEY_COLLEGE_STUDENT = "collegeStudent";
    public static final String KEY_MEET_LOCATION = "meetLocation";

    public User getHighSchoolStudent() { return (User) getParseUser(KEY_HIGHSCHOOL_STUDENT); }

    public User getCollegeStudent() {
        return (User) getParseUser(KEY_COLLEGE_STUDENT);
    }

    public boolean meetLocationSet() { return getMeetLocation() != null; }

    public ParseGeoPoint getMeetLocation() { return getParseGeoPoint(KEY_MEET_LOCATION); }

    public void setHighSchoolStudent(ParseUser user) {
        put(KEY_HIGHSCHOOL_STUDENT, user);
    }

    public void setCollegeStudent(ParseUser user) {
        put(KEY_COLLEGE_STUDENT, user);
    }

    public void setMeetLocation(ParseGeoPoint meetLocation, SaveCallback callback) {
        put(KEY_MEET_LOCATION, meetLocation);
        saveInBackground(callback);
    }

    // LatLng for US
    public static LatLng getDefaultMapLocation() {
        return new LatLng(39, -98);
    }
}
