package com.viettrekker.mountaintrekkingadviser;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.viettrekker.mountaintrekkingadviser.controller.search.BaseExampleFragment;
import com.viettrekker.mountaintrekkingadviser.controller.search.SlidingSearchResultsFragment;

public class TestActivity extends AppCompatActivity
        implements BaseExampleFragment.BaseExampleFragmentCallbacks{
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SlidingSearchResultsFragment()).commit();
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
    }
}
