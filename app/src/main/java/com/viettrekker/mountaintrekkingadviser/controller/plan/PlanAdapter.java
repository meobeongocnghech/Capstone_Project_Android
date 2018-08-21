package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.post.NewsFeedAdapter;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private Fragment fragment;
    private Context context;
    private List<Plan> listPlan;

    public PlanAdapter() {
    }

    public void setListPlan(List<Plan> listPlan) {
        this.listPlan = listPlan;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_plan_item, viewGroup, false);
        PlanAdapter.ViewHolder viewHolder = new PlanAdapter.ViewHolder(view, viewGroup.getContext());
        viewHolder.context = context;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        APIService mWebService = APIUtils.getWebService();
        Plan plan = listPlan.get(i);
        if (plan.getState() == 0){
            viewHolder.viewStateColorLeft.setBackground(context.getResources().getDrawable(R.color.colorPrimary));
            viewHolder.viewStateColorCircle.setBackground(context.getResources().getDrawable(R.color.colorPrimary));
            viewHolder.tvStatePlan.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.tvStatePlan.setText("Sẵn sàng");
        } else if (plan.getState() == 1){
            viewHolder.viewStateColorLeft.setBackground(context.getResources().getDrawable(R.color.colorOrange));
            viewHolder.viewStateColorCircle.setBackground(context.getResources().getDrawable(R.color.colorOrange));
            viewHolder.tvStatePlan.setTextColor(context.getResources().getColor(R.color.colorOrange));
            viewHolder.tvStatePlan.setText("Đang diễn ra");
        } else {
            viewHolder.viewStateColorLeft.setBackground(context.getResources().getDrawable(R.color.colorGray));
            viewHolder.viewStateColorCircle.setBackground(context.getResources().getDrawable(R.color.colorGray));
            viewHolder.tvStatePlan.setTextColor(context.getResources().getColor(R.color.colorGray));
            viewHolder.tvStatePlan.setText("Đã hoàn thành");
        }
        viewHolder.tvPlanName.setText(plan.getGroup().getName());
        try {
            viewHolder.tvStartPlan.setText(DateTimeUtils.parseStringDate(DateTimeUtils.changeTimeToLocale(plan.getStartTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PlanDetailActivity.class);
                intent.putExtra("id", plan.getId());
                intent.putExtra("token", MainActivity.user.getToken());
                intent.putExtra("userId", MainActivity.user.getId());
                context.startActivity(intent);
            }
        });
        mWebService.getPlaceById(MainActivity.user.getToken(), plan.getDirection().getPlaceId()).enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                viewHolder.tvStartLoc.setText(response.body() == null ? "" : response.body().getName());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {

            }
        });
        List<Member> members = plan.getGroup().getMembers();
        for (Member m: members) {
            if (m.getRoleInGroupId() == 1){
                mWebService.getUserById(MainActivity.user.getToken(),m.getUserId()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        viewHolder.tvPlanLead.setText(response.body().getFirstName() + " " + response.body().getLastName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
        viewHolder.tvMembers.setText(members.size() + " người");
        try {
            Date d1 = DateTimeUtils.changeTimeToLocale(plan.getStartTime());
            Date d2 = DateTimeUtils.changeTimeToLocale(plan.getFinishTime());
            viewHolder.tvDuration.setText(DateTimeUtils.caculatorStringTime(d1,d2));
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
       if (listPlan == null ){
           listPlan = new ArrayList<>();
       }
       return listPlan.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context;
        View viewStateColorLeft;
        TextView tvPlanName;
        View viewStateColorCircle;
        TextView tvStatePlan;
        TextView tvStartPlan;
        TextView tvStartLoc;
        TextView tvPlanLead;
        TextView tvDuration;
        TextView tvMembers;
        CardView cardView;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            viewStateColorLeft = (View) itemView.findViewById(R.id.viewStateColorLeft);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
            viewStateColorCircle = (View) itemView.findViewById(R.id.viewStateColorCircle);
            tvStatePlan = (TextView) itemView.findViewById(R.id.tvStatePlan);
            tvStartLoc = (TextView) itemView.findViewById(R.id.tvStartLoc);
            tvPlanLead = (TextView) itemView.findViewById(R.id.tvPlanLead);
            tvStartPlan = (TextView) itemView.findViewById(R.id.tvStartPlan);
            tvMembers = (TextView) itemView.findViewById(R.id.tvMembers);
            tvDuration = (TextView) itemView.findViewById(R.id.tvDuration);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

        @Override
        public void onClick(View view) {

        }
    }

}