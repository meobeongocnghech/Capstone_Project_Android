package com.viettrekker.mountaintrekkingadviser.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.register.RegisterActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        TextInputEditText pwd = (TextInputEditText) findViewById(R.id.edtPwd);
        TextInputEditText repwd = (TextInputEditText) findViewById(R.id.edtReNewPwd);
        TextInputEditText newPwd = (TextInputEditText) findViewById(R.id.edtNewPwd);

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

        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    pwd.setTransformationMethod(null);
                    repwd.setTransformationMethod(null);
                    newPwd.setTransformationMethod(null);
                } else {
                    pwd.setTransformationMethod(new PasswordTransformationMethod());
                    repwd.setTransformationMethod(new PasswordTransformationMethod());
                    newPwd.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnChange.setOnClickListener((v) -> {
            String pwdPattern = "^[a-zA-Z0-9]{8,}$";
            String password = pwd.getText().toString();
            String reNewpassword = repwd.getText().toString();
            String newpassword = newPwd.getText().toString();
            boolean invalidated = false;
            if (password.isEmpty()) {
                pwd.addTextChangedListener(new MyTextWatcher(findViewById(R.id.pwdLayout), pwd));
                setFailStatus(pwd, findViewById(R.id.pwdLayout), "Hãy nhập mật khẩu cũ");
                invalidated = true;
            } else {
                setCorrectStatus(pwd);
                if (!reNewpassword.equals(newpassword)) {
                    repwd.addTextChangedListener(new MyTextWatcher(findViewById(R.id.renewpwdLayout), repwd));
                    setFailStatus(repwd, findViewById(R.id.renewpwdLayout), "Nhập lại mật khẩu không đúng");
                    invalidated = true;
                } else {
                    setCorrectStatus(repwd);
                }
            }

            if (newpassword.isEmpty()) {
                newPwd.addTextChangedListener(new MyTextWatcher(findViewById(R.id.newpwdLayout), newPwd));
                setFailStatus(newPwd, findViewById(R.id.newpwdLayout), "Hãy nhập mật khẩu mới");
                invalidated = true;
            } else if (newpassword.length() < 8) {
                newPwd.addTextChangedListener(new MyTextWatcher(findViewById(R.id.newpwdLayout), newPwd));
                setFailStatus(newPwd, findViewById(R.id.newpwdLayout), "Mật khẩu quá ngắn");
                invalidated = true;
            } else if (!newpassword.matches(pwdPattern)) {
                newPwd.addTextChangedListener(new MyTextWatcher(findViewById(R.id.newpwdLayout), newPwd));
                setFailStatus(newPwd, findViewById(R.id.newpwdLayout), "Mật khẩu chỉ chứa kí tự số và chữ cái");
                invalidated = true;
            } else {
                setCorrectStatus(newPwd);
            }

            if (!invalidated) {
                APIUtils.getWebService().changePassword(Session.getToken(SettingsActivity.this), newpassword, password).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            Session.setSession(SettingsActivity.this, response.body());
                        } else {
                            Type type = new TypeToken<MyMessage>() {
                            }.getType();
                            Gson gson = new Gson();
                            try {
                                MyMessage message = gson.fromJson(response.errorBody().string(), type);
                                if (message.getMessage().trim().equalsIgnoreCase("Sai mật khẩu!")) {
                                    pwd.addTextChangedListener(new MyTextWatcher(findViewById(R.id.edtPwd), pwd));
                                    setFailStatus(pwd, findViewById(R.id.edtPwd), "Sai mật khẩu!");
                                } else {
                                    Snackbar.make(findViewById(R.id.layout), "Đã có lỗi xảy ra", Snackbar.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setFailStatus(TextInputEditText edt, TextInputLayout layout, String message) {
        layout.setErrorEnabled(true);
        layout.setError(message);
        if (edt != null) {
            edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning, 0);
        }
    }

    private void setCorrectStatus(TextInputEditText edt) {
        edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_correct, 0);
    }

    private class MyTextWatcher implements TextWatcher {

        private TextInputLayout layout;
        private TextInputEditText edt;

        public MyTextWatcher(TextInputLayout layout, TextInputEditText edt) {
            this.layout = layout;
            this.edt = edt;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (layout.isErrorEnabled()) {
                layout.setErrorEnabled(false);
                edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
