package com.example.collegeconnect.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collegeconnect.R;
import com.example.collegeconnect.activities.ConversationActivity;
import com.example.collegeconnect.adapters.ConversationsAdapter;
import com.example.collegeconnect.databinding.FragmentConversationsBinding;
import com.example.collegeconnect.databinding.FragmentSearchBinding;
import com.example.collegeconnect.models.Conversation;
import com.example.collegeconnect.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment implements ConversationsAdapter.OnConversationListener {

    public static final String TAG = "ConversationsFragment";
    public static final String KEY_CONVERSATION_1 = "conversation1";
    private FragmentConversationsBinding binding;
    private ConversationsAdapter adapter;
    private List<Conversation> conversations;
    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConversationsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();

        conversations = new ArrayList<>();
        user = (User) ParseUser.getCurrentUser();

        // Set up recycler view and adapter
        adapter = new ConversationsAdapter(getContext(), conversations, this, user.isInHighSchool());
        binding.rvConversations.setAdapter(adapter);
        binding.rvConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConversations.addItemDecoration(new DividerItemDecoration(binding.rvConversations.getContext(), DividerItemDecoration.VERTICAL));

        // Set up search edittext
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        queryConversations();

        return binding.getRoot();
    }

    private void queryConversations() {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);

        // If user is in high school, find the college students that the user is talking to
        if (user.isInHighSchool()) {
            query.include(Conversation.KEY_COLLEGE_STUDENT);
            query.whereEqualTo(Conversation.KEY_HIGHSCHOOL_STUDENT, user);
        } else {
        // If user is in college, find the high school students that the user is talking to
            query.include(Conversation.KEY_HIGHSCHOOL_STUDENT);
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
                Log.i(TAG, "Success querying conversations");

                adapter.clear();
                conversations.addAll(objects);
                adapter.setAllConversationsForFilter(conversations);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onConversationClick(int position) {
        Conversation conversation = conversations.get(position);
        Intent i = new Intent(getContext(), ConversationActivity.class);
        i.putExtra(KEY_CONVERSATION_1, Parcels.wrap(conversation));
        startActivity(i);
    }
}