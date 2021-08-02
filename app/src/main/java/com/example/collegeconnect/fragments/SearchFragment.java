package com.example.collegeconnect.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.collegeconnect.CollegeAIClient;
import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.SearchResultActivity;
import com.example.collegeconnect.adapters.CollegeSuggestionsAdapter;
import com.example.collegeconnect.databinding.FragmentSearchBinding;
import com.example.collegeconnect.models.College;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SearchFragment extends Fragment implements CollegeSuggestionsAdapter.OnCollegeListener {

    public static final String TAG = "SearchFragment";
    public static final String KEY_SEARCH_FRAG_COLLEGE_ID = "SearchFrag collegeId";
    private static final int MIN_SAT_SCORE = 600;
    private static final int MIN_ACT_SCORE = 12;
    private ArrayAdapter<String> arrayAdapter;
    private FragmentSearchBinding binding;
    private CollegeSuggestionsAdapter adapter;
    private List<College> collegeSuggestionsByCategory;
    private List<String> autocompleteSuggestionIds;
    String collegeId;
    boolean suggestionClicked = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        configureAutocompleteSearch();
        configureSuggestionsByCategory();
        configureFilterDialog();

        // When user clicks on search button, open search result activity -> college details fragment
        binding.ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = binding.aetCollegeAutoComplete.getText().toString();
                if (input.isEmpty() || autocompleteSuggestionIds.isEmpty() || !suggestionClicked) {
                    return;
                }
                launchSearchResultActivity(collegeId);
            }
        });

        return binding.getRoot();
    }

    private void configureAutocompleteSearch() {
        autocompleteSuggestionIds = new ArrayList<>();
        List<String> autocompleteSuggestions = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, autocompleteSuggestions);
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
                collegeId = autocompleteSuggestionIds.get(position);
                suggestionClicked = true;
            }
        });
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
                    autocompleteSuggestionIds.clear();
                    JSONArray collegeList = jsonObject.getJSONArray("collegeList");
                    for (int i = 0; i < collegeList.length(); i++) {
                        Log.i(TAG, collegeList.getJSONObject(i).getString(College.KEY_NAME));
                        arrayAdapter.add(collegeList.getJSONObject(i).getString(College.KEY_NAME));
                        autocompleteSuggestionIds.add(collegeList.getJSONObject(i).getString(College.KEY_UNIT_ID));
                    }
                    // Force the adapter to filter itself, necessary to show new data.
                    arrayAdapter.getFilter().filter("", null);

                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON exception getting auto complete ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.e(TAG, "GET request for autocomplete failed ", e);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureSuggestionsByCategory() {
        collegeSuggestionsByCategory = new ArrayList<>();
        adapter = new CollegeSuggestionsAdapter(getContext(), collegeSuggestionsByCategory, this);
        binding.rvSuggestions.setAdapter(adapter);
        binding.rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.readable_college_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnCategory.setAdapter(adapter);

        // Default suggestions category is "Best colleges"
        String defaultCategory = getString(R.string.formatted_best_colleges_category);
        getCollegeSuggestionsByCategory(defaultCategory);

        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals(getString(R.string.readable_best_colleges_category))) {
                    getCollegeSuggestionsByCategory(getString(R.string.formatted_best_colleges_category));
                } else if (selectedItem.equals(getString(R.string.readable_best_value_category))) {
                    getCollegeSuggestionsByCategory(getString(R.string.formatted_best_value_category));
                } else if (selectedItem.equals(getString(R.string.readable_best_academics_category))) {
                    getCollegeSuggestionsByCategory(getString(R.string.formatted_best_academics_category));
                } else if (selectedItem.equals(getString(R.string.readable_best_student_life_category))) {
                    getCollegeSuggestionsByCategory(getString(R.string.formatted_best_student_life_category));
                } else if (selectedItem.equals(getString(R.string.readable_best_CS_category))) {
                    getCollegeSuggestionsByCategory(getString(R.string.formatted_best_CS_category));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCollegeSuggestionsByCategory(String category) {
        CollegeAIClient.getCollegeSuggestionsByCategory(category, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    String success = jsonObject.getString("success");
                    if (success.equals("false")) {
                        Log.e(TAG, "GET request returned but is unsuccessful in getting data");
                        return;
                    }
                    JSONArray collegeList = jsonObject.getJSONArray("colleges");
                    adapter.clear();
                    collegeSuggestionsByCategory.addAll(College.fromJSONArray(collegeList));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d(TAG, "Hit JSON exception getting suggestions ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    private void configureFilterDialog() {
        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                final View filterAlertDialog = getLayoutInflater().inflate(R.layout.filter_alert_dialog, null);
                builder.setView(filterAlertDialog);

                CheckBox cbxPublic = filterAlertDialog.findViewById(R.id.cbxPublic);
                CheckBox cbxPrivate = filterAlertDialog.findViewById(R.id.cbxPrivate);
                CheckBox cbx4Year = filterAlertDialog.findViewById(R.id.cbx4Year);
                CheckBox cbx2Year = filterAlertDialog.findViewById(R.id.cbx2Year);
                TextView tvSliderCost = filterAlertDialog.findViewById(R.id.tvSliderCost);
                Slider sliderCost = filterAlertDialog.findViewById(R.id.sliderCost);
                TextView tvSliderSAT = filterAlertDialog.findViewById(R.id.tvSliderSAT);
                Slider sliderSAT = filterAlertDialog.findViewById(R.id.sliderSAT);
                TextView tvSliderACT = filterAlertDialog.findViewById(R.id.tvSliderACT);
                Slider sliderACT = filterAlertDialog.findViewById(R.id.sliderACT);
                Button btnSmall = filterAlertDialog.findViewById(R.id.btnCollegeSmall);
                Button btnMedium = filterAlertDialog.findViewById(R.id.btnCollegeMedium);
                Button btnLarge = filterAlertDialog.findViewById(R.id.btnCollegeLarge);

                btnSmall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnSmall.setSelected(!btnSmall.isSelected());
                    }
                });

                btnMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnMedium.setSelected(!btnMedium.isSelected());
                    }
                });

                btnLarge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnLarge.setSelected(!btnLarge.isSelected());
                    }
                });

                // If max net cost slider is set to 0, make slider gray and don't filter with max net cost
                sliderCost.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                        String maxNetCostPrompt = getString(R.string.college_cost);
                        tvSliderCost.setText(String.format("%s: $%s", maxNetCostPrompt, (int) value));
                        if (value > 0) {
                            sliderCost.setThumbStrokeColor(ColorStateList.valueOf(0x04C4CFF));
                        } else {
                            sliderCost.setThumbStrokeColor(ColorStateList.valueOf(0xFFAAAAAA));
                        }
                    }
                });

                // If SAT slider is set to 600, make slider gray and don't filter with SAT score
                sliderSAT.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                        tvSliderSAT.setText(String.format("SAT: %s", (int) value));
                        if (value > MIN_SAT_SCORE) {
                            sliderSAT.setThumbStrokeColor(ColorStateList.valueOf(Color.parseColor("#4C4CFF")));
                        } else {
                            sliderSAT.setThumbStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFAAAAAA")));
                        }
                    }
                });

                // If ACT slider is set to 12, make slider gray and don't filter with ACT score
                sliderACT.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                        tvSliderACT.setText(String.format("ACT: %s", (int) value));
                        if (value > MIN_ACT_SCORE) {
                            sliderACT.setThumbStrokeColor(ColorStateList.valueOf(Color.parseColor("#4C4CFF")));
                        } else {
                            sliderACT.setThumbStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFAAAAAA")));
                        }
                    }
                });

                // Apply button: send data from the AlertDialog to the Activity
                builder.setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendDialogDataToFragment(cbxPublic.isChecked(), cbxPrivate.isChecked(),
                                                        cbx4Year.isChecked(), cbx2Year.isChecked(),
                                                        (int) sliderCost.getValue(),
                                                        (int) sliderSAT.getValue(),
                                                        (int) sliderACT.getValue());
                            }
                        }
                );

                // Cancel button
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Show dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
            }
        });
    }

    private void sendDialogDataToFragment(boolean filterPublic, boolean filterPrivate,
                                          boolean filter4Year, boolean filter2Year,
                                          int maxCost, int SAT, int ACT) {
        Toast.makeText(getContext(), String.valueOf(maxCost), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), String.valueOf(filterPublic), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCollegeSuggestionByCategoryClick(int position) {
        launchSearchResultActivity(collegeSuggestionsByCategory.get(position).getCollegeUnitId());
    }
}