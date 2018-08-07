package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceDetailActivity extends AppCompatActivity {
    private RecyclerView rvGuide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        init();
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
        Picasso.get()
                .load(getIntent().getStringExtra("img"))
                .into(imgCover);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
