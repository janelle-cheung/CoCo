package com.example.collegeconnect.adapters;

import android.content.ClipData;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.ItemConversationBinding;
import com.example.collegeconnect.models.College;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    public static final String TAG = "ConversationsAdapter";
    Context context;
    List<Conversation> conversations;
    List<Conversation> conversationsForFilter;
    OnConversationListener onConversationListener;
    boolean isHighSchool;

    public ConversationsAdapter(Context context, List<Conversation> conversations, OnConversationListener onConversationListener, boolean isHighSchool) {
        this.context = context;
        this.conversations = conversations;
        this.onConversationListener = onConversationListener;
        this.isHighSchool = isHighSchool;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view, onConversationListener);
    }

    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void clear() {
        conversations.clear();
        notifyDataSetChanged();
    }

    public void setAllConversationsForFilter(List<Conversation> conversations) {
        conversationsForFilter = new ArrayList<>(conversations);
    }

    public void filter(String filter) {
        filter = filter.toLowerCase(Locale.getDefault());
        conversations.clear();
        if (filter.isEmpty()) {
            conversations.addAll(conversationsForFilter);
            notifyDataSetChanged();
            return;
        }

        // Filter by both username and school name
        for (Conversation c : conversationsForFilter) {
            if (isHighSchool &&
                    (c.getCollegeStudent().getUsername().toLowerCase().contains(filter) ||
                     c.getCollegeStudent().getCollege().toLowerCase().contains(filter))) {
                conversations.add(c);
            } else if (!isHighSchool &&
                    (c.getHighSchoolStudent().getUsername().toLowerCase().contains(filter) ||
                     c.getHighSchoolStudent().getHighSchool().toLowerCase().contains(filter))) {
                conversations.add(c);
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvGradeAndSchool;
        User user;
        OnConversationListener onConversationListener;

        public ViewHolder(View itemView, OnConversationListener onConversationListener) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvGradeAndSchool = itemView.findViewById(R.id.tvGradeAndSchool);
            user = (User) ParseUser.getCurrentUser();
            this.onConversationListener = onConversationListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Conversation conversation) {
            User otherStudent;
            if (user.isInHighSchool()) {
                otherStudent = conversation.getCollegeStudent();
                tvGradeAndSchool.setText(String.format("%s at %s", otherStudent.getGrade(), otherStudent.getCollege()));
            } else {
                otherStudent = conversation.getHighSchoolStudent();
                tvGradeAndSchool.setText(String.format("%s at %s", otherStudent.getGrade(), otherStudent.getHighSchool()));
            }

            tvUsername.setText(otherStudent.getUsername());

            if (otherStudent.hasProfileImage()) {
                Glide.with(context)
                        .load(otherStudent.getProfileImageUrl())
                        .circleCrop()
                        .into(ivProfileImage);
            } else {
                // Display placeholder image if user has no profile image
                Glide.with(context)
                        .load(R.mipmap.profile_placeholder_foreground)
                        .circleCrop()
                        .into(ivProfileImage);
            }
        }

        @Override
        public void onClick(View v) {
            onConversationListener.onConversationClick(getAdapterPosition());
        }
    }

    public interface OnConversationListener {
        void onConversationClick(int position);
    }
}
