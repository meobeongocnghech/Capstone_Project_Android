package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viettrekker.mountaintrekkingadviser.controller.post.NewsFeedFragment;

public class ProfileMemberPostAdapter extends FragmentPagerAdapter {
    private boolean isByUserId;
    private int userId;
    NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
    ProfileOwnFragment profileOwnFragment = new ProfileOwnFragment();
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public void setByUserId(boolean byUserId) {
        isByUserId = byUserId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ProfileMemberPostAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                if (isByUserId) {
                    newsFeedFragment.setByUserId(isByUserId);
                    newsFeedFragment.setUserId(userId);
                    newsFeedFragment.setToken(token);
                }
                return newsFeedFragment;
            case 1:
                return profileOwnFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
