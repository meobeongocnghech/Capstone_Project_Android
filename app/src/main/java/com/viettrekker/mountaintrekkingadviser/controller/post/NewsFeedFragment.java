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
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedFragment extends Fragment {
    private boolean isByUserId = false;
    private int userId;
    private boolean loading = true;
    private NewsFeedAdapter adapter;
    private String token;

    public void setByUserId(boolean byUserId) {
        isByUserId = byUserId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        token = Session.getToken(getActivity());
        RecyclerView rcvNewsFeed = view.findViewById(R.id.rcvNewsFeed);
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        rcvNewsFeed.setLayoutManager(mLayoutManager);
        adapter = new NewsFeedAdapter(getContext(), this);
        initLoad(adapter);
        rcvNewsFeed.setAdapter(adapter);
    }

    public void incrementalLoad(){
        adapter.incrementalLoad();
    }

    public void notifyChanged(){
        adapter.notifyDataSetChanged();
    }

    private void initLoad(NewsFeedAdapter newsFeedAdapter){
        APIService mWebService = APIUtils.getWebService();
        if (isByUserId) {
            newsFeedAdapter.setByUserId(isByUserId);
            newsFeedAdapter.setUserId(userId);
            mWebService.getPostPageByUserId(token, userId, 1,"DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        newsFeedAdapter.setListPost(list);
                        newsFeedAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(getContext(), "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mWebService.getPostPage(token,1,5,"id", "DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        list.remove(0);
                        newsFeedAdapter.setListPost(list);
                        newsFeedAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(getContext(), "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
