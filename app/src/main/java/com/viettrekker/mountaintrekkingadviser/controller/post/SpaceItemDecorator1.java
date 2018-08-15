package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecorator1 extends android.support.v7.widget.RecyclerView.ItemDecoration {
    private int left;
    private int right;
    private int top;
    private int bottom;

    public SpaceItemDecorator1(Rect rect) {
        this.left = rect.left;
        this.right = rect.right;
        this.bottom = rect.bottom;
        this.top = rect.top;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = this.left;
        outRect.top = this.top;
        outRect.right = this.right;
        outRect.bottom = this.bottom;
    }
}
