package com.viettrekker.mountaintrekkingadviser.controller;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.support.design.widget.Snackbar;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.register.RegisterActivity;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button btnForgotPassword;
    private Button btnRegister;
    private Button btnLogin;
    private EditText edtLoginEmail;
    private EditText edtLoginPassword;
    private ImageView imgBg;

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
                String email = edtLoginEmail.getText().toString().trim();
                String password = edtLoginPassword.getText().toString().trim();
                final ConstraintLayout view = findViewById(R.id.loginLayout);
                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(view, "Không được để trống", Snackbar.LENGTH_LONG).show();
                } else {
                    APIService mWebService = APIUtils.getWebService();
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.DialogStyle);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mWebService.postLogin(email, password).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            progressDialog.dismiss();
                            if (response.body() == null) {
                                Snackbar.make(view, "Sai email hoặc mật khẩu", Snackbar.LENGTH_LONG).show();
                            } else {
                                User user = response.body();
                                MainActivity.user = user;
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable throwable) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            runOnUiThread(() -> {
                                btnForgotPassword.setVisibility(View.GONE);
                                findViewById(R.id.tvQuestion).setVisibility(View.GONE);
                                btnRegister.setVisibility(View.GONE);
//                    ((ConstraintLayout.LayoutParams) edtLoginEmail.getLayoutParams()).verticalBias = 0.5f;
                            });
                        } else {
                            runOnUiThread(() -> {
                                btnForgotPassword.setVisibility(View.VISIBLE);
                                findViewById(R.id.tvQuestion).setVisibility(View.VISIBLE);
                                btnRegister.setVisibility(View.VISIBLE);
//                    ((ConstraintLayout.LayoutParams) edtLoginEmail.getLayoutParams()).verticalBias = 0.4f;
                            });
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imgBg.getLayoutParams().height = displayMetrics.heightPixels;
        imgBg.requestLayout();
    }

    private void init() {
        edtLoginEmail = (EditText) findViewById(R.id.edtLoginEmail);
        edtLoginPassword = (EditText) findViewById(R.id.edtLoginPassword);

        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        imgBg = (ImageView) findViewById(R.id.imgBG1);
    }
}
