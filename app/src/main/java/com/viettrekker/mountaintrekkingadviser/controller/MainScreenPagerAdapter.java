package com.viettrekker.mountaintrekkingadviser.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationFragment;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.controller.search.SearchFragment;
public class MainScreenPagerAdapter extends FragmentPagerAdapter {

    private PostFragment postFragment;
    private PlanFragment planFragment;
    private NotificationFragment notificationFragment;
    private SearchFragment searchFragment;
    private String token;

    public void setToken(String token) {
        this.token = token;
        notificationFragment.setToken(token);
    }

    public PostFragment getPostFragment() {
        return postFragment;
    }

    public void setPostFragment(PostFragment postFragment) {
        this.postFragment = postFragment;
    }

    public PlanFragment getPlanFragment() {
        return planFragment;
    }

    public void setPlanFragment(PlanFragment planFragment) {
        this.planFragment = planFragment;
    }

    public NotificationFragment getNotificationFragment() {
        return notificationFragment;
    }

    public void setNotificationFragment(NotificationFragment notificationFragment) {
        this.notificationFragment = notificationFragment;
    }

    public SearchFragment getSearchFragment() {
        return searchFragment;
    }

    public void setSearchFragment(SearchFragment searchFragment) {
        this.searchFragment = searchFragment;
    }

    public MainScreenPagerAdapter(FragmentManager fm) {
        super(fm);
        postFragment = new PostFragment();
        planFragment = new PlanFragment();
        notificationFragment = new NotificationFragment();
        searchFragment = new SearchFragment();
    }

//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        if (object instanceof PostFragment) {
//            return 0;
//        } else if (object instanceof PlanFragment) {
//            return 1;
//        } else if (object instanceof NotificationFragment) {
//            return 2;
//        } else if (object instanceof SearchFragment) {
//            return 3;
//        } else {
//            return POSITION_NONE;
//        }
//    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return postFragment;
            case 1:
                return planFragment;
            case 2:
                return notificationFragment;
            case 3:
                return searchFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
