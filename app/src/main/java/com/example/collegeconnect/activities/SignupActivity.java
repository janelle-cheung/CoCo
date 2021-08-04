package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityMainBinding;
import com.example.collegeconnect.databinding.ActivitySignupBinding;
import com.example.collegeconnect.fragments.CollegeDetailsFragment;
import com.example.collegeconnect.fragments.SignupExtraInfoFragment;
import com.example.collegeconnect.fragments.SignupRegistrationFragment;
import com.example.collegeconnect.fragments.SignupSchoolInfoFragment;
import com.example.collegeconnect.fragments.SignupTypeFragment;
import com.example.collegeconnect.models.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    public static final String KEY_TYPE = "type";
    private ActivitySignupBinding binding;


    private String type;
    private String username;
    private String email;
    private String password;
    private String college;
    private String collegeUnitId;
    private String highSchool;
    private String grade;
    private String from;
    private String academicInterests;
    private String extracurricularInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, SignupRegistrationFragment.class, new Bundle())
                .commit();
    }

    public void replaceFragment(Class fragmentClass) {
        Bundle bundle = new Bundle();
        if (fragmentClass.equals(SignupSchoolInfoFragment.class)) {
            bundle.putString(KEY_TYPE, type);
        }
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, fragmentClass, bundle)
                .addToBackStack(null)
                .commit();
    }

    public void createUserAccount() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setType(type);
        user.setHighSchool(highSchool);
        user.setGrade(grade);
        user.setFrom(from);
        user.setAcademics(academicInterests);
        user.setExtracurriculars(extracurricularInterests);

        if (type.equals(User.KEY_COLLEGE)) {
            user.setCollege(college);
            user.setCollegeUnitId(collegeUnitId);
        }

        // Send request to Parse to save new user
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignupActivity.this, "Signed up!", Toast.LENGTH_SHORT).show();
                    launchMainActivity();
                } else {
                    Toast.makeText(SignupActivity.this, "Error with sign-up", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error signing up ", e);
                }
            }
        });
    }

    private void launchMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCollege(String college) {
        this.college = college;
    }
    public void setCollegeUnitId(String collegeUnitId) {
        this.collegeUnitId = collegeUnitId;
    }

    public void setHighSchool(String highSchool) {
        this.highSchool = highSchool;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setAcademicInterests(String academicInterests) {
        this.academicInterests = academicInterests;
    }

    public void setExtracurricularInterests(String extracurricularInterests) {
        this.extracurricularInterests = extracurricularInterests;
    }
}