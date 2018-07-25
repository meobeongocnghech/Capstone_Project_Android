package com.viettrekker.mountaintrekkingadviser.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.retrofit.APIService;
import com.viettrekker.mountaintrekkingadviser.util.retrofit.APIUtils;
import com.viettrekker.mountaintrekkingadviser.util.ActivityUtils;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnForgotPassword;
    private Button btnRegister;
    private Button btnLogin;
    private EditText edtLoginEmail;
    private EditText edtLoginPassword;

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
                String email = edtLoginEmail.getText().toString();
                String password = edtLoginPassword.getText().toString();
                APIService mWebService = APIUtils.getWebService();
                mWebService.postLogin("linhnt@gmail.com", "linhnt").enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ConstraintLayout view = findViewById(R.id.loginLayout);
                        if (response.body() == null) {
                            Snackbar.make(view, "Email không tồn tại", Snackbar.LENGTH_LONG).show();
                        } else {
                            User user = response.body();
                            MainActivity.user = user;
                            ActivityUtils.changeActivity(LoginActivity.this, MainActivity.class, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable throwable) {

                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        edtLoginEmail = (EditText) findViewById(R.id.edtLoginEmail);
        edtLoginPassword = (EditText) findViewById(R.id.edtLoginPassword);

        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }
}
