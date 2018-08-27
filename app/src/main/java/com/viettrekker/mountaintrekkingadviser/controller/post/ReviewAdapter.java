package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.GuideViewHolder> {

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_item, parent, false);
        return new ReviewAdapter.GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class GuideViewHolder extends RecyclerView.ViewHolder {
        public GuideViewHolder(View itemView) {
            super(itemView);
        }
    }
}
