package com.example.collegeconnect.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = User.class)
@ParseClassName("_User")
public class User extends ParseUser {
    public User() {}

    public static final String KEY_TYPE = "type";
    public static final String KEY_SCHOOL = "school";
    public static final String KEY_GRADE = "grade";
    public static final String KEY_FROM = "from";
    public static final String KEY_PROFILEIMAGE = "profileImage";
    public static final String KEY_ACADEMICS = "academicInterests";
    public static final String KEY_EXTRACURRICULARS = "extracurricularInterests";
    public static final String KEY_HIGHSCHOOL = "highSchool";
    public static final String KEY_EMAIL = "email";

    public boolean isInHighSchool() {
        return getString(KEY_TYPE).equals("high school");
    }

    public String getSchool() { return getString(KEY_SCHOOL); }

    public String getGrade() { return getString(KEY_GRADE); }

    public String getFrom() { return getString(KEY_FROM); }

    public boolean hasProfileImage() { return getParseFile(KEY_PROFILEIMAGE) != null; }

    // Must use hasProfileImage() to check if null first
    public String getProfileImageUrl() { return getParseFile(KEY_PROFILEIMAGE).getUrl(); }

    public String getAcademics() { return getString(KEY_ACADEMICS); }

    public String getExtracurriculars() { return getString(KEY_EXTRACURRICULARS); }

    public String getHighSchool() { return getString(KEY_HIGHSCHOOL); }

    public String getEmail() { return getString(KEY_EMAIL); }

    public void setType(String type) { put(KEY_TYPE, type); }

    public void setSchool(String school) { put(KEY_SCHOOL, school); }

    public void setGrade(String grade) { put(KEY_GRADE, grade); }

    public void setFrom(String from) { put(KEY_FROM, from); }

    public void setAcademics(String academics) { put(KEY_ACADEMICS, academics); }

    public void setExtracurriculars(String extracurriculars) { put(KEY_EXTRACURRICULARS, extracurriculars); }

    public void setEmail(String email) { put(KEY_EMAIL, email); }

    public void setHighSchool(String highSchool) { put(KEY_HIGHSCHOOL, highSchool); }
}
