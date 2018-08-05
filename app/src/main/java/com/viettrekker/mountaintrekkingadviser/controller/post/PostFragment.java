package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxPageTransformer;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxTransformInformation;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.customview.PtrLoadingHeader;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

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

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.placeViewPager);

        ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
                .addViewToParallax(new ParallaxTransformInformation(R.id.imgPlaceCover, -5f, -5f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceName, -5f, -5f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceAddress, -5f, -5f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceTotalPlan, -5f, -5f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceDescription, -5f, -5f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceDistance, -5f, -5f));

        viewPager.setPageTransformer(true, pageTransformer);
        PlaceViewPagerAdapter placeAdapter = new PlaceViewPagerAdapter(getChildFragmentManager());
        loadEffect(view, placeAdapter);
        viewPager.setAdapter(placeAdapter);
    }

    private void loadEffect(View view, PlaceViewPagerAdapter placeAdapter) {
        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.post_ptr_frame);

        final PtrLoadingHeader header = new PtrLoadingHeader(getContext());
        LocalDisplay.init(getContext());
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setBackgroundColor(Color.WHITE);

        frame.setLoadingMinTime(1000);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.setPullToRefresh(false);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(true);
            }
        }, 100);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                placeAdapter.load(placeAdapter.getCount() == 0 ? 3 : placeAdapter.getCount(), frame);
            }
        });
    }
}
