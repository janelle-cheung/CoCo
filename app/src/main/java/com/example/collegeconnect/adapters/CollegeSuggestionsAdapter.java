package com.example.collegeconnect.adapters;

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

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(College college) {
            tvName.setText(college.getName());
        }
    }
}
