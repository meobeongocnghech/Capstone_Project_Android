package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.R;

import androidx.fragment.app.Fragment;

public class NotificationFragment extends Fragment {


    public NotificationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
}
