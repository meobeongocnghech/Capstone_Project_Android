package com.viettrekker.mountaintrekkingadviser.controller.search;

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

public class SearchFragment extends Fragment {

    private RecyclerView rcvSearch;

    public RecyclerView getRcvSearch() {
        return rcvSearch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvSearch = (RecyclerView) view.findViewById(R.id.rcvSearch);
        rcvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
