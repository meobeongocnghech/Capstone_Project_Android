package com.viettrekker.mountaintrekkingadviser.controller;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.util.retrofit.APIService;
import com.viettrekker.mountaintrekkingadviser.util.retrofit.APIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private MaterialButton submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.regToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        submit = (MaterialButton) findViewById(R.id.btnRegNext);

        submit.setOnClickListener((v) -> submitRegister());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void submitRegister() {
        TextInputEditText edtSurname = (TextInputEditText) findViewById(R.id.edtSurname);
        String surname = edtSurname.getText().toString();

        TextInputEditText edtForename = (TextInputEditText) findViewById(R.id.edtForename);
        String forename = edtForename.getText().toString();

        TextInputEditText edtEmail = (TextInputEditText) findViewById(R.id.edtEmail);
        String email = edtEmail.getText().toString();

        TextInputEditText edtPwd = (TextInputEditText) findViewById(R.id.edtPassword);
        String pwd = edtPwd.getText().toString();

        TextInputEditText edtRePwd = (TextInputEditText) findViewById(R.id.edtRePassword);
        String rePwd = edtRePwd.getText().toString();

        RadioButton rbMale = (RadioButton) findViewById(R.id.radioMale);
        int gender = rbMale.isChecked() ? 1 : 0;

        TextInputEditText edtDoB = (TextInputEditText) findViewById(R.id.edtDoB);
        String sDoB = edtDoB.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd/MM/yyyy");
        Date dob = null;
        try {
            dob = sdf.parse(sDoB);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.applyPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        String birthdate = sdf.format(dob);

        APIService mWebService = APIUtils.getWebService();
        mWebService.postRegister(email, pwd, surname, forename, gender, birthdate).enqueue(new Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                NestedScrollView view = (NestedScrollView) findViewById(R.id.registerLayout);
                MyMessage msg = response.body();
                if (msg.getMessage() != null) {
                    Snackbar.make(view, msg.getMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable throwable) {
                //Snackbar.make(RegisterActivity.this, R.string.error, Snackbar.LENGTH_LONG).show();
                Log.e("ERR", throwable.getMessage());
            }
        });
    }
}
