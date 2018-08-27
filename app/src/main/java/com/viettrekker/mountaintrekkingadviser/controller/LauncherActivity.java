package com.viettrekker.mountaintrekkingadviser.controller;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.viettrekker.mountaintrekkingadviser.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        ImageView v = (ImageView) findViewById(R.id.loading);
        Drawable d = v.getDrawable();
        if (d instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
            avd.start();
        } else if (d instanceof AnimatedVectorDrawable){
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
            avd.start();
        }

        ProgressBar progress = (ProgressBar) findViewById(R.id.launcherProgress);

        v.postDelayed(() -> {
            InitialLoadAsyncTask task = new InitialLoadAsyncTask(progress, this);
            task.execute();
        }, 1500);
    }
}
