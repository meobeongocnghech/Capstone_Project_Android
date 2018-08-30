package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxPageTransformer;
import com.viettrekker.mountaintrekkingadviser.animator.ParallaxTransformInformation;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

public class PagePlaceFragment extends Fragment {

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_place, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.placeViewPager);

        ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
                .addViewToParallax(new ParallaxTransformInformation(R.id.imgPlaceCover, -5f, -5f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceName, -3f, -3f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceAddress, -3f, -3f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceTotalPlan, -3f, -3f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceDescription, -3f, -3f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.tvPlaceDistance, -3f, -3f));

        viewPager.setPageTransformer(true, pageTransformer);
        PlaceViewPagerAdapter placeAdapter = new PlaceViewPagerAdapter(getChildFragmentManager());
        placeAdapter.setCurrentFragment(this);
        //loadEffect(view, placeAdapter);
        placeAdapter.load(placeAdapter.getCount() == 0 ? 5 : placeAdapter.getCount());
        viewPager.setAdapter(placeAdapter);

        MaterialButton btnViewDetail = (MaterialButton) view.findViewById(R.id.btnPlaceView);

        btnViewDetail.setOnClickListener((v) -> {
            Place place = placeAdapter.getPlaceItem(viewPager.getCurrentItem());
            Intent i = new Intent(getActivity(), PlaceDetailActivity.class);
            i.putExtra("id", place.getId());
            i.putExtra("token", token);
            i.putExtra("name", place.getName());
            i.putExtra("img", APIUtils.BASE_URL_API + place.getGallery().getMedia().get(0).getPath().substring(4));
            i.putExtra("lat", ((MainActivity) getActivity()).getLatLng().latitude);
            i.putExtra("lng", ((MainActivity) getActivity()).getLatLng().longitude);
            startActivity(i);
        });
    }
}
