package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.transition.Transition;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;

import java.util.List;

public class DetailImageActivity extends AppCompatActivity {
    private ViewPager pager;
    private MaterialButton back;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        pager = findViewById(R.id.imgDetailPager);
        back = (MaterialButton) findViewById(R.id.btnBack);
        tv = (TextView) findViewById(R.id.tvTitle);
        back.setOnClickListener(v -> supportFinishAfterTransition());
        List<String> lists = (List<String>) getIntent().getSerializableExtra("listImages");
        tv.setText(getIntent().getStringExtra("title"));
        ImageDetailPagerAdapter adapter = new ImageDetailPagerAdapter(getSupportFragmentManager());
        adapter.setMedias(lists);
        pager.setAdapter(adapter);
        pager.setCurrentItem(getIntent().getIntExtra("current", 0));
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
