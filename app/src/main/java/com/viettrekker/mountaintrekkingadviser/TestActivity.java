package com.viettrekker.mountaintrekkingadviser;

import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.search.BaseExampleFragment;
import com.viettrekker.mountaintrekkingadviser.controller.search.SlidingSearchResultsFragment;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestActivity extends AppCompatActivity {

    TextView tv;
    RecyclerView rcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        rcv = (RecyclerView) findViewById(R.id.rcvTestImages);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        TestAdapter adapter = new TestAdapter();
        adapter.setWidth(LocalDisplay.getScreenWidth(this));
        adapter.setContext(this);
        rcv.setAdapter(adapter);

        tv = (TextView) findViewById(R.id.loadMore);

        tv.setOnClickListener((v) -> adapter.loadMore());
    }

    public void hideLoadMore() {
        tv.setVisibility(View.GONE);
    }

    public RecyclerView getRcv() {
        return rcv;
    }
}
