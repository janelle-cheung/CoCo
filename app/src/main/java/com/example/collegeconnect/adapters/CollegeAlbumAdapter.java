package com.example.collegeconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.models.CollegeMedia;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollegeAlbumAdapter extends RecyclerView.Adapter<CollegeAlbumAdapter.ViewHolder> {

    Context context;
    List<CollegeMedia> allMedia;

    public CollegeAlbumAdapter(Context context, List<CollegeMedia> media) {
        this.context = context;
        this.allMedia = media;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        CollegeMedia media = allMedia.get(position);
        holder.bind(media);
    }

    @Override
    public int getItemCount() {
        return allMedia.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCollegeMedia;
        TextView tvCaption;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.ivCollegeMedia = itemView.findViewById(R.id.ivCollegeMedia);
            this.tvCaption = itemView.findViewById(R.id.tvCaption);
        }

        public void bind(CollegeMedia media) {
            Glide.with(context)
                    .load(media.getFile().getUrl())
                    .centerCrop()
                    .into(ivCollegeMedia);
            tvCaption.setText(media.getCaption());
        }
    }
}
