package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityHighSchoolLocationShareBinding;
import com.example.collegeconnect.models.Conversation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

public class HighSchoolLocationShareActivity extends AppCompatActivity {

    private ActivityHighSchoolLocationShareBinding binding;
    private GoogleMap map;
    private Conversation conversation;
    private LatLng meetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHighSchoolLocationShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversation = Parcels.unwrap(getIntent().getParcelableExtra(ConversationActivity.KEY_CONVERSATION_2));
        ParseGeoPoint geoPoint = conversation.getParseGeoPoint(Conversation.KEY_MEET_LOCATION);
        meetLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

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
    }

    public void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            if (conversation.meetLocationSet()) {
                ParseGeoPoint geoPoint = conversation.getMeetLocation();
                LatLng meetLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(meetLocation)
                        .title("Meet location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetLocation, 10));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(Conversation.getDefaultMapLocation()));
            }
        } else {
            Toast.makeText(this, "Error - Google Map was null", Toast.LENGTH_SHORT).show();
        }
    }

}