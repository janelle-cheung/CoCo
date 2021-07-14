package com.example.collegeconnect.adapters;

import android.content.ClipData;
import android.content.Context;
import android.media.Image;
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
import com.example.collegeconnect.models.Conversation;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    Context context;
    List<Conversation> conversations;
    OnConversationListener onConversationListener;

    public ConversationsAdapter(Context context, List<Conversation> conversations, OnConversationListener onConversationListener) {
        this.context = context;
        this.conversations = conversations;
        this.onConversationListener = onConversationListener;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvGradeAndSchool;
        ParseUser user;
        OnConversationListener onConversationListener;

        public ViewHolder(View itemView, OnConversationListener onConversationListener) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvGradeAndSchool = itemView.findViewById(R.id.tvGradeAndSchool);
            user = ParseUser.getCurrentUser();
            this.onConversationListener = onConversationListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Conversation conversation) {
            ParseUser otherStudent;
            if (user.getString("type").equals("high school")) {
                otherStudent = conversation.getCollegeStudent();
            } else {
                otherStudent = conversation.getHighSchoolStudent();
            }

            tvUsername.setText(otherStudent.getUsername());
            tvGradeAndSchool.setText(String.format("%s at %s", otherStudent.getString("grade"), otherStudent.getString("school")));

            // Display placeholder image if user has no profile image
            ParseFile profileImage = otherStudent.getParseFile("profileImage");
            if (profileImage == null) {
                Glide.with(context)
                        .load(R.mipmap.profile_placeholder_foreground)
                        .circleCrop()
                        .into(ivProfileImage);;
            } else {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .placeholder(R.mipmap.profile_placeholder_foreground)
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
