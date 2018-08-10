package com.viettrekker.mountaintrekkingadviser.controller;

import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
public class MainScreenPagerAdapter extends FragmentPagerAdapter {

    private PostFragment postFragment;

    public MainScreenPagerAdapter(FragmentManager fm) {
        super(fm);
        postFragment = new PostFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return postFragment;
            default :
                return new NotificationFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
