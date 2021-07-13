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

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvGradeAndSchool;
        ParseUser user;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvGradeAndSchool = itemView.findViewById(R.id.tvGradeAndSchool);
            user = ParseUser.getCurrentUser();
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
    }
}
