package com.viettrekker.mountaintrekkingadviser.controller.post;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PlaceViewPagerAdapter extends FragmentPagerAdapter {
    public PlaceViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        PlacePageLayoutFragment fragment = new PlacePageLayoutFragment();
        fragment.setPosition(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return 10;
    }
}
