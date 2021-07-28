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
    OnMediaListener onMediaListener;

    public CollegeAlbumAdapter(Context context, List<CollegeMedia> media, OnMediaListener onMediaListener) {
        this.context = context;
        this.allMedia = media;
        this.onMediaListener = onMediaListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college_media, parent, false);
        return new ViewHolder(view, onMediaListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivCollegeMedia;
        TextView tvCaption;
        OnMediaListener onMediaListener;

        public ViewHolder(@NonNull @NotNull View itemView, OnMediaListener onMediaListener) {
            super(itemView);
            this.ivCollegeMedia = itemView.findViewById(R.id.ivCollegeMedia);
            this.tvCaption = itemView.findViewById(R.id.tvCaption);
            this.onMediaListener = onMediaListener;
            itemView.setOnClickListener(this);
        }

        public void bind(CollegeMedia media) {
            Glide.with(context)
                    .load(media.getFile().getUrl())
                    .centerCrop()
                    .into(ivCollegeMedia);
            tvCaption.setText(media.getCaption());
        }

        @Override
        public void onClick(View v) {
            onMediaListener.onMediaClick(getAdapterPosition(), ivCollegeMedia);
        }
    }

    public interface OnMediaListener {
        void onMediaClick(int position, View ivCollegeMedia);
    }
}
