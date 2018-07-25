package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.util.transformation.ParallaxPageTransformer;
import com.viettrekker.mountaintrekkingadviser.util.transformation.ParallaxTransformInformation;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class PostFragment extends Fragment {

    SwitchCompat swap;
    boolean isViewPlace;
    Spinner spnCategory;
    ScrollView scrollView;

    public PostFragment() {
        isViewPlace = true;
    }

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
        viewPager.setAdapter(new PlaceViewPagerAdapter(getChildFragmentManager()));
    }

    public void scrollToTop() {
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }
}
