package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SwitchCompat;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxPageTransformer;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxTransformInformation;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

public class PostFragment extends Fragment {

    private NestedScrollView layout;
    private SwitchCompat switchView;
    private ProgressBar progress;
    private NewsFeedFragment newsFeedFragment;
    private PagePlaceFragment placeFragment;

    public PostFragment() {
        newsFeedFragment = new NewsFeedFragment();
        placeFragment = new PagePlaceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        layout = (NestedScrollView) view.findViewById(R.id.postScrollView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchView = (SwitchCompat) view.findViewById(R.id.swtMode);
        Chip chip = (Chip) view.findViewById(R.id.postHint);
        progress = (ProgressBar) view.findViewById(R.id.progressPost);

        loadNewsfeedData();
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    hideProgress();
                    loadPlaceData();
                    ((MainActivity) getActivity()).setTitle("Địa điểm");
                    loadChipAnimation(chip, "Địa điểm");
                } else {
                    showProgress();
                    loadNewsfeedData();
                    ((MainActivity) getActivity()).setTitle("Bảng tin");
                    loadChipAnimation(chip, "Bảng tin");
                }
            }
        });

        layout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (layout != null) {
                    if (layout.getChildAt(0).getBottom() <= (layout.getHeight() + layout.getScrollY())) {
                        NewsFeedFragment fragment = ((NewsFeedFragment) getFragmentManager().findFragmentByTag("newsfeed"));
                        if (fragment != null && fragment.isVisible()) {
                            fragment.incrementalLoad();
                        }
                    }
                }
            }
        });
    }

    private void loadPlaceData() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        placeFragment.setToken(Session.getToken(getActivity()));

        fragmentTransaction.replace(R.id.postFragment, placeFragment, "place");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadNewsfeedData() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        newsFeedFragment.setUserId(Session.getUserId(getActivity()));
        newsFeedFragment.setToken(Session.getToken(getActivity()));

        fragmentTransaction.replace(R.id.postFragment, newsFeedFragment, "newsfeed");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadChipAnimation(Chip chip, String hint) {
        chip.setVisibility(View.VISIBLE);
        chip.setAlpha(1f);
        chip.setText(hint);
        chip.animate().alpha(1f).setDuration(200).start();
        chip.animate().alpha(0f).setDuration(200).setStartDelay(700).start();
    }

    public void scrollToTop() {
        layout.dispatchTouchEvent(MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN, 100,100,0.5f,5,0,1,1,0,0));
        layout.fling(0);
        layout.smoothScrollTo(0, 0);
    }

    public int getCurrentScrollY() {
        return layout.getScrollY();
    }

    public void refreshData() {
        if (switchView.isChecked()) {
            loadPlaceData();
            hideProgress();
        } else {
            loadNewsfeedData();
            showProgress();
        }
    }

    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }
}
