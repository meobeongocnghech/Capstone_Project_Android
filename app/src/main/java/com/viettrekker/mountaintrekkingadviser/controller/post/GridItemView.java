package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;

public class GridItemView extends FrameLayout {
    private ImageView img;
    private TextView tv;
    public GridItemView(@NonNull Context context) {
        super(context);
        img = new ImageView(context);
        img.setAdjustViewBounds(true);
        tv = new TextView(context);
        tv.setVisibility(GONE);
        this.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(tv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setImage(String path) {
        GlideApp.with(this.getContext())
                .load(path)
                .fallback(R.drawable.default_background)
                .placeholder(R.drawable.default_background)
                .centerCrop()
                .into(img);
        requestLayout();
    }

    public void setText(int num, float size) {
        tv.setText("+ " + num);
        tv.setBackgroundColor(Color.argb(127, 0, 0, 0));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.rgb(255, 255, 255));
        tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        tv.setVisibility(VISIBLE);
    }
//    private TextView title;
//
//    public GridItemView1(Context context) {
//        super(context);
//        title = new TextView(context);
//        title.setGravity(Gravity.CENTER);
//        addView(title, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//    }
//
//    fun setTitle(text:String) {
//        title.text = text
//
//        requestLayout()
//    }
}
