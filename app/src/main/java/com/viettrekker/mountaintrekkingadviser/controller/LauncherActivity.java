package com.viettrekker.mountaintrekkingadviser.controller;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.viettrekker.mountaintrekkingadviser.R;

import android.support.v7.app.AppCompatActivity;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;

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

//        int SPLASH_DISPLAY_LENGTH = 2000;
//        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }, SPLASH_DISPLAY_LENGTH);
    }
}
