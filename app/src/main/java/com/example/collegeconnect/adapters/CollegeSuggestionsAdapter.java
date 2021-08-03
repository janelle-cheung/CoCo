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

import java.util.List;

public class CollegeSuggestionsAdapter extends RecyclerView.Adapter<CollegeSuggestionsAdapter.ViewHolder> {

    private Context context;
    private List<College> collegeSuggestions;
    private OnCollegeListener onCollegeListener;

    public CollegeSuggestionsAdapter(Context context, List<College> collegeSuggestions, OnCollegeListener onCollegeListener) {
        this.context = context;
        this.collegeSuggestions = collegeSuggestions;
        this.onCollegeListener = onCollegeListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_suggestion, parent, false);
        return new ViewHolder(view, onCollegeListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        TextView tvCityState;
        TextView tvAcceptanceRate;
        TextView tvSATRange;
        OnCollegeListener onCollegeListener;

        public ViewHolder(@NonNull @NotNull View itemView, OnCollegeListener onCollegeListener) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvCityState = itemView.findViewById(R.id.tvCityState);
            this.tvAcceptanceRate = itemView.findViewById(R.id.tvAcceptanceRate);
            this.tvSATRange = itemView.findViewById(R.id.tvSATRange);
            this.onCollegeListener = onCollegeListener;
            itemView.setOnClickListener(this);
        }

        @SuppressLint("DefaultLocale")
        public void bind(College college) {
            tvName.setText(college.getName());
            if (college.getCityState() == null) {
                tvCityState.setVisibility(View.GONE);
            }
            else {
                tvCityState.setText(college.getCityState());
                tvSATRange.setVisibility(View.VISIBLE);
            }

            if (college.getAcceptanceRate() == -1) {
                tvAcceptanceRate.setVisibility(View.GONE);
            } else {
                String acceptanceRatePrompt = context.getString(R.string.acceptance_rate);
                tvAcceptanceRate.setText(String.format("%s %.1f%%", acceptanceRatePrompt, college.getAcceptanceRate()));
                tvAcceptanceRate.setVisibility(View.VISIBLE);
            }

            if (college.getSATRange() == null) {
                tvSATRange.setVisibility(View.GONE);
            } else {
                String SATText = "";
                if (college.getAcceptanceRate() == -1) {
                    // If the acceptance rate is gone, it messes up the SAT textview
                    tvAcceptanceRate.setText("");
                    tvAcceptanceRate.setVisibility(View.INVISIBLE);
                } else {
                    SATText += " • ";
                }
                SATText += context.getString(R.string.sat_range) + " " + college.getSATRange();
                tvSATRange.setText(SATText);
                tvSATRange.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            onCollegeListener.onCollegeSuggestionByCategoryClick(getAdapterPosition());
        }
    }

    public interface OnCollegeListener {
        void onCollegeSuggestionByCategoryClick(int position);
    }
}
