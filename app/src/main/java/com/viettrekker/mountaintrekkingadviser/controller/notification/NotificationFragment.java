package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.content.Intent;
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
import com.viettrekker.mountaintrekkingadviser.controller.LoginActivity;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    RecyclerView rcvNotiItem;
    NotificationAdapter notiAdapter;
    private int countNew;
    MaterialButton notiCount;
    private int newestId;
    private NestedScrollView layout;
    private TextView tvNoNoti;

    public int getCountNew() {
        return countNew;
    }

    private ProgressBar progress;

    public NotificationFragment() {

    }

    private String token;

    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    public void stopProgress() {
        progress.setVisibility(View.GONE);
    }

    public void setNotiCount(MaterialButton notiCount) {
        this.notiCount = notiCount;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvNotiItem = (RecyclerView) view.findViewById(R.id.rcvNotiItem);
        layout = (NestedScrollView) view.findViewById(R.id.notiSrollView);
        tvNoNoti = (TextView) view.findViewById(R.id.tvNoNoti);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rcvNotiItem.setLayoutManager(layoutManager);
        notiAdapter = new NotificationAdapter();
        notiAdapter.setContext(getContext());
        notiAdapter.setFragment(this);
        initLoad();
        rcvNotiItem.setAdapter(notiAdapter);

        progress = (ProgressBar) view.findViewById(R.id.progressNoti);

        layout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (i1 > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        tvNoNoti.setVisibility(View.GONE);
                        notiAdapter.incrementalLoad();
                    }
                }
            }
        });
    }

    public int getCurrentScrollY() {
        return layout.getScrollY();
    }

    public void scrollToTop() {
        layout.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100, 100, 0.5f, 5, 0, 1, 1, 0, 0));
        layout.fling(0);
        layout.smoothScrollTo(0, 0);
    }

    public void initLoad() {
        APIService mWebService = APIUtils.getWebService();
        if (tvNoNoti != null) {
            tvNoNoti.setVisibility(View.GONE);
        }

//        if (progress != null) {
//            progress.setVisibility(View.VISIBLE);
//        }
        mWebService.getNoti(token).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                List<Notification> list = response.body();
                if (list != null) {
                    newestId = list.get(0).getNewestId();
                    countNew = list.get(0).getCountNew();
                    if (countNew > 0 && countNew <= 9) {
                        notiCount.setText(countNew + "");
                        notiCount.setVisibility(View.VISIBLE);
                    } else if (countNew > 9) {
                        notiCount.setText(countNew + "+");
                        notiCount.setVisibility(View.VISIBLE);
                    } else {
                        notiCount.setVisibility(View.GONE);
                    }
                    if (notiAdapter != null) {
                        notiAdapter.setOlderId(list.get(0).getOldestId());
                        list.remove(0);
                        if (list.get(0).getTypeId() == 7) {
                            Session.clearSession(getActivity());
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.putExtra("ban", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        if (list.size() > 0) {
                            notiAdapter.setListNoti(list);
                            notiAdapter.notifyDataSetChanged();
                        } else {
                            tvNoNoti.setVisibility(View.VISIBLE);
                        }
                        stopProgress();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(getContext(), "Xảy ra lỗi", Toast.LENGTH_LONG).show();
                stopProgress();
            }
        });
    }

    public void setAllCheck() {
        APIService mWebService = APIUtils.getWebService();
        mWebService.setCheckAll(token, true, newestId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.code() == 200){
                    List<Notification> lists = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {

            }
        });
    }
}
