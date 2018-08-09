package com.viettrekker.mountaintrekkingadviser.controller;

import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
