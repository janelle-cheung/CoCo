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
import android.widget.ImageButton;
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
    private ArrayAdapter<String> arrayAdapter;
    private FragmentSearchBinding binding;
    private CollegeSuggestionsAdapter adapter;
    private List<College> collegeSuggestionsByCategory;
    private List<String> autocompleteSuggestionIds;
    private String formattedCategory;
    private JSONObject filtersJSON;
    private String[] formattedCollegeCategoriesArray;
    String collegeId;
    boolean autocompleteSuggestionClicked = false;

    private boolean filterPublicChecked = false;
    private boolean filterPrivateChecked = false;
    private boolean filter4YearChecked = false;
    private boolean filter2YearChecked = false;
    private boolean filterSmallChecked = false;
    private boolean filterMediumChecked = false;
    private boolean filterLargeChecked = false;
    private int filterMaxNetCost = -1;
    private int filterSAT = -1;
    private int filterACT = -1;
    private static final int MIN_SAT_SCORE = 600;
    private static final int MIN_ACT_SCORE = 12;

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
                if (input.isEmpty() || autocompleteSuggestionIds.isEmpty() || !autocompleteSuggestionClicked) {
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
                autocompleteSuggestionClicked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.aetCollegeAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                collegeId = autocompleteSuggestionIds.get(position);
                autocompleteSuggestionClicked = true;
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
        formattedCategory = getString(R.string.formatted_best_colleges_category);
        // Filters are defaulted to none
        filtersJSON = new JSONObject();
        formattedCollegeCategoriesArray = getResources().getStringArray(R.array.formatted_college_categories_array);
        getCollegeSuggestionsByCategoryAndFilters();

        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formattedCategory = formattedCollegeCategoriesArray[position];
                getCollegeSuggestionsByCategoryAndFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCollegeSuggestionsByCategoryAndFilters() {
        CollegeAIClient.getCollegeSuggestionsByCategoryAndFilters(formattedCategory, filtersJSON, new JsonHttpResponseHandler() {
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
                ImageButton ibClearCost = filterAlertDialog.findViewById(R.id.ibClearCost);
                ImageButton ibClearSAT = filterAlertDialog.findViewById(R.id.ibClearSAT);
                ImageButton ibClearACT = filterAlertDialog.findViewById(R.id.ibClearACT);


                // Set saved filters
                if (filterPublicChecked) { cbxPublic.setChecked(true); }
                if (filterPrivateChecked) { cbxPrivate.setChecked(true); }
                if (filter4YearChecked) { cbx4Year.setChecked(true); }
                if (filter2YearChecked) { cbx2Year.setChecked(true); }
                if (filterSmallChecked) { btnSmall.setSelected(true); }
                if (filterMediumChecked) { btnMedium.setSelected(true); }
                if (filterLargeChecked) { btnLarge.setSelected(true); }
                if (filterMaxNetCost == -1) {
                    ibClearCost.setSelected(true);
                } else {
                    tvSliderCost.setText((String.format("%s: $%s", getString(R.string.college_cost), (int) filterMaxNetCost)));
                    sliderCost.setValue(filterMaxNetCost);
                }

                if (filterSAT == -1) {
                    ibClearSAT.setSelected(true);
                } else {
                    tvSliderSAT.setText((String.format("%s: %s", getString(R.string.SAT), (int) filterSAT)));
                    sliderSAT.setValue(filterSAT);
                }

                if (filterACT == -1) {
                    ibClearACT.setSelected(true);
                } else {
                    tvSliderACT.setText((String.format("%s: %s", getString(R.string.ACT), (int) filterACT)));
                    sliderACT.setValue(filterACT);
                }

                Log.i(TAG, "clearACT.isSelected(): " + ibClearACT.isSelected());

                btnSmall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { btnSmall.setSelected(!btnSmall.isSelected()); }
                });

                btnMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { btnMedium.setSelected(!btnMedium.isSelected()); }
                });

                btnLarge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { btnLarge.setSelected(!btnLarge.isSelected()); }
                });

                sliderCost.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                        tvSliderCost.setText((String.format("%s: $%s", getString(R.string.college_cost), (int) value)));
                        ibClearCost.setSelected(false);
                    }
                });

                sliderSAT.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                        tvSliderSAT.setText((String.format("%s: %s", getString(R.string.SAT), (int) value)));
                        ibClearSAT.setSelected(false);
                    }
                });

                sliderACT.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                        tvSliderACT.setText((String.format("%s: %s", getString(R.string.ACT), (int) value)));
                        ibClearACT.setSelected(false);
                    }
                });

                ibClearCost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sliderCost.setValue(0);
                        tvSliderCost.setText(getString(R.string.college_cost_hint));
                        ibClearCost.setSelected(true);
                    }
                });

                ibClearSAT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sliderSAT.setValue(MIN_SAT_SCORE);
                        tvSliderSAT.setText(getString(R.string.sat_slider_hint));
                        ibClearSAT.setSelected(true);
                    }
                });

                ibClearACT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sliderACT.setValue(MIN_ACT_SCORE);
                        tvSliderACT.setText(getString(R.string.act_slider_hint));
                        ibClearACT.setSelected(true);
                    }
                });

                // Apply button: send data from the AlertDialog to the Activity
                builder.setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                filterPublicChecked = cbxPublic.isChecked();
                                filterPrivateChecked = cbxPrivate.isChecked();
                                filter4YearChecked = cbx4Year.isChecked();
                                filter2YearChecked = cbx2Year.isChecked();
                                filterSmallChecked = btnSmall.isSelected();
                                filterMediumChecked = btnMedium.isSelected();
                                filterLargeChecked = btnLarge.isSelected();
                                if (ibClearCost.isSelected()) { filterMaxNetCost = -1; }
                                else { filterMaxNetCost = (int) sliderCost.getValue(); }

                                if (ibClearSAT.isSelected()) { filterSAT = -1; }
                                else { filterSAT = (int) sliderSAT.getValue(); }

                                if (ibClearACT.isSelected()) { filterACT = -1; }
                                else { filterACT = (int) sliderACT.getValue(); }

                                filtersJSON = CollegeAIClient.createFiltersJSONObject(
                                        filterPublicChecked, filterPrivateChecked,
                                        filter4YearChecked, filter2YearChecked,
                                        filterSmallChecked, filterMediumChecked, filterLargeChecked,
                                        filterMaxNetCost, filterSAT, filterACT);
                                getCollegeSuggestionsByCategoryAndFilters();

                                int numFiltersApplied = 0;
                                if (cbxPublic.isChecked() || cbxPrivate.isChecked() || cbx4Year.isChecked() || cbx2Year.isChecked()) numFiltersApplied++;
                                if (btnSmall.isSelected() || btnMedium.isSelected() || btnLarge.isSelected()) numFiltersApplied++;
                                if (!ibClearCost.isSelected()) numFiltersApplied++;
                                if (!ibClearSAT.isSelected()) numFiltersApplied++;
                                if (!ibClearACT.isSelected()) numFiltersApplied++;
                                if (numFiltersApplied > 0) {
                                    binding.tvFilterCount.setText(String.valueOf(numFiltersApplied));
                                    binding.tvFilterCount.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvFilterCount.setVisibility(View.GONE);
                                }
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