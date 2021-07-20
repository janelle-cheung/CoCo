package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityCollegeLocationShareBinding;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.User;
import com.example.collegeconnect.ui.conversations.ConversationsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

public class CollegeLocationShare extends AppCompatActivity implements GoogleMap.OnMapClickListener {

    public static final String TAG = "CollegeLocationShare";
    private ActivityCollegeLocationShareBinding binding;
    private boolean locationPermissionGranted = false;
    private GoogleMap map;
    private Conversation conversation;
    private LatLng locationSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityCollegeLocationShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversation = Parcels.unwrap(getIntent().getParcelableExtra(ConversationActivity.KEY_CONVERSATION_2));

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

        binding.btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();
            }
        });
    }

    private void saveLocation() {
        ParseGeoPoint meetLocation = new ParseGeoPoint(locationSelected.latitude, locationSelected.longitude);
        conversation.setMeetLocation(meetLocation, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error saving meet location ", e);
                    Toast.makeText(CollegeLocationShare.this, "Error saving meet location", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CollegeLocationShare.this, "Saved meet location", Toast.LENGTH_SHORT).show();
                binding.btnSendLocation.setEnabled(false);
                binding.btnSendLocation.setText(getString(R.string.location_sent));
            }
        });
    }

    public void loadMap(GoogleMap googleMap) {
        Log.i(TAG, "loadMap");
        map = googleMap;
        if (map != null) {
            map.setOnMapClickListener(this);
            if (conversation.meetLocationSet()) {
                ParseGeoPoint geoPoint = conversation.getMeetLocation();
                Log.i(TAG, String.valueOf(geoPoint.getLatitude()));
                LatLng meetLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(meetLocation)
                        .title("Meet location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(meetLocation));
                binding.tvSelectLocationPrompt.setVisibility(View.GONE);
                binding.btnSendLocation.setEnabled(false);
                binding.btnSendLocation.setText(getString(R.string.location_sent));
                binding.btnSendLocation.setVisibility(View.VISIBLE);
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