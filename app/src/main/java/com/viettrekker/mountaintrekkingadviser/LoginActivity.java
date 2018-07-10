package com.viettrekker.mountaintrekkingadviser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    private ImageView imgBG1;
    private ImageView imgBG2;
    private int index;
    private boolean isFirst;
    private Button btnForgotPassword;
    private Button btnRegister;
    private Button btnLogin;

    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                while(true) {
//                    index = ++index == 11 ? 1 : index;
//                    if (isFirst) {
//                        imgBG1.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("cover_" + index, "drawable", getPackageName())));
//                        Log.i(TAG, index + "");
//                        isFirst = false;
//                    } else {
//                        isFirst = true;
//                        Log.i(TAG, index + "");
//                        imgBG2.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("cover_" + index, "drawable", getPackageName())));
//                    }
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//
//        thread.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        imgBG1 = (ImageView) findViewById(R.id.imgBG1);
        imgBG2 = (ImageView) findViewById(R.id.imgBG2);
        index = 0;
        isFirst = true;

        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }
}
