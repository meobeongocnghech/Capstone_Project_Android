package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.R;

public class NewsFeedFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rcvNewsFeed = view.findViewById(R.id.rcvNewsFeed);
        rcvNewsFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        NewsFeedAdapter adapter = new NewsFeedAdapter();
        rcvNewsFeed.setAdapter(adapter);
    }
}
