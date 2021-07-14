package com.example.collegeconnect.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = Conversation.class)
@ParseClassName("Conversation")
public class Conversation extends ParseObject {

    public Conversation() {}

    public static final String KEY_HIGHSCHOOL_STUDENT = "highSchoolStudent";
    public static final String KEY_COLLEGE_STUDENT = "collegeStudent";

    public User getHighSchoolStudent() {
        return (User) getParseUser(KEY_HIGHSCHOOL_STUDENT);
    }

    public User getCollegeStudent() {
        return (User) getParseUser(KEY_COLLEGE_STUDENT);
    }

    public void setHighSchoolStudent(ParseUser user) {
        put(KEY_HIGHSCHOOL_STUDENT, user);
    }

    public void setCollegeStudent(ParseUser user) {
        put(KEY_COLLEGE_STUDENT, user);
    }
}
