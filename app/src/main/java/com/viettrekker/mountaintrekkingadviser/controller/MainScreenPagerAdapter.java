package com.viettrekker.mountaintrekkingadviser.controller;

import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationFragment;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainScreenPagerAdapter extends FragmentPagerAdapter {

    public MainScreenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return position == 0 ? new PostFragment() : new NotificationFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }
}
