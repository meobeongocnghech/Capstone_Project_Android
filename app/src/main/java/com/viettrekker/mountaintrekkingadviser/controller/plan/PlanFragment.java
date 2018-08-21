package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanFragment extends Fragment {
    RecyclerView rcvPlanListItem;
    PlanAdapter planAdapter;
    ProgressBar progressPlan;

    public PlanFragment() {

    }
    public int getCurrentScrollY() {
        return rcvPlanListItem.computeVerticalScrollOffset();
    }

    public void scrollToTop() {
        rcvPlanListItem.smoothScrollToPosition(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    public  void initLoad(){
        APIService mWebService = APIUtils.getWebService();
        showProgress();
        mWebService.getListPlan(Session.getToken(getActivity()), 1,10,"id").enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                List<Plan> plans = response.body();
                if (plans != null){
                    plans.remove(0);
                    planAdapter.setListPlan(plans);
                    planAdapter.notifyDataSetChanged();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi",Toast.LENGTH_LONG).show();
                hideProgress();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressPlan = (ProgressBar) view.findViewById(R.id.progressPlan);
        rcvPlanListItem = (RecyclerView) view.findViewById(R.id.rcvPlanListItem);
        rcvPlanListItem.setNestedScrollingEnabled(false);
        rcvPlanListItem.setLayoutManager(new LinearLayoutManager(view.getContext()));
        planAdapter = new PlanAdapter();
        planAdapter.setContext(getContext());
        planAdapter.setFragment(this);
        initLoad();
        rcvPlanListItem.setAdapter(planAdapter);
    }

    public void showProgress() {
        progressPlan.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressPlan.setVisibility(View.GONE);
    }
}
