package com.viettrekker.mountaintrekkingadviser.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        TextView tvHint = (TextView) findViewById(R.id.tvHint);
        EditText edtChangePwdEmail = (EditText) findViewById(R.id.edtChangePwdEmail);

        MaterialCardView cardUser = (MaterialCardView) findViewById(R.id.cardUser);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.mail);

        TextInputLayout keyLayout = (TextInputLayout) findViewById(R.id.keyLayout);
        TextInputLayout pwdLayout = (TextInputLayout) findViewById(R.id.pwdLayout);
        TextInputEditText edtKey = (TextInputEditText) findViewById(R.id.edtKey);
        TextInputEditText edtPwd = (TextInputEditText) findViewById(R.id.edtPwd);

        MaterialButton btnForgotAction = (MaterialButton) findViewById(R.id.btnForgotAction);
        MaterialButton btnReset = (MaterialButton) findViewById(R.id.btnReset);

        btnReset.setOnClickListener((v) -> {
            cardUser.setVisibility(View.GONE);
            keyLayout.setVisibility(View.GONE);
            pwdLayout.setVisibility(View.GONE);
            btnForgotAction.setText("Tìm");
            btnReset.setVisibility(View.GONE);
        });

        btnForgotAction.setOnClickListener((v) -> {
            if (btnForgotAction.getText().toString().equalsIgnoreCase("tìm")) {
                Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");
                if (!ptr.matcher(edtChangePwdEmail.getText().toString().trim()).matches()) {
                    Snackbar.make(findViewById(R.id.layout), "Email không đúng định dạng!", Snackbar.LENGTH_LONG).show();
                } else {
                    String token = getResources().getString(R.string.default_token);
                    APIUtils.getWebService().searchMember(token, edtChangePwdEmail.getText().toString().trim()).enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            List<User> list = response.body();
                            list.remove(0);
                            if (list.size() >= 1) {
                                User user  = list.get(0);
                                edtChangePwdEmail.setVisibility(View.GONE);
                                cardUser.setVisibility(View.VISIBLE);
                                btnReset.setVisibility(View.VISIBLE);
                                if (!user.getAvatar().getPath().isEmpty()){
                                    GlideApp.with(ForgotPasswordActivity.this)
                                            .load(APIUtils.BASE_URL_API + user.getAvatar().getPath().substring(4) + "&w=" + LocalDisplay.dp2px(56, ForgotPasswordActivity.this))
                                            .placeholder(R.drawable.avatar_default)
                                            .fallback(R.drawable.avatar_default)
                                            .into(avatar);
                                }
                                name.setText(user.getFirstName() + " " + user.getLastName());
                                email.setText(user.getEmail());
                                tvHint.setText(Html.fromHtml("Nhấn <b>Gửi email</b> để nhận mã xác nhận"));
                                btnForgotAction.setText("Gửi email");
                                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(ForgotPasswordActivity.this.getCurrentFocus().getWindowToken(), 0);
                                ForgotPasswordActivity.this.getCurrentFocus().clearFocus();
                            } else {
                                Snackbar.make(findViewById(R.id.layout), "Tài khoản email không tồn tại", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            Snackbar.make(findViewById(R.id.layout), "Đã có lỗi xảy ra", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            } else if (btnForgotAction.getText().toString().equalsIgnoreCase("gửi email")) {
                APIUtils.getWebService().sendKeyToEmail(edtChangePwdEmail.getText().toString().trim()).enqueue(new Callback<MyMessage>() {
                    @Override
                    public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                        if (response.code() == HttpURLConnection.HTTP_OK) {
                            Snackbar.make(findViewById(R.id.layout), response.message(), Snackbar.LENGTH_LONG).show();
                            btnForgotAction.setText("Đổi mật khẩu");
                            tvHint.setText("Nhập mã xác nhận và mật khẩu");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(ForgotPasswordActivity.this.getCurrentFocus().getWindowToken(), 0);
                            ForgotPasswordActivity.this.getCurrentFocus().clearFocus();
                            keyLayout.setVisibility(View.VISIBLE);
                            pwdLayout.setVisibility(View.VISIBLE);
                        } else {
                            Snackbar.make(findViewById(R.id.layout), "Đã có lỗi xảy ra", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyMessage> call, Throwable t) {
                        Snackbar.make(findViewById(R.id.layout), "Đã có lỗi xảy ra", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                boolean invalidated = false;
                if (!edtKey.getText().toString().trim().isEmpty()) {
                    Snackbar.make(findViewById(R.id.layout), "Mã xác nhận không được để trống", Snackbar.LENGTH_LONG).show();
                    invalidated = true;
                }

                String pwdPattern = "^[a-zA-Z0-9]{8,}$";
                if (!edtPwd.getText().toString().trim().isEmpty()) {
                    Snackbar.make(findViewById(R.id.layout), "Mật khẩu không được để trống", Snackbar.LENGTH_LONG).show();
                    invalidated = true;
                } else if(edtPwd.getText().toString().length() < 8) {
                    Snackbar.make(findViewById(R.id.layout), "Mật khẩu quá ngắn", Snackbar.LENGTH_LONG).show();
                    invalidated = true;
                } else if(!edtPwd.getText().toString().matches(pwdPattern)) {
                    Snackbar.make(findViewById(R.id.layout), "Mật khẩu chỉ bao gồm kí tự số và chữ cái", Snackbar.LENGTH_LONG).show();
                    invalidated = true;
                }
                if (!invalidated) {
                    APIUtils.getWebService().sendChangePassword(edtKey.getText().toString(), edtPwd.getText().toString()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                                Snackbar.make(findViewById(R.id.layout), "Đổi mật khẩu thành công", Snackbar.LENGTH_LONG).show();
                                cardUser.setVisibility(View.GONE);
                                keyLayout.setVisibility(View.GONE);
                                pwdLayout.setVisibility(View.GONE);
                                btnForgotAction.setText("Tìm");
                                btnReset.setVisibility(View.GONE);
                            } else {
                                Snackbar.make(findViewById(R.id.layout), "Đã có lỗi xảy ra", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(findViewById(R.id.layout), "Đã có lỗi xảy ra", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
