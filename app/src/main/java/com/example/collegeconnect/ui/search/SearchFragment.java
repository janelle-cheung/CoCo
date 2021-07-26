package com.example.collegeconnect.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.databinding.FragmentSearchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    public static final String KEY_SEARCH_FRAG_COLLEGE_ID = "SearchFrag collegeId";
    private SearchViewModel mViewModel;
    private List<String> suggestionIds;
    private ArrayAdapter<String> arrayAdapter;
    private FragmentSearchBinding binding;
    String collegeId;
    boolean suggestionClicked = false;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

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
                suggestionClicked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.aetCollegeAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                collegeId = suggestionIds.get(position);
                suggestionClicked = true;
            }
        });

        binding.ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = binding.aetCollegeAutoComplete.getText().toString();
                if (input.isEmpty() || suggestionIds.isEmpty() || !suggestionClicked) { return; }
                launchSearchResultActivity(collegeId);
            }
        });

        return binding.getRoot();
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
                    // Filter based on the current text because api call is asynchronous.
                    arrayAdapter.getFilter().filter(collegeInput, null);

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

    private void launchSearchResultActivity(String collegeId) {
        Intent i = new Intent(getContext(), SearchResultActivity.class);
        i.putExtra(KEY_SEARCH_FRAG_COLLEGE_ID, collegeId);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}