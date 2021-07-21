package com.example.collegeconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeconnect.R;

import org.jetbrains.annotations.NotNull;

public class CollegeAlbumAdapter extends RecyclerView.Adapter<CollegeAlbumAdapter.ViewHolder> {

    Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCollegeMedia;
        TextView tvCaption;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.ivCollegeMedia = itemView.findViewById(R.id.ivCollegeMedia);
            this.tvCaption = itemView.findViewById(R.id.tvCaption);
        }

        public void bind() {

        }
    }
}
