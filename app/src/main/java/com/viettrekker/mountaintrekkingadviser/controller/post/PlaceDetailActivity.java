package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.os.Bundle;

import com.viettrekker.mountaintrekkingadviser.R;

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
