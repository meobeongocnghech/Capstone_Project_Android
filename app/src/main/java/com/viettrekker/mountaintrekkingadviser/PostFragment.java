package com.viettrekker.mountaintrekkingadviser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostFragment extends Fragment {


    public PostFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.postRcl);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        PostAdapter adapter = new PostAdapter(view.getContext());
        recyclerView.setAdapter(adapter);

        return inflater.inflate(R.layout.fragment_post, container, false);
    }

}
