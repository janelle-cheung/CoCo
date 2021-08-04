package com.example.collegeconnect.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityCollegeLocationShareBinding;
import com.example.collegeconnect.firebase.FirebaseClient;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class CollegeLocationShareActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener {

    public static final String TAG = "CollegeLocationShareActivity";
    public static final int AUTOCOMPLETE_REQUEST_CODE = 24;
    public static final String KEY_NEW_CONVERSATION = "new conversation";
    private ActivityCollegeLocationShareBinding binding;
    private GoogleMap map;
    private Conversation conversation;
    private User currUser;
    private User highSchoolUser;
    private LatLng locationSelected;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollegeLocationShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversation = Parcels.unwrap(getIntent().getParcelableExtra(ConversationActivity.KEY_CONVERSATION_2));
        highSchoolUser = Parcels.unwrap(getIntent().getParcelableExtra(ConversationActivity.KEY_HIGH_SCHOOL_USER));
        currUser = (User) ParseUser.getCurrentUser();
        if (conversation.meetLocationSet()) {
            locationSelected = new LatLng(conversation.getMeetLocation().getLatitude(),
                                        conversation.getMeetLocation().getLongitude());
        }

        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadMap(googleMap);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null", Toast.LENGTH_SHORT).show();
        }

        binding.aetSearchLocationAutoComplete.setFocusable(false);
        binding.aetSearchLocationAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent i = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(CollegeLocationShareActivity.this);
                startActivityForResult(i, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        binding.btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();
            }
        });

        binding.btnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng searchLocation = place.getLatLng();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation, 10));
            onMapClick(searchLocation);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.e(TAG, "Error with Places autocomplete " + status.getStatusMessage());
        }
    }

    private void openGoogleMaps() {
        String uri = "http://maps.google.com/maps?daddr=" + locationSelected.latitude + "," + locationSelected.longitude;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        i.setPackage("com.google.android.apps.maps");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation() {
        ParseGeoPoint meetLocation = new ParseGeoPoint(locationSelected.latitude, locationSelected.longitude);
        boolean firstTimeSettingLocation = !conversation.meetLocationSet();
        conversation.setMeetLocation(meetLocation, new SaveCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error saving meet location ", e);
                    Toast.makeText(CollegeLocationShareActivity.this, "Error saving meet location", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CollegeLocationShareActivity.this, "Saved meet location", Toast.LENGTH_SHORT).show();
                binding.btnSendLocation.setEnabled(false);
                binding.btnSendLocation.setText(getString(R.string.location_sent));
                binding.btnGoogleMaps.setVisibility(View.VISIBLE);
                Intent i = getIntent();
                i.putExtra(KEY_NEW_CONVERSATION, Parcels.wrap(conversation));
                setResult(RESULT_OK, i);
                if (highSchoolUser.hasFCMToken()) {
                    createAndSendJSONNotification(firstTimeSettingLocation);
                } else {
                    Log.i(TAG, "Other user doesn't have active token. Not creating notification");
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void createAndSendJSONNotification(boolean firstTimeSettingLocation) {
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put(Message.KEY_SENDER, currUser.getUsername());
            if (firstTimeSettingLocation) {
                data.put(Message.KEY_BODY, currUser.getUsername() + " " + getString(R.string.location_sent_message));
            } else {
                data.put(Message.KEY_BODY, currUser.getUsername() + " " + getString(R.string.location_updated_message));
            }
            if (currUser.hasProfileImage()) {
                data.put(User.KEY_PROFILEIMAGE, currUser.getProfileImageUrl());
            }
            data.put(Message.KEY_CONVERSATION, conversation.getObjectId());

            notification.put("to", highSchoolUser.getFCMToken());
            notification.put("data", data);

            FirebaseClient.postNotification(this, notification);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON notification ", e );
        }
    }

    @SuppressLint("LongLogTag")
    public void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.setOnMapClickListener(this);
            if (conversation.meetLocationSet()) {
                ParseGeoPoint geoPoint = conversation.getMeetLocation();
                LatLng meetLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(meetLocation)
                        .title("Meet location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetLocation, 10));
                binding.tvSelectLocationPrompt.setVisibility(View.GONE);
                binding.btnSendLocation.setEnabled(false);
                binding.btnSendLocation.setText(getString(R.string.location_sent));
                binding.btnSendLocation.setVisibility(View.VISIBLE);
                binding.btnGoogleMaps.setVisibility(View.VISIBLE);
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(Conversation.getDefaultMapLocation()));
            }
        } else {
            Toast.makeText(this, "Error - Google Map was null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.clear();
        binding.tvSelectLocationPrompt.setVisibility(View.GONE);
        binding.btnGoogleMaps.setVisibility(View.GONE);
        if (conversation.meetLocationSet()) {
            binding.btnSendLocation.setEnabled(true);
            binding.btnSendLocation.setText(getString(R.string.update_location));
        }
        binding.btnSendLocation.setVisibility(View.VISIBLE);
        locationSelected = latLng;
        map.addMarker(new MarkerOptions()
                .position(latLng));
    }
}