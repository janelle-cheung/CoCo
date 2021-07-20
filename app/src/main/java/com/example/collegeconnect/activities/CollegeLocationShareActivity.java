package com.example.collegeconnect.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityCollegeLocationShareBinding;
import com.example.collegeconnect.models.Conversation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollegeLocationShareActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener {

    public static final String TAG = "CollegeLocationShareActivity";
    public static final String KEY_NEW_CONVERSATION = "new conversation";
    public static final int AUTOCOMPLETE_REQUEST_CODE = 100;
    private ActivityCollegeLocationShareBinding binding;
    private GoogleMap map;
    private Conversation conversation;
    private LatLng locationSelected;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
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
                Intent i = getIntent();
                i.putExtra(KEY_NEW_CONVERSATION, Parcels.wrap(conversation));
                setResult(RESULT_OK, i);
            }
        });
    }

    @SuppressLint("LongLogTag")
    public void loadMap(GoogleMap googleMap) {
        Log.i(TAG, "loadMap");
        map = googleMap;
        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.setOnMapClickListener(this);
            if (conversation.meetLocationSet()) {
                ParseGeoPoint geoPoint = conversation.getMeetLocation();
                Log.i(TAG, String.valueOf(geoPoint.getLatitude()));
                LatLng meetLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(meetLocation)
                        .title("Meet location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetLocation, 10));
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