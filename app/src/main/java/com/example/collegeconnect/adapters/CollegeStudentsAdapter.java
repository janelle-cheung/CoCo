package com.example.collegeconnect.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollegeStudentsAdapter extends RecyclerView.Adapter<CollegeStudentsAdapter.ViewHolder> {

    Context context;
    List<User> collegeStudents;
    OnCollegeStudentListener onCollegeStudentListener;

    public CollegeStudentsAdapter(Context context, List<User> collegeStudents, OnCollegeStudentListener onCollegeStudentListener) {
        this.context = context;
        this.collegeStudents = collegeStudents;
        this.onCollegeStudentListener = onCollegeStudentListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_student, parent, false);
        return new ViewHolder(view, onCollegeStudentListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User collegeStudent = collegeStudents.get(position);
        holder.bind(collegeStudent);
    }

    @Override
    public int getItemCount() {
        return collegeStudents.size();
    }

    public void clear() {
        collegeStudents.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivStudentImage;
        TextView tvCollegeName;
        OnCollegeStudentListener onCollegeStudentListener;

        public ViewHolder(View itemView, OnCollegeStudentListener onCollegeStudentListener) {
            super(itemView);
            tvCollegeName = itemView.findViewById(R.id.tvStudentName);
            ivStudentImage = itemView.findViewById(R.id.ivStudentImage);
            this.onCollegeStudentListener = onCollegeStudentListener;
            itemView.setOnClickListener(this);
        }

        public void bind(User collegeStudent) {
            tvCollegeName.setText(collegeStudent.getUsername());
            if (collegeStudent.hasProfileImage()) {
                Glide.with(context)
                        .load(collegeStudent.getProfileImageUrl())
                        .placeholder(R.mipmap.profile_placeholder_foreground)
                        .centerCrop()
                        .into(ivStudentImage);
            } else {
                // Display placeholder image if user has no profile image
                Glide.with(context)
                        .load(R.mipmap.profile_placeholder_foreground)
                        .centerCrop()
                        .into(ivStudentImage);
            }
        }

        @Override
        public void onClick(View v) {
            onCollegeStudentListener.onCollegeStudentClick(getAdapterPosition());
        }
    }

    public interface OnCollegeStudentListener {
        void onCollegeStudentClick(int position);
    }
}
