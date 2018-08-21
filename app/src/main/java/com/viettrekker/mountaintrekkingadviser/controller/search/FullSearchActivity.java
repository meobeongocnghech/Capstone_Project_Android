package com.viettrekker.mountaintrekkingadviser.controller.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullSearchActivity extends AppCompatActivity {

    private int size;
    private RecyclerView rcv;
    private MaterialButton loadMore;
    private ProgressBar progress;
    private TextView description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String type = getIntent().getStringExtra("type");
        String token = getIntent().getStringExtra("token");
        String query = getIntent().getStringExtra("query");

        description = (TextView) findViewById(R.id.tvSearchDescription);

        size = 0;

        loadData(type, token, query);

        rcv = (RecyclerView) findViewById(R.id.rcvFullSearch);
        loadMore = (MaterialButton) findViewById(R.id.btnLoadMore);
        progress = (ProgressBar) findViewById(R.id.progressLoadMore);

        loadMore.setOnClickListener((v) -> {
            loadMore.setVisibility(View.GONE);
            loadData(type, token, query);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadData(String type, String token, String query) {
        size = size + 5;
        if (type.equalsIgnoreCase("user")) {
            if (size == 5) {
                String text = "Kết quả tìm kiếm người dùng cho: <b>" + query + "</b>";
                description.setText(Html.fromHtml(text));
            }
            APIService mWebService = APIUtils.getWebService();
            mWebService.searchUserSuggestion(token, 1, size, query).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    List<User> users = response.body();
                    users.remove(0);
                    SearchResultAdapter adapter = new SearchResultAdapter();
                    adapter.setUser(true);
                    adapter.setPlace(false);
                    adapter.setPost(false);
                    adapter.setUsers(users);
                    adapter.setToken(token);
                    adapter.setUserId(Session.getUserId(FullSearchActivity.this));
                    adapter.setContext(getBaseContext());
                    rcv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    rcv.setAdapter(adapter);
                    if (users.size() < 5) {
                        loadMore.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                    } else {
                        loadMore.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {

                }
            });
        } else if (type.equalsIgnoreCase("place")) {
            if (size == 5) {
                String text = "Kết quả tìm kiếm địa điểm cho: <b>" + query + "</b>";
                description.setText(Html.fromHtml(text));
            }
            APIService mWebService = APIUtils.getWebService();
            mWebService.searchPlaceSuggestion(token, 1, size, query).enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    List<Place> places = response.body();
                    places.remove(0);
                    SearchResultAdapter adapter = new SearchResultAdapter();
                    adapter.setUser(false);
                    adapter.setPlace(true);
                    adapter.setPost(false);
                    adapter.setToken(token);
                    adapter.setUserId(Session.getUserId(FullSearchActivity.this));
                    adapter.setPlaces(places);
                    adapter.setContext(getBaseContext());
                    rcv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    rcv.setAdapter(adapter);
                    if (places.size() < 5) {
                        loadMore.setVisibility(View.GONE);
                        progress.setVisibility(View.GONE);
                    } else {
                        loadMore.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {

                }
            });

        } else if (type.equalsIgnoreCase("post")) {
            if (size == 5) {
                String text = "Kết quả tìm kiếm bài đăng cho: <b>" + query + "</b>";
                description.setText(Html.fromHtml(text));
            }
            APIService mWebService = APIUtils.getWebService();
            mWebService.searchPostSuggestion(token, 1, size, "created_at", "DESC", query)
                    .enqueue(new Callback<List<Post>>() {
                        @Override
                        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                            List<Post> posts = response.body();
                            posts.remove(0);
                            SearchResultAdapter adapter = new SearchResultAdapter();
                            adapter.setUser(false);
                            adapter.setPlace(false);
                            adapter.setPost(true);
                            adapter.setToken(token);
                            adapter.setUserId(Session.getUserId(FullSearchActivity.this));
                            adapter.setPosts(posts);
                            adapter.setContext(getBaseContext());
                            rcv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                            rcv.setAdapter(adapter);
                            if (posts.size() < 5) {
                                loadMore.setVisibility(View.GONE);
                                progress.setVisibility(View.GONE);
                            } else {
                                loadMore.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Post>> call, Throwable t) {

                        }
                    });
        }
    }
}
