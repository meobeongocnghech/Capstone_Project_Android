package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxPageTransformer;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxTransformInformation;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

public class PostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchCompat switchView = (SwitchCompat) view.findViewById(R.id.swtMode);
        Chip chip = (Chip) view.findViewById(R.id.postHint);
        loadPlaceData(chip);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    loadNewsfeedData(chip);
                } else {
                    loadPlaceData(chip);
                }
            }
        });

    }

    private void loadPlaceData(Chip chip) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        PagePlaceFragment placeFragment = new PagePlaceFragment();

        Fade fadeIn = new Fade();
        fadeIn.setMode(Fade.MODE_IN);
        fadeIn.setDuration(100);
        fadeIn.setStartDelay(100);
        placeFragment.setEnterTransition(fadeIn);

        Fade fadeOut = new Fade();
        fadeOut.setMode(Fade.MODE_OUT);
        fadeOut.setDuration(100);
        placeFragment.setExitTransition(fadeOut);

        fragmentTransaction.replace(R.id.postFragment, placeFragment);
        fragmentTransaction.commitAllowingStateLoss();

        loadChipAnimation(chip, "Địa điểm");
    }

    private void loadChipAnimation(Chip chip, String hint) {
        chip.setVisibility(View.VISIBLE);
        chip.setAlpha(1f);
        chip.setText(hint);

        chip.animate().alpha(1f).setDuration(200).start();

        chip.animate().alpha(0f).setDuration(200).setStartDelay(700).start();
    }

    private void loadNewsfeedData(Chip chip) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        NewsFeedFragment newsFeedFragment = new NewsFeedFragment();

        Fade fadeIn = new Fade();
        fadeIn.setMode(Fade.MODE_IN);
        fadeIn.setDuration(100);
        fadeIn.setStartDelay(100);
        newsFeedFragment.setEnterTransition(fadeIn);

        Fade fadeOut = new Fade();
        fadeOut.setMode(Fade.MODE_OUT);
        fadeOut.setDuration(100);
        newsFeedFragment.setExitTransition(fadeOut);

        fragmentTransaction.replace(R.id.postFragment, newsFeedFragment);
        fragmentTransaction.commitAllowingStateLoss();

        loadChipAnimation(chip, "Bảng tin");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //    private void loadEffect(View view, PlaceViewPagerAdapter placeAdapter) {
//        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.post_ptr_frame);
//
////        final PtrLoadingHeader header = new PtrLoadingHeader(getContext());
////        LocalDisplay.init(getContext());
////        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
////        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
////        header.setBackgroundColor(Color.WHITE);
//
//        final MaterialHeader header = new MaterialHeader(getContext());
//        int[] colors = getResources().getIntArray(R.array.google_colors);
//        header.setColorSchemeColors(colors);
//        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
//        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
//        header.setPtrFrameLayout(frame);
//
//        frame.setLoadingMinTime(1000);
//        frame.setDurationToCloseHeader(1500);
//        frame.setHeaderView(header);
//        frame.addPtrUIHandler(header);
//        frame.setPullToRefresh(false);
//        frame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                frame.autoRefresh(true);
//            }
//        }, 100);
//
//        frame.setPtrHandler(new PtrHandler() {
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                return true;
//            }
//
//            @Override
//            public void onRefreshBegin(final PtrFrameLayout frame) {
//                placeAdapter.load(placeAdapter.getCount() == 0 ? 5 : placeAdapter.getCount(), frame);
//            }
//        });
//    }
}
