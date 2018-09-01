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
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private Fragment fragment;
    private Context context;
    private List<Plan> listPlan;
    private String token;
    private int pageMyPlan = 1;
    private int pagePublicPlan = 1;

    public PlanAdapter() {
    }

    public void resetPageCount() {
        pageMyPlan = 1;
        pagePublicPlan = 1;
    }

    public void setListPlan(List<Plan> listPlan) {
        this.listPlan = listPlan;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        token = Session.getToken(((PlanFragment) fragment).getActivity());
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
        boolean reqFlag = false;

        viewHolder.tvRequestJoin.setVisibility(View.GONE);
        List<Member> mem = plan.getGroup().getMembers();
        for (Member me: mem) {
            if (me.getUserId() == Session.getUserId(context)){
                reqFlag = true;
            }
        }
        if (!reqFlag)
            viewHolder.tvRequestJoin.setVisibility(View.VISIBLE);

        viewHolder.tvRequestJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWebService.requestJoin(token, plan.getId()).enqueue(new Callback<Plan>() {
                    @Override
                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                        if (response.code() == 200){
                            Intent intent = new Intent(context,PlanDetailActivity.class);
                            intent.putExtra("id", plan.getId());
                            intent.putExtra("token", token);
                            intent.putExtra("userId", Session.getUserId(((PlanFragment) fragment).getActivity()));
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context,"Thao tác không thành công, vui lòng thử lại sau", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Plan> call, Throwable t) {
                        Toast.makeText(context,"Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        try {
            if (DateTimeUtils.changeTimeToLocale(plan.getFinishTime()).before(Calendar.getInstance().getTime())){
                viewHolder.viewStateColorLeft.setBackground(context.getResources().getDrawable(R.color.colorGray));
                viewHolder.viewStateColorCircle.setBackground(context.getResources().getDrawable(R.color.colorGray));
                viewHolder.tvStatePlan.setTextColor(context.getResources().getColor(R.color.colorGray));
                viewHolder.tvStatePlan.setText("Đã hoàn thành");
            } else if (DateTimeUtils.changeTimeToLocale(plan.getStartTime()).before(Calendar.getInstance().getTime())
                    && DateTimeUtils.changeTimeToLocale(plan.getFinishTime()).after(Calendar.getInstance().getTime())) {
                viewHolder.viewStateColorLeft.setBackground(context.getResources().getDrawable(R.color.colorOrange));
                viewHolder.viewStateColorCircle.setBackground(context.getResources().getDrawable(R.color.colorOrange));
                viewHolder.tvStatePlan.setTextColor(context.getResources().getColor(R.color.colorOrange));
                viewHolder.tvStatePlan.setText("Đang diễn ra");
            } else {
                viewHolder.viewStateColorLeft.setBackground(context.getResources().getDrawable(R.color.colorPrimary));
                viewHolder.viewStateColorCircle.setBackground(context.getResources().getDrawable(R.color.colorPrimary));
                viewHolder.tvStatePlan.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                viewHolder.tvStatePlan.setText("Sẵn sàng");
            }
        } catch (ParseException e) {

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
                intent.putExtra("token", token);
                intent.putExtra("userId", Session.getUserId(((PlanFragment) fragment).getActivity()));
                ((PlanFragment) fragment).getActivity().startActivityForResult(intent, 10);
            }
        });
        mWebService.getPlaceById(token, plan.getDirection().getPlaceId()).enqueue(new Callback<Place>() {
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
                mWebService.getUserById(token, m.getUserId()).enqueue(new Callback<User>() {
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

    public void loadMoreMyPlan() {
        pageMyPlan++;
        APIService mWebService = APIUtils.getWebService();
        mWebService.getListPlan(token, pageMyPlan,4,"id").enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                List<Plan> plans = response.body();
                if (plans != null){
                    if (plans.size() < 4) {
                        ((PlanFragment) fragment).stopLoading(0);
                    } else {
                        ((PlanFragment) fragment).showLoading(0);
                    }
                    plans.remove(0);
                    int lastSize = listPlan.size();
                    for (Plan plan : plans) {
                        listPlan.add(plan);
                    }
                    notifyItemRangeChanged(lastSize, plans.size());
                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadMorePublicPlan() {
        pagePublicPlan++;
        APIService mWebService = APIUtils.getWebService();
        mWebService.getListPlanIsPublic(token, pagePublicPlan,4,"id", true).enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                List<Plan> plans = response.body();
                if (plans != null){
                    plans.remove(0);
                    int lastSize = listPlan.size();
                    int userId = Session.getUserId(((PlanFragment) fragment).getActivity());
                    for (Iterator<Plan> iterator = plans.iterator(); iterator.hasNext(); ) {
                        Plan plan = iterator.next();
                        for (Member mem : plan.getGroup().getMembers()) {
                            if (mem.getUserId() == userId) {
                                iterator.remove();
                            }
                        }
                    }

                    for (Plan plan : plans) {
                        listPlan.add(plan);
                    }
                    if (plans.size() < 4) {
                        ((PlanFragment) fragment).stopLoading(1);
                    } else {
                        ((PlanFragment) fragment).showLoading(1);
                    }
                    notifyItemRangeChanged(lastSize, plans.size());
                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        });
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
        TextView tvRequestJoin;
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
            tvRequestJoin = (TextView) itemView.findViewById(R.id.tvRequestJoin);
            tvMembers = (TextView) itemView.findViewById(R.id.tvMembers);
            tvDuration = (TextView) itemView.findViewById(R.id.tvDuration);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

        @Override
        public void onClick(View view) {

        }
    }

}
