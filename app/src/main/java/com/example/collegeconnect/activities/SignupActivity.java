package com.example.collegeconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ActivityMainBinding;
import com.example.collegeconnect.databinding.ActivitySignupBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra(SelectTypeActivity.KEY_TYPE);
        if (type.equals("high school")) { binding.etHighSchool.setVisibility(View.GONE); }

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fieldsValid()) {
                    Toast.makeText(SignupActivity.this, "Fields cannot be left blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get what grade the user selected
                String grade;
                switch (binding.rGroupGrade.getCheckedRadioButtonId()) {
                    case R.id.radioFreshman: grade = "freshman"; break;
                    case R.id.radioSophomore: grade = "sophomore"; break;
                    case R.id.radioJunior: grade = "junior"; break;
                    case R.id.radioSenior:
                    default: grade = "senior";
                }

                // Create new Parse user with user inputs
                ParseUser user = new ParseUser();
                user.setUsername(binding.etName.getText().toString());
                user.setEmail(binding.etEmail.getText().toString());
                user.setPassword(binding.etPassword.getText().toString());
                user.put(getString(R.string.KEY_TYPE), type);
                user.put(getString(R.string.KEY_FROM), binding.etFrom.getText().toString());
                user.put(getString(R.string.KEY_SCHOOL), binding.etSchool.getText().toString());
                user.put(getString(R.string.KEY_ACADEMICS), binding.etAcademics.getText().toString());
                user.put(getString(R.string.KEY_EXTRACURRICULARS), binding.etExtracurriculars.getText().toString());
                user.put(getString(R.string.KEY_GRADE), grade);
                if (type.equals("college")) {
                    user.put(getString(R.string.KEY_HIGHSCHOOL), binding.etHighSchool.getText().toString());
                }

                // Send request to Parse to save new user
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(SignupActivity.this, "Account sign-up successful", Toast.LENGTH_SHORT).show();
                            launchMainActivity();
                        } else {
                            Toast.makeText(SignupActivity.this, "Error with sign-up", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error signing up ", e);
                            return;
                        }
                    }
                });
            }
        });
    }

    // Check if user filled out all fields
    private boolean fieldsValid() {
        if (binding.etName.getText().toString().isEmpty()) return false;
        if (binding.etEmail.getText().toString().isEmpty()) return false;
        if (binding.etPassword.getText().toString().isEmpty()) return false;
        if (binding.etFrom.getText().toString().isEmpty()) return false;
        if (binding.etSchool.getText().toString().isEmpty()) return false;
        if (binding.rGroupGrade.getCheckedRadioButtonId() == -1) return false;
        if (binding.etAcademics.getText().toString().isEmpty()) return false;
        if (binding.etExtracurriculars.getText().toString().isEmpty()) return false;
        // College students must fill out the high school field
        if (type.equals("college") && binding.etHighSchool.getText().toString().isEmpty()) return false;
        return true;
    }

    private void launchMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}