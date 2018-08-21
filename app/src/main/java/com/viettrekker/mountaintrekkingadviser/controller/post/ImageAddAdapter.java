package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.plan.ChecklistAdapter;
import com.viettrekker.mountaintrekkingadviser.model.ChecklistItem;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;

import java.util.ArrayList;
import java.util.List;

public class ImageAddAdapter extends RecyclerView.Adapter<ImageAddAdapter.ViewHolder>{

    private List<String> listImg;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListImg(List<String> listImg) {
        this.listImg = listImg;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_image_add_item, viewGroup, false);
        return new ImageAddAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GlideApp.with(context)
                        .load(listImg.get(i))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .centerCrop()
                        .override(LocalDisplay.dp2px(80, context)).into(viewHolder.imgAdd);
    }

    @Override
    public int getItemCount() {
        if (listImg == null){
            listImg = new ArrayList<>();
        }
        return listImg.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        ImageView imgAdd;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imgAdd = (ImageView) itemView.findViewById(R.id.imgAdd);
        }

    }
}
