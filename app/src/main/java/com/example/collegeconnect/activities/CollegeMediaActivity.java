package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.collegeconnect.R;
import com.example.collegeconnect.ui.CollegeAlbumsFragment;
import com.example.collegeconnect.ui.CollegeDetailsFragment;
import com.example.collegeconnect.ui.search.SearchFragment;

public class CollegeMediaActivity extends AppCompatActivity {

    public static final String TAG = "CollegeMediaActivity";
    public static final int UPLOAD_MEDIA_REQUEST_CODE = 432;
    public static final int SELECT_IMAGE_REQUEST_CODE = 234;
    public static final String KEY_COLLEGE_MEDIA_ACTIVITY_COLLEGE_UNIT_ID = "CollegeMediaActivity collegeId";
    public static final String KEY_NEW_FILE_URI = "new file URI";
    public String collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_media);

        collegeId = getIntent().getStringExtra(CollegeDetailsFragment.KEY_COLLEGE_DETAILS_FRAGMENT_COLLEGE_ID);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, CollegeAlbumsFragment.class, new Bundle())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_college_media, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miUpload) {
            pickPhoto();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Trigger gallery selection for a photo
    @SuppressLint("QueryPermissionsNeeded")
    public void pickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (i.resolveActivity(CollegeMediaActivity.this.getPackageManager()) != null) {
            startActivityForResult(i, SELECT_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (data != null && requestCode == SELECT_IMAGE_REQUEST_CODE) {
            Intent i = new Intent(this, UploadCollegeMediaActivity.class);
            i.putExtra(KEY_COLLEGE_MEDIA_ACTIVITY_COLLEGE_UNIT_ID, collegeId);
            i.putExtra(KEY_NEW_FILE_URI, data.getData());
            startActivityForResult(i, UPLOAD_MEDIA_REQUEST_CODE);
        } else if (requestCode == UPLOAD_MEDIA_REQUEST_CODE){
            Fragment currFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
            if (currFragment instanceof CollegeAlbumsFragment) {
                ((CollegeAlbumsFragment) currFragment).onMediaUploaded();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void changeFragment(Class fragmentClass, Bundle bundle) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, fragmentClass, bundle)
                .addToBackStack(null)
                .commit();
    }
}