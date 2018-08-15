package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PlaceDetailActivity extends AppCompatActivity {
    private RecyclerView rvGuide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        init();
        //setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        getWindow().setExitTransition(fade);
        getWindow().setSharedElementExitTransition(new Explode());
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.placeToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvGuide = (RecyclerView) findViewById(R.id.rvListGuide);
        rvGuide.setLayoutManager(new LinearLayoutManager(this));
        ReviewAdapter adapter = new ReviewAdapter();
        rvGuide.setAdapter(adapter);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.placeCollapsingToolbar);
        toolbarLayout.setTitle(getIntent().getStringExtra("name"));

        ImageView imgCover = (ImageView) findViewById(R.id.placeToolbarImage);
        Point size;
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        size = new Point();
        wm.getDefaultDisplay().getSize(size);
        imgCover.getLayoutParams().height = (int) (size.x * 1.5 / 3);
        imgCover.requestLayout();
        toolbarLayout.getLayoutParams().height = (int) (size.x * 1.5 / 3);
        toolbarLayout.requestLayout();
//        GlideApp.with(this)
//                .load(getIntent().getStringExtra("img"))
//                .into(imgCover);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //supportFinishAfterTransition();
        onBackPressed();
        return true;
    }
}
