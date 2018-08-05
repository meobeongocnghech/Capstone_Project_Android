package com.viettrekker.mountaintrekkingadviser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

        final ImageView imageView = (ImageView) findViewById(R.id.material_style_image_view);
        final PtrFrameLayout frame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);

        // header
        //final RentalsSunHeaderView header = new RentalsSunHeaderView(this);
        final PtrLoadingHeader header = new PtrLoadingHeader(this);
        LocalDisplay.init(this);
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        //header.setUp(frame);

        frame.setLoadingMinTime(1000);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.setPullToRefresh(false);
//        frame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                frame.autoRefresh(true);
//            }
//        }, 100);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                if (mImageHasLoaded) {
                    long delay = 1500;
                    frame.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            frame.refreshComplete();
                        }
                    }, delay);
                } else {
                    mStartLoadingTime = System.currentTimeMillis();
                    Picasso.get().load(mUrl).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mImageHasLoaded = true;
                            long delay = 1500;
                            frame.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    frame.refreshComplete();
                                }
                            }, delay);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }
        });
    }
}
