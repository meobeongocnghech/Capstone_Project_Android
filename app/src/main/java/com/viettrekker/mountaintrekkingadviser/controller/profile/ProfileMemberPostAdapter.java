package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.post.NewsFeedFragment;

public class ProfileMemberPostAdapter extends FragmentPagerAdapter {
    private boolean isByUserId;
    private int userId;

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
                NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
                if (isByUserId) {
                    newsFeedFragment.setByUserId(isByUserId);
                    newsFeedFragment.setUserId(userId);
                }

                return newsFeedFragment;
            case 1:
                ProfileOwnFragment profileOwnFragment = new ProfileOwnFragment();
                return profileOwnFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
