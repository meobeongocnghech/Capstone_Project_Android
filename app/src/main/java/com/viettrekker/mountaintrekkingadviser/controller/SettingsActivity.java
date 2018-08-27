package com.viettrekker.mountaintrekkingadviser.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialButton btnPolicy = (MaterialButton) findViewById(R.id.btnPolicy);
        TextView tvPolicy = (TextView) findViewById(R.id.tvPolicy);
        MaterialButton btnChangePwd = (MaterialButton) findViewById(R.id.btnChangePwd);
        ConstraintLayout changePwdLayout = (ConstraintLayout) findViewById(R.id.changePwdLayout);
        CheckBox cbShowPassword = (CheckBox) findViewById(R.id.cbShowPassword);
        MaterialButton btnChange = (MaterialButton) findViewById(R.id.btnChange);
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.policyScrollView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPolicy.setOnClickListener((v) -> {
            if (tvPolicy.getVisibility() == View.VISIBLE) {
                tvPolicy.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
            } else {
                tvPolicy.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                changePwdLayout.setVisibility(View.GONE);
            }
        });

        btnChangePwd.setOnClickListener((v) -> {
            if (changePwdLayout.getVisibility() == View.VISIBLE) {
                changePwdLayout.setVisibility(View.GONE);
            } else {
                tvPolicy.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                changePwdLayout.setVisibility(View.VISIBLE);
            }
        });

        String policy = getResources().getString(R.string.policy);
        tvPolicy.setText(policy);
        tvPolicy.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
