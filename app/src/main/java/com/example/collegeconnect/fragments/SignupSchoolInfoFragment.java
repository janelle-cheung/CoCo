package com.example.collegeconnect.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.SignupActivity;
import com.example.collegeconnect.databinding.FragmentSignupSchoolInfoBinding;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SignupSchoolInfoFragment extends Fragment {

    public static final String TAG = "SignupTypeFragment";
    private FragmentSignupSchoolInfoBinding binding;
    private List<String> suggestionIds;
    private ArrayAdapter<String> arrayAdapter;
    private String college_id;
    private String type;
    private boolean autocompleteSuggestionClicked = false;

    public SignupSchoolInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TransitionInflater transitionInflater = TransitionInflater.from(requireContext());
        setEnterTransition(transitionInflater.inflateTransition(android.R.transition.slide_right));
        setExitTransition(transitionInflater.inflateTransition(android.R.transition.fade));

        binding = FragmentSignupSchoolInfoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignupActivity signupActivityInstance = (SignupActivity) getActivity();

        Bundle bundle = this.getArguments();
        type = bundle.getString(SignupActivity.KEY_TYPE);
        if (type.equals(User.KEY_HIGHSCHOOL)) {
            binding.tvCollegePrompt.setVisibility(View.GONE);
            binding.aetCollegeAutoComplete.setVisibility(View.GONE);
        } else {
            configureAutocompleteSearch();
        }

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForValidInputs()) return;
                signupActivityInstance.setHighSchool(binding.etHighSchool.getText().toString());
                if (type.equals(User.KEY_COLLEGE)) {
                    signupActivityInstance.setCollege(binding.aetCollegeAutoComplete.getText().toString());
                    signupActivityInstance.setCollegeUnitId(college_id);
                }

                String grade;
                switch (binding.rGroupGrade.getCheckedRadioButtonId()) {
                    case R.id.radioFreshman:
                        grade = getString(R.string.freshman);
                        break;
                    case R.id.radioSophomore:
                        grade = getString(R.string.sophomore);
                        break;
                    case R.id.radioJunior:
                        grade = getString(R.string.junior);
                        break;
                    case R.id.radioSenior:
                    default:
                        grade = getString(R.string.senior);
                }
                signupActivityInstance.setGrade(grade);
                signupActivityInstance.replaceFragment(SignupExtraInfoFragment.class);
            }
        });
    }

    private void configureAutocompleteSearch() {
        suggestionIds = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
        binding.aetCollegeAutoComplete.setAdapter(arrayAdapter);
        binding.aetCollegeAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAutoComplete();
                autocompleteSuggestionClicked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.aetCollegeAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                college_id = suggestionIds.get(position);
                autocompleteSuggestionClicked = true;
            }
        });
    }

    private boolean checkForValidInputs() {
        if (binding.etHighSchool.getText().toString().isEmpty() ||
                binding.rGroupGrade.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Empty field(s)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (type.equals(User.KEY_COLLEGE)) {
            if (binding.aetCollegeAutoComplete.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Empty field(s)", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!autocompleteSuggestionClicked) {
                Toast.makeText(getContext(), "Select a college", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void getAutoComplete() {
        String collegeInput = binding.aetCollegeAutoComplete.getText().toString();
        CollegeAIClient.getAutoComplete(collegeInput, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    String success = jsonObject.getString("success");
                    if (success.equals("false")) {
                        Log.e(TAG, "GET request returned but is unsuccessful in getting data");
                        return;
                    }
                    arrayAdapter.clear();
                    suggestionIds.clear();
                    JSONArray collegeList = jsonObject.getJSONArray("collegeList");
                    for (int i = 0; i < collegeList.length(); i++) {
                        arrayAdapter.add(collegeList.getJSONObject(i).getString("name"));
                        suggestionIds.add(collegeList.getJSONObject(i).getString("unitId"));
                    }
                    // Force the adapter to filter itself, necessary to show new data.
                    arrayAdapter.getFilter().filter("", null);

                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON exception ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.e(TAG, "GET request for autocomplete failed ", e);
            }
        });
    }
}