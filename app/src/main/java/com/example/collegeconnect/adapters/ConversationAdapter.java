package com.example.collegeconnect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeconnect.R;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.Message;
import com.example.collegeconnect.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MessageViewHolder> {

    public static final String TAG = "ConversationAdapter";
    private List<Message> messages;
    private Context context;
    User user;
    User otherUser;

    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;

    public ConversationAdapter(Context context, User user, User otherUser, List<Message> messages) {
        this.messages = messages;
        this.context = context;
        this.user = user;
        this.otherUser = otherUser;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View view = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(view);
        } else if (viewType == MESSAGE_OUTGOING) {
            View view = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(view);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = messages.get(position);
        return message.getSender() != null && message.getSender().getObjectId().equals(user.getObjectId());
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Message message) {

        }
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {

        TextView tvBody;
        ImageView ivProfileOther;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfileOther = itemView.findViewById(R.id.ivProfileOther);
        }

        public void bind(Message message) {
            tvBody.setText(message.getBody());
            if (otherUser.hasProfileImage()) {
                Glide.with(context)
                        .load(otherUser.getProfileImageUrl())
                        .placeholder(R.mipmap.profile_placeholder_foreground)
                        .circleCrop()
                        .into(ivProfileOther);
            } else {
                // Display placeholder image if user has no profile image
                Glide.with(context)
                        .load(R.mipmap.profile_placeholder_foreground)
                        .circleCrop()
                        .into(ivProfileOther);
            }
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {

        TextView tvBody;
        ImageView ivProfileMe;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfileMe = itemView.findViewById(R.id.ivProfileMe);
        }

        public void bind(Message message) {
            tvBody.setText(message.getBody());

            if (user.hasProfileImage()) {
                Glide.with(context)
                        .load(user.getProfileImageUrl())
                        .circleCrop()
                        .into(ivProfileMe);
            } else {
                // Display placeholder image if user has no profile image
                Glide.with(context)
                        .load(R.mipmap.profile_placeholder_foreground)
                        .circleCrop()
                        .into(ivProfileMe);
            }
        }
    }
}
