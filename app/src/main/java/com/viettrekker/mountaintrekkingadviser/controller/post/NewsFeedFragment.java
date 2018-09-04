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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.Post;
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
    private LinearLayoutManager mLayoutManager;
    private TextView tvMessage;
    private ProgressBar progressBar;

    public void setByUserId(boolean byUserId) {
        isByUserId = byUserId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NewsFeedAdapter getAdapter() {
        return adapter;
    }

    public void setTvMessage(TextView tvMessage) {
        this.tvMessage = tvMessage;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rcvNewsFeed = view.findViewById(R.id.rcvNewsFeed);
        tvMessage = (TextView) view.findViewById(R.id.tvNoMore);
        progressBar = (ProgressBar) view.findViewById(R.id.progressPost);
        mLayoutManager = new LinearLayoutManager(getContext());
        rcvNewsFeed.setLayoutManager(mLayoutManager);
        adapter = new NewsFeedAdapter(getContext(), this, token);
        adapter.setTvMessage(tvMessage);
        adapter.setProgressBar(progressBar);
        initLoad(adapter);
        rcvNewsFeed.setAdapter(adapter);
        rcvNewsFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        adapter.incrementalLoad();
                    }
                }
            }
        });
    }

    public LinearLayoutManager getmLayoutManager() {
        return mLayoutManager;
    }

    public void incrementalLoad() {
        adapter.incrementalLoad();
    }

    public void notifyChanged() {
        adapter.notifyDataSetChanged();
    }

    private void initLoad(NewsFeedAdapter newsFeedAdapter) {
        APIService mWebService = APIUtils.getWebService();
        if (isByUserId) {
            progressBar.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
            newsFeedAdapter.setByUserId(isByUserId);
            newsFeedAdapter.setUserId(userId);
            mWebService.getPostPageByUserId(token, userId, 1, "DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    List<Post> finalList = new ArrayList<>();
                    if (list != null && !list.isEmpty()) {
                        for (Post post : list) {
                            if (post.getState() != 1 && post.getTypeId() != 2) {
                                finalList.add(post);
                            }
                        }

                        if (finalList.size() == 0) {
                            tvMessage.setVisibility(View.VISIBLE);
                            tvMessage.setText("Không có bài đăng nào");
                        }
                        newsFeedAdapter.setListPost(finalList);
                        newsFeedAdapter.notifyDataSetChanged();
                    } else {
                        tvMessage.setVisibility(View.VISIBLE);
                        tvMessage.setText("Không có bài đăng nào");
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(getContext(), "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            newsFeedAdapter.setUserId(userId);
            mWebService.getPostPage(token, 1, 5, "id", "DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    List<Post> finalList = new ArrayList<>();
                    if (list != null && !list.isEmpty()) {
                        list.remove(0);
                        for (Post post : list) {
                            if (post.getState() != 1 && post.getTypeId() != 2) {
                                finalList.add(post);
                            }
                        }
                        if (list.size() < 5) {
                            tvMessage.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        if (list.size() == 0) {
                            tvMessage.setText("Không có bài đăng nào");
                        }
                        newsFeedAdapter.setListPost(finalList);
                        newsFeedAdapter.notifyDataSetChanged();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        tvMessage.setText("Không có bài đăng nào");
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
