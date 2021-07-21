package com.example.collegeconnect;

import android.app.Application;

import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.google.android.libraries.places.api.Places;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Conversation.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("cTxaFE96P54cuwItOi4DNBHWXVSZkfS30pmIEhdE")
                .clientKey("U9JvyI3nvaHAjxMgGRP8FXLfRjx4UAOt2UdXAQfs")
                .server("https://collegeconnect.b4a.io")
                .build()
        );

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
    }
}
