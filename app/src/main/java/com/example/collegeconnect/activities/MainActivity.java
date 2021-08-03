package com.example.collegeconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.collegeconnect.R;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.firebase.FirebaseNotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.collegeconnect.databinding.ActivityMainBinding;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    User currUser;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureBottomNavBar();

        currUser = (User) ParseUser.getCurrentUser();

        if (FirebaseNotificationService.newTokenGenerated(this)) {
            token = FirebaseNotificationService.getNewToken(this);
            saveUserFCMToken();
            Log.i(TAG, currUser.getUsername() + " token: " + token);
        } else {
            retrieveToken();
        }
    }

    private void configureBottomNavBar() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_conversations, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void retrieveToken() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "retrieveToken: Fetching FCM token failed");
                        return;
                    }
                    token = task.getResult();
                    saveUserFCMToken();
                    Log.i(TAG, currUser.getUsername() + " token: " + token);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "retrieveToken: Error fetching FCM token ", e);
        }
    }

    public void saveUserFCMToken() {
        currUser.setFCMToken(token);
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error saving token ", e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
        }
        return true;
    }

    private void logout() {
        currUser.setFCMToken("");
        // Remove FCM token in background, only after that call returns do we log out in background
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error removing token " + e);
                } else {
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                returnToStartActivity();
                            } else {
                                Log.e(TAG, "Issue with log-out");
                            }
                        }
                    });
                }
            }
        });
    }

    private void returnToStartActivity() {
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
        finish();
    }
}