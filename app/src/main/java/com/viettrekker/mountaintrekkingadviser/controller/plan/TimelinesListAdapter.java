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
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelinesListAdapter extends RecyclerView.Adapter<TimelinesListAdapter.ViewHolder>{
    private Fragment fragment;
    private Context context;
    private List<TimeLines> list;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setList(List<TimeLines> list) {
        this.list = list;
    }

    public TimelinesListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_timeline_item, viewGroup, false);
        return new TimelinesListAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TimeLines timeLines = list.get(i);
        //TODO compare date
        viewHolder.tvTlTitle.setText(timeLines.getName());
        viewHolder.tvTlContent.setText(timeLines.getContent());
        try {
            Date newDate = DateTimeUtils.changeTimeToLocale(timeLines.getTime());
            viewHolder.tvTimeInDetail.setText(DateTimeUtils.parseStringTime(newDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.imgEditTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO edit timeline here
            }
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
        ImageView imgCircleState;
        TextView tvTlTitle;
        TextView tvTlContent;
        TextView tvTimeInDetail;
        ImageView imgEditTimeline;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imgCircleState = (ImageView) itemView.findViewById(R.id.imgCircleState);
            tvTlTitle = (TextView) itemView.findViewById(R.id.tvTlTitle);
            tvTlContent = (TextView) itemView.findViewById(R.id.tvTlContent);
            tvTimeInDetail = (TextView) itemView.findViewById(R.id.tvTimeInDetail);
            imgEditTimeline = (ImageView) itemView.findViewById(R.id.imgEditTimeline);
        }

    }
}
