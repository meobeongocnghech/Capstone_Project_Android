package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.GlideApp;

public class GridItemView1 extends FrameLayout {
        private ImageView img;
    public GridItemView1(@NonNull Context context) {
        super(context);
        img = new ImageView(context);
        img.setAdjustViewBounds(true);
        this.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setImage(String path) {
        GlideApp.with(this.getContext())
                .load(path)
                .centerCrop()
                .into(img);
        requestLayout();
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
