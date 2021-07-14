package com.example.collegeconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeconnect.R;
import com.example.collegeconnect.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollegeStudentsAdapter extends RecyclerView.Adapter<CollegeStudentsAdapter.ViewHolder> {

    Context context;
    List<User> collegeStudents;

    public CollegeStudentsAdapter(Context context, List<User> collegeStudents) {
        this.context = context;
        this.collegeStudents = collegeStudents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_student, parent, false);
        return new ViewHolder(view);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCollegeName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
        }

        public void bind(User collegeStudent) {
            tvCollegeName.setText(collegeStudent.getUsername());
        }
    }
}
