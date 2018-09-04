package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanFragment extends Fragment {
    RecyclerView rcvPlanListItem;
    RecyclerView rcvPublicPlan;
    TextView tvMyPlan;
    TextView tvPublicPlan;
    PlanAdapter myPlanAdapter;
    PlanAdapter publicPlanAdapter;
    ProgressBar progressMyPlan;
    MaterialButton loadMoreMyPlan;
    NestedScrollView planScrollView;
    TextView tvNoPlan;
    int hasNoPlan = 0;

    public PlanFragment() {

    }

    public int getCurrentScrollY() {
        return planScrollView.getScrollY();
    }

    public void scrollToTop() {
        planScrollView.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100, 100, 0.5f, 5, 0, 1, 1, 0, 0));
        planScrollView.fling(0);
        planScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    public void initLoad() {
        hasNoPlan = 0;
        if (tvNoPlan != null) {
            tvNoPlan.setVisibility(View.GONE);
        }
        APIService mWebService = APIUtils.getWebService();
        myPlanAdapter.resetPageCount();
        mWebService.getListPlan(Session.getToken(getActivity()), 1, 4, "id").enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                List<Plan> plans = response.body();
                if (plans != null) {
                    plans.remove(0);
                    if (plans.size() > 0) {
                        if (plans.size() < 4) {
                            stopLoading(0);
                        } else {
                            showLoading(0);
                        }
                        tvMyPlan.setVisibility(View.VISIBLE);
                        rcvPlanListItem.setVisibility(View.VISIBLE);
                        myPlanAdapter.setListPlan(plans);
                        myPlanAdapter.notifyDataSetChanged();
                    } else {
                        hasNoPlan++;
                        if (hasNoPlan == 2) {
                            tvNoPlan.setVisibility(View.VISIBLE);
                        }
                        tvMyPlan.setVisibility(View.GONE);
                        rcvPlanListItem.setVisibility(View.GONE);
                        stopLoading(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {

            }
        });

        publicPlanAdapter.resetPageCount();
        mWebService.getListPlanIsPublic(Session.getToken(getActivity()), 1, 100, "id", true).enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                List<Plan> plans = response.body();
                if (plans != null) {
                    plans.remove(0);
                    if (plans.size() > 0) {
                        tvPublicPlan.setVisibility(View.VISIBLE);
                        rcvPublicPlan.setVisibility(View.VISIBLE);
                        int userId = Session.getUserId(getActivity());
                        outer: for (Iterator<Plan> iterator = plans.iterator(); iterator.hasNext(); ) {
                            Plan plan = iterator.next();
                            try {
                                if (DateTimeUtils.changeTimeToLocale(plan.getStartTime()).before(Calendar.getInstance().getTime())) {
                                    iterator.remove();
                                } else {
                                    for (Member mem : plan.getGroup().getMembers()) {
                                        if (mem.getUserId() == userId) {
                                            iterator.remove();
                                            break;
                                        }
                                    }
                                }
                            } catch (ParseException e) {

                            }
                        }

                        publicPlanAdapter.setListPlan(plans);
                        publicPlanAdapter.notifyDataSetChanged();
//
//                        if (plans.size() < 4) {
//                            stopLoading(1);
//                        } else {
//                            showLoading(1);
//                        }

                        if (plans.size() == 0) {
                            tvPublicPlan.setVisibility(View.GONE);
                            rcvPublicPlan.setVisibility(View.GONE);
                            stopLoading(1);
                        } else {
                            publicPlanAdapter.setListPlan(plans);
                            publicPlanAdapter.notifyDataSetChanged();
                        }
                    } else {
                        hasNoPlan++;
                        if (hasNoPlan == 2) {
                            tvNoPlan.setVisibility(View.VISIBLE);
                        }
                        tvPublicPlan.setVisibility(View.GONE);
                        rcvPublicPlan.setVisibility(View.GONE);
                        stopLoading(1);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressMyPlan = (ProgressBar) view.findViewById(R.id.progressMyPlan);
        loadMoreMyPlan = (MaterialButton) view.findViewById(R.id.loadMoreMyPlan);
        rcvPlanListItem = (RecyclerView) view.findViewById(R.id.rcvPlanListItem);
        rcvPublicPlan = (RecyclerView) view.findViewById(R.id.rcvPublicPlanListItem);
        planScrollView = (NestedScrollView) view.findViewById(R.id.planScrollView);
        tvMyPlan = (TextView) view.findViewById(R.id.tvMyPlan);
        tvPublicPlan = (TextView) view.findViewById(R.id.tvPublicPlan);
        tvNoPlan = (TextView) view.findViewById(R.id.tvNoPlan);

        rcvPlanListItem.setNestedScrollingEnabled(false);
        rcvPlanListItem.setLayoutManager(new LinearLayoutManager(view.getContext()));

        rcvPublicPlan.setNestedScrollingEnabled(false);
        rcvPublicPlan.setLayoutManager(new LinearLayoutManager(view.getContext()));

        myPlanAdapter = new PlanAdapter();
        myPlanAdapter.setContext(getContext());
        myPlanAdapter.setFragment(this);

        publicPlanAdapter = new PlanAdapter();
        publicPlanAdapter.setContext(getContext());
        publicPlanAdapter.setFragment(this);

        initLoad();
        rcvPlanListItem.setAdapter(myPlanAdapter);
        rcvPublicPlan.setAdapter(publicPlanAdapter);

        loadMoreMyPlan.setOnClickListener((v) -> {
            loadMoreMyPlan.setVisibility(View.GONE);
            myPlanAdapter.loadMoreMyPlan();
        });
    }

    public void stopLoading(int i) {
        if (i == 0) {
            loadMoreMyPlan.setVisibility(View.GONE);
            progressMyPlan.setVisibility(View.GONE);
        }
    }

    public void showLoading(int i) {
        if (i == 0) {
            loadMoreMyPlan.setVisibility(View.VISIBLE);
        }
    }
}
