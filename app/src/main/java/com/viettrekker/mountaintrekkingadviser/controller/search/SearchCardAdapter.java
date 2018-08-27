package com.viettrekker.mountaintrekkingadviser.controller.search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;

import java.util.List;

public class SearchCardAdapter extends RecyclerView.Adapter<SearchCardAdapter.ViewHolder> {
    private List<User> users;
    private List<Place> places;
    private List<Post> posts;
    private boolean isUser = false;
    private boolean isPlace = false;
    private boolean isPost = false;
    private String token;
    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String query;

    public void setQuery(String query) {
        this.query = query;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        if (users != null && !users.isEmpty()) {
            isUser = true;
        }
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
        if (places != null && !places.isEmpty()) {
            isPlace = true;
        }
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        if (posts != null && !posts.isEmpty()) {
            isPost = true;
        }
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public void setPlace(boolean place) {
        isPlace = place;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_card, viewGroup, false);
        return new SearchCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (isUser) {
            viewHolder.tvSearchTitle.setText("Tài khoản");
            SearchResultAdapter adapter = new SearchResultAdapter();
            adapter.setUser(true);
            adapter.setPlace(false);
            adapter.setPost(false);
            adapter.setToken(token);
            adapter.setUserId(userId);
            if (users.size() <= 3) viewHolder.btnViewAll.setVisibility(View.GONE);
            if (users.size() > 3) users.remove(users.size() - 1);
            adapter.setUsers(users);
            adapter.setContext(context);
            viewHolder.rcvSearchItem.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.rcvSearchItem.setAdapter(adapter);
            viewHolder.btnViewAll.setOnClickListener((v) -> {
                Intent intent = new Intent(context, FullSearchActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("type", "user");
                intent.putExtra("query", query);
                context.startActivity(intent);
            });
            isUser = false;
        }
        else if (isPlace) {
            viewHolder.tvSearchTitle.setText("Địa điểm");
            SearchResultAdapter adapter = new SearchResultAdapter();
            adapter.setUser(false);
            adapter.setPlace(true);
            adapter.setPost(false);
            if (places.size() <= 3) viewHolder.btnViewAll.setVisibility(View.GONE);
            if (places.size() > 3) places.remove(places.size() - 1);
            adapter.setPlaces(places);
            adapter.setContext(context);
            viewHolder.rcvSearchItem.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.rcvSearchItem.setAdapter(adapter);
            viewHolder.btnViewAll.setOnClickListener((v) -> {
                Intent intent = new Intent(context, FullSearchActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("type", "place");
                intent.putExtra("query", query);
                context.startActivity(intent);
            });
            isPlace = false;
        }
        else if (isPost) {
            viewHolder.tvSearchTitle.setText("Bài viết");
            SearchResultAdapter adapter = new SearchResultAdapter();
            adapter.setUser(false);
            adapter.setPlace(false);
            adapter.setPost(true);
            if (posts.size() <= 3) viewHolder.btnViewAll.setVisibility(View.GONE);
            if (posts.size() > 3) posts.remove(posts.size() - 1);
            adapter.setPosts(posts);
            adapter.setContext(context);
            viewHolder.rcvSearchItem.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.rcvSearchItem.setAdapter(adapter);
            viewHolder.btnViewAll.setOnClickListener((v) -> {
                Intent intent = new Intent(context, FullSearchActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("type", "post");
                intent.putExtra("query", query);
                context.startActivity(intent);
            });
            isPost = false;
        }
    }

    @Override
    public int getItemCount() {
        return (users.isEmpty() ? 0 : 1) + (places.isEmpty() ? 0 : 1) + (posts.isEmpty() ? 0 : 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSearchTitle;
        RecyclerView rcvSearchItem;
        MaterialButton btnViewAll;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearchTitle = (TextView) itemView.findViewById(R.id.tvSearchTitle);
            rcvSearchItem = (RecyclerView) itemView.findViewById(R.id.rcvSearchItem);
            btnViewAll = (MaterialButton) itemView.findViewById(R.id.btnViewAll);
        }
    }
}
