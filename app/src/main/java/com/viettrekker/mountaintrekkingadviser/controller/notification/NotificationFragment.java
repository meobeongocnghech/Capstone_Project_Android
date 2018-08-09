package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.List;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {


    public NotificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rcvNotiItem = (RecyclerView) view.findViewById(R.id.rcvNotiItem);
        rcvNotiItem.setLayoutManager(new LinearLayoutManager(view.getContext()));
        NotificationAdapter notiAdapter = new NotificationAdapter();
        initLoad(notiAdapter);
        rcvNotiItem.setAdapter(notiAdapter);
    }

    private void initLoad(NotificationAdapter notiAdapter) {
        APIService mWebService = APIUtils.getWebService();
        mWebService.getNoti(MainActivity.user.getToken()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                List<Notification> list = response.body();
                if (list != null) {
                    notiAdapter.setOlderId(list.get(0).getOldestId());
                    list.remove(0);
                    notiAdapter.setListNoti(list);
                    notiAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(getContext(), "Xảy ra lỗi", Toast.LENGTH_LONG).show();
            }
        });
    }
}
