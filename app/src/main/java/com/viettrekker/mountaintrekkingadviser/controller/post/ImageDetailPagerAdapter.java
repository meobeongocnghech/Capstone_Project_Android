package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viettrekker.mountaintrekkingadviser.model.MyMedia;

import java.util.List;

public class ImageDetailPagerAdapter extends FragmentStatePagerAdapter {
    private List<String> medias;

    public ImageDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setMedias(List<String> medias) {
        this.medias = medias;
    }

    @Override
    public Fragment getItem(int i) {
        return PostImageFragment.newInstance(medias.get(i));
    }

    @Override
    public int getCount() {
        return medias.size();
    }
}
