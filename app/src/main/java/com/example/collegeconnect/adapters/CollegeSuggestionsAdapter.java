package com.example.collegeconnect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeconnect.R;
import com.example.collegeconnect.models.College;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class CollegeSuggestionsAdapter extends RecyclerView.Adapter<CollegeSuggestionsAdapter.ViewHolder> {

    Context context;
    List<College> collegeSuggestions;

    public CollegeSuggestionsAdapter(Context context, List<College> collegeSuggestions) {
        this.context = context;
        this.collegeSuggestions = collegeSuggestions;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        College college = collegeSuggestions.get(position);
        holder.bind(college);
    }

    @Override
    public int getItemCount() {
        return collegeSuggestions.size();
    }

    public void clear() {
        collegeSuggestions.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCityState;
        TextView tvAcceptanceRate;
        TextView tvSATRange;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvCityState = itemView.findViewById(R.id.tvCityState);
            this.tvAcceptanceRate = itemView.findViewById(R.id.tvAcceptanceRate);
            this.tvSATRange = itemView.findViewById(R.id.tvSATRange);
        }

        @SuppressLint("DefaultLocale")
        public void bind(College college) {
            tvName.setText(college.getName());
            if (college.getCityState() == null) { tvCityState.setVisibility(View.GONE); }
            else { tvCityState.setText(college.getCityState()); }

            if (college.getAcceptanceRate() == -1) {
                tvAcceptanceRate.setVisibility(View.GONE);
            } else {
                String acceptanceRatePrompt = context.getString(R.string.acceptance_rate);
                tvAcceptanceRate.setText(String.format("%s %.1f%% • ", acceptanceRatePrompt, college.getAcceptanceRate()));
            }

            if (college.getSATRange() == null) {
                tvSATRange.setVisibility(View.GONE);
            } else {
                String SATRangePrompt = context.getString(R.string.sat_range);
                tvSATRange.setText(String.format("%s %s", SATRangePrompt, college.getSATRange()));
            }
        }
    }
}
