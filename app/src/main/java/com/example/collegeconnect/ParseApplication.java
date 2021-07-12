package com.example.collegeconnect;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("cTxaFE96P54cuwItOi4DNBHWXVSZkfS30pmIEhdE")
                .clientKey("U9JvyI3nvaHAjxMgGRP8FXLfRjx4UAOt2UdXAQfs")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
