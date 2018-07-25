package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.util.ActivityUtils;

import androidx.fragment.app.Fragment;

public class PlacePageLayoutFragment extends Fragment {

    private int position = 0;
    ShimmerFrameLayout mShimmer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_page_place_item, container, false);
        mShimmer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer);
        return view;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmer.startShimmer();
    }

    @Override
    public void onPause() {
        mShimmer.stopShimmer();
        super.onPause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView img = (ImageView) view.findViewById(R.id.imgPlaceCover);

        Point size;
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        size = new Point();
        wm.getDefaultDisplay().getSize(size);

        img.getLayoutParams().height = (int) (size.x * 1.5 / 3);;
        img.requestLayout();

        int rand = (int) (Math.random() * 9);
        
        String url = "http://longwallpapers.com/Desktop-Wallpaper/chicago-wallpapers-for-iphone-For-Desktop-Wallpaper.jpg";

        switch (position) {
            case 0:
                Picasso.get()
                        .load(R.drawable.cover_1)
                        .into(img, new Callback() {
                            @Override
                            public void onSuccess() {
                                mShimmer.stopShimmer();
                                mShimmer.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                break;
            default:
                Picasso.get()
                        .load(R.drawable.cover_2)
                        .into(img, new Callback() {
                            @Override
                            public void onSuccess() {
                                mShimmer.stopShimmer();
                                mShimmer.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
        }

        Button btnViewDetail = (Button) view.findViewById(R.id.btnPlaceView);
        btnViewDetail.setOnClickListener((v) -> ActivityUtils.changeActivity(view.getContext(), PlaceDetailActivity.class, false));

    }
}
