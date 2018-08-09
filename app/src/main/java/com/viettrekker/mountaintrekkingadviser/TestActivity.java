package com.viettrekker.mountaintrekkingadviser;

import android.support.v7.app.AppCompatActivity;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.customview.PtrLoadingHeader;
import com.viettrekker.mountaintrekkingadviser.customview.RentalsSunHeaderView;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;

public class TestActivity extends AppCompatActivity {

    private String mUrl = "http://img4.duitang.com/uploads/blog/201407/07/20140707113856_hBf3R.thumb.jpeggg";
    private long mStartLoadingTime = -1;
    private boolean mImageHasLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ShimmerFrameLayout shimmer = (ShimmerFrameLayout) findViewById(R.id.shimmerTest);
        shimmer.startShimmer();
        new Handler().postDelayed(()->{
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }, 9000);
    }
}
