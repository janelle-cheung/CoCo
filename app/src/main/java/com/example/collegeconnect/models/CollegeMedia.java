package com.example.collegeconnect.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("CollegeMedia")
public class CollegeMedia extends ParseObject {

    public static final String KEY_FILE = "file";
    public static final String KEY_USER = "user";
    public static final String KEY_ALBUM_NAME = "albumName";
    public static final String KEY_COLLEGE_UNIT_ID= "collegeUnitId";
    public static final String KEY_CAPTION = "caption";

    public CollegeMedia() {}

    public ParseFile getFile() { return getParseFile(KEY_FILE); }

    public User getUser() { return (User) getParseUser(KEY_USER); }

    public String getAlbumName() { return getString(KEY_ALBUM_NAME); }

    public String getCollegeUnitId() { return getString(KEY_COLLEGE_UNIT_ID); }

    public String getCaption() { return getString(KEY_CAPTION); }

    public void setFile(ParseFile file) { put(KEY_FILE, file); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public void setAlbumName(String albumName) { put(KEY_ALBUM_NAME, albumName); }

    public void setCollegeUnitId(String collegeUnitId) { put(KEY_COLLEGE_UNIT_ID, collegeUnitId); }

    public void setCaption(String caption) { put(KEY_CAPTION, caption); }
}
