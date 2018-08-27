package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelinesCalendarAdapter extends RecyclerView.Adapter<TimelinesCalendarAdapter.ViewHolder>{
    private List<Date> date;
    private Fragment fragment;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setDate(List<Date> date) {
        this.date = date;
    }

    public TimelinesCalendarAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_timeline_calendar, viewGroup, false);
        return new TimelinesCalendarAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvDate.setText(DateTimeUtils.parseStringDay(date.get(i)));
        viewHolder.tvMonth.setText(DateTimeUtils.parseStringDayinWeek(date.get(i)));
    }

    @Override
    public int getItemCount() {
        if (date == null){
            date = new ArrayList<>();
        }
        return date.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView tvMonth;
        TextView tvDate;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvMonth = (TextView) itemView.findViewById(R.id.tvMonth);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }

    }
}
