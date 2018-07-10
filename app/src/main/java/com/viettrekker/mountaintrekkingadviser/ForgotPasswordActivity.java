package com.viettrekker.mountaintrekkingadviser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordActivity.this.onBackPressed();
            }
        });
    }

    private void init() {
        btnBack = (ImageButton) findViewById(R.id.btnForgotPasswordBack);

        toolbar = (Toolbar) findViewById(R.id.forgotPasswordToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }
}
