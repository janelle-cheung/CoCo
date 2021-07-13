package com.example.collegeconnect.ui.conversations;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.adapters.ConversationsAdapter;
import com.example.collegeconnect.databinding.FragmentConversationsBinding;
import com.example.collegeconnect.databinding.FragmentSearchBinding;
import com.example.collegeconnect.models.Conversation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment {

    public static final String TAG = "ConversationsFragment";
    private ConversationsViewModel mViewModel;
    private FragmentConversationsBinding binding;
    ConversationsAdapter adapter;
    List<Conversation> conversations;
    ParseUser user;

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConversationsBinding.inflate(inflater, container, false);

        conversations = new ArrayList<>();
        user = ParseUser.getCurrentUser();

        // Set up recycler view and adapter
        adapter = new ConversationsAdapter(getContext(), conversations);
        binding.rvConversations.setAdapter(adapter);
        binding.rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConversations.addItemDecoration(new DividerItemDecoration(binding.rvConversations.getContext(), DividerItemDecoration.VERTICAL));

        queryConversations();

        return binding.getRoot();
    }

    private void queryConversations() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);

        if (user.getString(getString(R.string.KEY_TYPE)).equals("high school")) {
            query.include(Conversation.KEY_COLLEGE_STUDENT);
            query.whereEqualTo(Conversation.KEY_HIGHSCHOOL_STUDENT, user);
        } else {
            Log.e(TAG, "Colleger indeed");
            query.include(Conversation.KEY_HIGHSCHOOL_STUDENT);
            query.include(Conversation.KEY_COLLEGE_STUDENT);
            query.whereEqualTo(Conversation.KEY_COLLEGE_STUDENT, user);
        }

        // TO-DO: Make sure you change conversation updatedAt time
        query.addDescendingOrder("updatedAt");

        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem with querying conversations ", e);
                    return;
                }
                Log.i(TAG, "Success querying posts");

                adapter.clear();
                conversations.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}