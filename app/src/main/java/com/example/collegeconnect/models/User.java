package com.example.collegeconnect.models;

import android.util.Log;
import android.widget.Toast;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

@Parcel(analyze = User.class)
@ParseClassName("_User")
public class User extends ParseUser {
    public User() {}

    public static final String KEY_TYPE = "type";
    public static final String KEY_COLLEGE = "college";
    public static final String KEY_GRADE = "grade";
    public static final String KEY_FROM = "from";
    public static final String KEY_PROFILEIMAGE = "profileImage";
    public static final String KEY_ACADEMICS = "academicInterests";
    public static final String KEY_EXTRACURRICULARS = "extracurricularInterests";
    public static final String KEY_HIGHSCHOOL = "highSchool";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_COLLEGE_ID = "collegeUnitId";
    public static final String KEY_FCM_TOKEN = "FCMToken";

    public boolean isInHighSchool() {
        return getString(KEY_TYPE).equals("high school");
    }

    public String getCollege() { return getString(KEY_COLLEGE); }

    public String getGrade() { return getString(KEY_GRADE); }

    public String getFrom() { return getString(KEY_FROM); }

    public boolean hasProfileImage() { return getParseFile(KEY_PROFILEIMAGE) != null; }

    // Must use hasProfileImage() to check if null first
    public String getProfileImageUrl() { return getParseFile(KEY_PROFILEIMAGE).getUrl(); }

    public String getAcademics() { return getString(KEY_ACADEMICS); }

    public String getExtracurriculars() { return getString(KEY_EXTRACURRICULARS); }

    public String getHighSchool() { return getString(KEY_HIGHSCHOOL); }

    public String getEmail() { return getString(KEY_EMAIL); }

    public String getCollegeUnitId() { return getString(KEY_COLLEGE_ID); }

    public boolean hasFCMToken() {
        String FCMToken = getString(KEY_FCM_TOKEN);
        return FCMToken != null && !FCMToken.equals("");
    }

    public String getFCMToken() { return getString(KEY_FCM_TOKEN); }

    public void setType(String type) { put(KEY_TYPE, type); }

    public void setCollege(String college) { put(KEY_COLLEGE, college); }

    public void setGrade(String grade) { put(KEY_GRADE, grade); }

    public void setFrom(String from) { put(KEY_FROM, from); }

    public void setProfileImage(ParseFile file, SaveCallback callback) {
        put(KEY_PROFILEIMAGE, file);
        saveInBackground(callback);
    }

    public void setAcademics(String academics) { put(KEY_ACADEMICS, academics); }

    public void setExtracurriculars(String extracurriculars) { put(KEY_EXTRACURRICULARS, extracurriculars); }

    public void setEmail(String email) { put(KEY_EMAIL, email); }

    public void setHighSchool(String highSchool) { put(KEY_HIGHSCHOOL, highSchool); }

    public void setCollegeUnitId(String collegeId) { put(KEY_COLLEGE_ID, collegeId); }

    public void setFCMToken(String FCMToken) { put(KEY_FCM_TOKEN, FCMToken); }
}
