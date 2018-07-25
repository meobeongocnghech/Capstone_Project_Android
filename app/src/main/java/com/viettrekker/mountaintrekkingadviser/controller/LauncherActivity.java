package com.viettrekker.mountaintrekkingadviser.controller;

import android.content.Intent;
import android.os.Bundle;

import com.viettrekker.mountaintrekkingadviser.R;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }
}
