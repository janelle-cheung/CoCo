package com.example.collegeconnect.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Save")
public class Save extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COLLEGE_NAME = "collegeName";
    public static final String KEY_COLLEGE_UNIT_ID = "collegeUnitId";
    public static final String KEY_COLUMN = "column";
    public static final String COLUMN_SAVED = "Saved";
    public static final String COLUMN_SAFETY = "Safety";
    public static final String COLUMN_MATCH = "Match";
    public static final String COLUMN_REACH = "Reach";
    public static final String[] COLUMNS_ARRAY = {COLUMN_SAVED, COLUMN_SAFETY, COLUMN_MATCH, COLUMN_REACH};

    public Save() {}

    public void setUser(User user) { put(KEY_USER,user); }

    public void setCollegeName(String collegeName) { put(KEY_COLLEGE_NAME, collegeName); }

    public void setCollegeUnitId(String collegeUnitId) { put(KEY_COLLEGE_UNIT_ID, collegeUnitId); }

    public void setColumn(String column) { put(KEY_COLUMN, column); }

    public User getUser() {
        return (User) getParseUser(KEY_USER);
    }

    public String getCollegeName() {
        return getString(KEY_COLLEGE_NAME);
    }

    public String getCollegeUnitId() {
        return getString(KEY_COLLEGE_UNIT_ID);
    }

    public String getColumn() { return getString(KEY_COLUMN); }
}
