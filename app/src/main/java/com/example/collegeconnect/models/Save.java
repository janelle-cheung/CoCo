package com.example.collegeconnect.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Save")
public class Save extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COLLEGE_NAME = "collegeName";
    public static final String KEY_COLLEGE_UNIT_ID = "collegeUnitId";

    public Save() {}

    public void setUser(User user) { put(KEY_USER,user); }

    public void setCollegeName(String collegeName) { put(KEY_COLLEGE_NAME, collegeName); }

    public void setCollegeUnitId(String collegeUnitId) { put(KEY_COLLEGE_UNIT_ID, collegeUnitId); }

    public User getUser() {
        return (User) getParseUser(KEY_USER);
    }

    public String getCollegeName() {
        return getString(KEY_COLLEGE_NAME);
    }

    public String getCollegeUnitId() {
        return getString(KEY_COLLEGE_UNIT_ID);
    }
}
