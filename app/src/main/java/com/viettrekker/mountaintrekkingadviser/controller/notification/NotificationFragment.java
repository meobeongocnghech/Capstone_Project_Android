package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
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

    public int getCountNew() {
        return countNew;
    }

    private ProgressBar progress;

    public NotificationFragment() {

    }

    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    public void stopProgress() {
        progress.setVisibility(View.GONE);
    }

    public void setNotiCount(MaterialButton notiCount) {
        this.notiCount = notiCount;
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
        rcvNotiItem.setLayoutManager(new LinearLayoutManager(view.getContext()));
        notiAdapter = new NotificationAdapter();
        notiAdapter.setContext(getContext());
        notiAdapter.setFragment(this);
        initLoad();
        rcvNotiItem.setAdapter(notiAdapter);

        progress = (ProgressBar) view.findViewById(R.id.progressNoti);
    }

    public int getCurrentScrollY() {
        return rcvNotiItem.computeVerticalScrollOffset();
    }

    public void scrollToTop() {
        rcvNotiItem.smoothScrollToPosition(0);
    }

    public void initLoad() {
        APIService mWebService = APIUtils.getWebService();
        mWebService.getNoti(Session.getToken(getActivity())).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                List<Notification> list = response.body();
                if (list != null) {
                    notiAdapter.setOlderId(list.get(0).getOldestId());
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
                    list.remove(0);
                    notiAdapter.setListNoti(list);
                    notiAdapter.notifyDataSetChanged();
                    stopProgress();
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
        mWebService.setCheckAll(Session.getToken(getActivity()), true).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {

            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {

            }
        });
    }
}
