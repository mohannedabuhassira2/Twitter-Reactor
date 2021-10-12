package com.example.commentsanalyzer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RVAllItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> allComments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rv_all_items, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null)
            allComments = bundle.getParcelableArrayList("all_comments_list");

        recyclerView = view.findViewById(R.id.all_comments_list);
        commentsAdapter = new CommentsAdapter(getContext(), allComments, allComments);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentsAdapter);
        return view;
    }
}
