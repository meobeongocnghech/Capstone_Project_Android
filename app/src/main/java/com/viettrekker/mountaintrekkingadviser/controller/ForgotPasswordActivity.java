package com.viettrekker.mountaintrekkingadviser.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.viettrekker.mountaintrekkingadviser.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.forgotPasswordToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
