package com.example.collegeconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityUploadCollegeMediaBinding;
import com.example.collegeconnect.models.CollegeMedia;
import com.example.collegeconnect.ui.CollegeAlbumsFragment;
import com.example.collegeconnect.ui.CollegeDetailsFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadCollegeMediaActivity extends AppCompatActivity {

    public static final String TAG = "UploadCollegeMediaActivity";
    private ActivityUploadCollegeMediaBinding binding;
    private String collegeId;
    private ParseFile newFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadCollegeMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        collegeId = getIntent().getStringExtra(CollegeMediaActivity.KEY_COLLEGE_MEDIA_ACTIVITY_COLLEGE_UNIT_ID);
        Uri newFileURI = getIntent().getParcelableExtra(CollegeMediaActivity.KEY_NEW_FILE_URI);
        Bitmap selectedImage = loadFromUri(newFileURI);
        binding.ivNewMedia.setImageBitmap(selectedImage);
        newFile = conversionBitmapParseFile(selectedImage);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.albums_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnAlbum.setAdapter(adapter);

        // TODO: Set text to college name
        binding.tvCollege.setVisibility(View.GONE);

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.spnAlbum.getSelectedItem() == null) {
                    Toast.makeText(UploadCollegeMediaActivity.this, "Select an album", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadCollegeMedia();
            }
        });
    }

    private void uploadCollegeMedia() {
        String albumName = binding.spnAlbum.getSelectedItem().toString();
        String caption = binding.etCaption.getText().toString();

        CollegeMedia newMedia = new CollegeMedia();
        newMedia.setFile(newFile);
        newMedia.setCaption(caption);
        newMedia.setAlbumName(albumName);
        newMedia.setUser(ParseUser.getCurrentUser());
        newMedia.setCollegeUnitId(collegeId);
        newMedia.saveInBackground(new SaveCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(UploadCollegeMediaActivity.this, "Photo uploaded", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(UploadCollegeMediaActivity.this, "Error while uploading photo", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error while uploading media");
                }
            }
        });
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(UploadCollegeMediaActivity.this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(UploadCollegeMediaActivity.this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
}