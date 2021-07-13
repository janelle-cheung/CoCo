package com.example.collegeconnect.ui.conversations;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeconnect.R;
import com.example.collegeconnect.databinding.FragmentConversationsBinding;
import com.example.collegeconnect.databinding.FragmentSearchBinding;

public class ConversationsFragment extends Fragment {

    private ConversationsViewModel mViewModel;
    private FragmentConversationsBinding binding;

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConversationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}