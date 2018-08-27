package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.ChecklistItem;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter extends  RecyclerView.Adapter<ChecklistAdapter.ViewHolder>{
    private Fragment fragment;
    private Context context;
    List<ChecklistItem> list;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setList(List<ChecklistItem> list) {
        this.list = list;
    }

    public ChecklistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_checklist_item, viewGroup, false);
        return new ChecklistAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.item = list.get(i);
        viewHolder.tvCheckListItem.setText(list.get(i).getContent());
        viewHolder.btnDeleteItem.setOnClickListener((v) -> {
            list.remove(viewHolder.item);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (list == null){
            list = new ArrayList<>();
        }
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        ChecklistItem item;
        TextView tvCheckListItem;
        ImageView btnDeleteItem;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvCheckListItem = (TextView) itemView.findViewById(R.id.tvCheckListItem);
            btnDeleteItem = (ImageView) itemView.findViewById(R.id.btnDeleteItem);
        }

    }
}
