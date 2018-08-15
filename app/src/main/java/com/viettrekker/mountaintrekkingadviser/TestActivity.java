package com.viettrekker.mountaintrekkingadviser;

import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
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
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayout.Spec;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


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
//        GridLayout grid = (GridLayout) findViewById(R.id.content);
//
//        Spec row1 = GridLayout.spec(0);
//        Spec row2 = GridLayout.spec(1);
//        Spec row3 = GridLayout.spec(2);
//        Spec row4 = GridLayout.spec(3);
//
//        Spec col0 = GridLayout.spec(0);
//        Spec col1 = GridLayout.spec(1);
//        Spec col2 = GridLayout.spec(2);
//
//        GridLayout gridLayout = new GridLayout(this);
//        GridLayout.LayoutParams first = new GridLayout.LayoutParams(row1, col0);
//        /*Here you can set options for first cell which is in first row and first column.*/
//        first.width = LocalDisplay.getScreenWidth(this);
//        first.height = LocalDisplay.getScreenWidth(this)/4 * 2;
//        TextView twoByTwo1 = new TextView(this);
//        twoByTwo1.setLayoutParams(first);
//        twoByTwo1.setGravity(Gravity.CENTER);
//        twoByTwo1.setBackgroundColor(Color.RED);
//        twoByTwo1.setText("TOP");
//        twoByTwo1.setTextAppearance(this, android.R.style.TextAppearance_Large);
//        gridLayout.addView(twoByTwo1, first);


        AppBarLayout layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        int max = imageView.getLayoutParams().height;
        int init = layout.getLayoutParams().height;
        layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                imageView.getLayoutParams().height = max + i;
                imageView.requestLayout();
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.test_menu, popup.getMenu());
        popup.show();
    }
}
