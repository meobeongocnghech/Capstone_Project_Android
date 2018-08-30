package com.viettrekker.mountaintrekkingadviser.controller.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.LoginActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private MaterialButton submit;
    private boolean isNext;
    private String firstname;
    private String lastname;
    private String sDoB;
    private int gender;
    private String email;
    private String pwd;
    private String repwd;
    private Fragment fragmentRegisterBasicInfo;
    private Fragment fragmentRegisterAccountInfo;
    private ProgressDialog progress;

    private Toolbar toolbar;

    public RegisterActivity() {
        isNext = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.regToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadInitialFragment();

        submit = (MaterialButton) findViewById(R.id.btnRegNext);
        submit.setOnClickListener((v) -> {
            if (isNext) {
                if (validateBasicInfoData()) {
                    performTransition();
                    MaterialButton button = (MaterialButton) v;
                    button.setText(R.string.button_register);
                    isNext = false;
                }
            } else {
                if (valiteAccountInfo()) {
                    submitRegister();
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            toolbar.setVisibility(View.GONE);
                        } else {
                            toolbar.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void loadInitialFragment() {
        fragmentRegisterBasicInfo = new RegisterBasicInfoFragment();
        fragmentRegisterAccountInfo = new RegisterAccountInfoFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.regFragmentContainer, fragmentRegisterBasicInfo);
        fragmentTransaction.commit();
    }

    private void performTransition() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Slide exit = new Slide();
        exit.setDuration(200);

        Slide enter = new Slide();
        enter.setStartDelay(100);
        enter.setDuration(300);

        if (isNext) {
            exit.setSlideEdge(Gravity.LEFT);
            fragmentRegisterBasicInfo.setExitTransition(exit);
            enter.setSlideEdge(Gravity.RIGHT);
            fragmentRegisterAccountInfo.setEnterTransition(enter);
            fragmentTransaction.replace(R.id.regFragmentContainer, fragmentRegisterAccountInfo);
        } else {
            exit.setSlideEdge(Gravity.RIGHT);
            fragmentRegisterAccountInfo.setExitTransition(exit);
            enter.setSlideEdge(Gravity.LEFT);
            fragmentRegisterBasicInfo.setEnterTransition(enter);
            fragmentTransaction.replace(R.id.regFragmentContainer, fragmentRegisterBasicInfo);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }

    private boolean validateBasicInfoData() {
        TextInputLayout layout;
        boolean isValidated = true;

        TextInputEditText edtFirstname = (TextInputEditText) findViewById(R.id.edtFirstname);
        firstname = edtFirstname.getText().toString().trim();
        layout = (TextInputLayout) findViewById(R.id.firstnameLayout);
        if (firstname.isEmpty()) {
            edtFirstname.addTextChangedListener(new MyTextWatcher(layout, edtFirstname));
            setFailStatus(edtFirstname, layout, "Hãy nhập họ của bạn");
            isValidated = false;
        } else {
            setCorrectStatus(edtFirstname);
        }

        TextInputEditText edtLastname = (TextInputEditText) findViewById(R.id.edtLastname);
        lastname = edtLastname.getText().toString().trim();
        layout = (TextInputLayout) findViewById(R.id.lastnameLayout);
        if (lastname.isEmpty()) {
            edtLastname.addTextChangedListener(new MyTextWatcher(layout, edtLastname));
            setFailStatus(edtLastname, layout, "Hãy nhập tên của bạn");
            isValidated = false;
        } else {
            setCorrectStatus(edtLastname);
        }

        RadioButton rbMale = (RadioButton) findViewById(R.id.radioMale);
        RadioButton rbFemale = (RadioButton) findViewById(R.id.radioFemale);
        final TextInputLayout genderLayout = (TextInputLayout) findViewById(R.id.genderLayout);
        if (!rbMale.isChecked() && !rbFemale.isChecked()) {
            setFailStatus(null, genderLayout, "Chọn giới tính");
            isValidated = false;
            rbMale.setOnCheckedChangeListener((compoundButton, b) -> {
                if (genderLayout.isErrorEnabled()) genderLayout.setErrorEnabled(false);
            });
            rbFemale.setOnCheckedChangeListener((compoundButton, b) -> {
                if (genderLayout.isErrorEnabled()) genderLayout.setErrorEnabled(false);
            });
        } else {
            gender = rbMale.isChecked() ? 0 : 1;
        }

        TextInputEditText edtDoB = (TextInputEditText) findViewById(R.id.edtDoB);
        sDoB = edtDoB.getText().toString().trim();
        layout = (TextInputLayout) findViewById(R.id.birthdateLayout);
        if (sDoB.isEmpty()) {
            setFailStatus(edtDoB, layout, "Hãy chọn ngày sinh");
            edtDoB.addTextChangedListener(new MyTextWatcher(layout, edtDoB));
            isValidated = false;
        } else {
            try {
                if (DateTimeUtils.calculateAge(sDoB) < 15) {
                    setFailStatus(edtDoB, layout, "Người dùng phải trên 15 tuổi");
                    edtDoB.addTextChangedListener(new MyTextWatcher(layout, edtDoB));
                    isValidated = false;
                } else {
                    sDoB = DateTimeUtils.formatISO(sDoB);
                    setCorrectStatus(edtDoB);
                }
            } catch (ParseException e) {
                setFailStatus(edtDoB, layout, "Sai định dạng");
                edtDoB.addTextChangedListener(new MyTextWatcher(layout, edtDoB));
                isValidated = false;
            }
        }

        return isValidated;
    }

    private boolean valiteAccountInfo() {
        boolean isValidated = true;
        TextInputLayout layout;

        TextInputEditText edtEmail = (TextInputEditText) findViewById(R.id.edtEmail);
        email = edtEmail.getText().toString().trim();
        layout = (TextInputLayout) findViewById(R.id.emailLayout);
        Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");
        if (email.isEmpty()) {
            edtEmail.addTextChangedListener(new MyTextWatcher(layout, edtEmail));
            setFailStatus(edtEmail, layout, "Hãy nhập email của bạn");
            isValidated = false;
        } else if (!ptr.matcher(email).matches()) {
            edtEmail.addTextChangedListener(new MyTextWatcher(layout, edtEmail));
            setFailStatus(edtEmail, layout, "Sai định dạng");
            isValidated = false;
        } else {
            setCorrectStatus(edtEmail);
        }

        TextInputEditText edtPwd = (TextInputEditText) findViewById(R.id.edtPassword);
        pwd = edtPwd.getText().toString();
        layout = (TextInputLayout) findViewById(R.id.passwordLayout);
        String pwdPattern = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,}$";
        if (pwd.isEmpty()) {
            edtPwd.addTextChangedListener(new MyTextWatcher(layout, edtPwd));
            setFailStatus(edtPwd, layout, "Hãy nhập mật khẩu");
            isValidated = false;
        } else if (pwd.length() < 8) {
            edtPwd.addTextChangedListener(new MyTextWatcher(layout, edtPwd));
            setFailStatus(edtPwd, layout, "Mật khẩu quá ngắn");
            isValidated = false;
        } else if (!pwd.matches(pwdPattern)) {
            edtPwd.addTextChangedListener(new MyTextWatcher(layout, edtPwd));
            setFailStatus(edtPwd, layout, "Mật khẩu chỉ chứa kí tự số và chữ cái");
            isValidated = false;
        } else {
            setCorrectStatus(edtPwd);
        }

        TextInputEditText edtRepwd = (TextInputEditText) findViewById(R.id.edtRepassword);
        repwd = edtRepwd.getText().toString();
        layout = (TextInputLayout) findViewById(R.id.repasswordLayout);
        if (repwd.isEmpty()) {
            edtRepwd.addTextChangedListener(new MyTextWatcher(layout, edtRepwd));
            setFailStatus(edtRepwd, layout, "Không được để trống");
            isValidated = false;
        } else if (!repwd.equals(pwd)) {
            edtRepwd.addTextChangedListener(new MyTextWatcher(layout, edtRepwd));
            setFailStatus(edtRepwd, layout, "Nhập lại không đúng");
            isValidated = false;
        } else {
            setCorrectStatus(edtRepwd);
        }

        return isValidated;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isNext) {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            performTransition();
            submit.setText(R.string.button_next);
            isNext = true;
        }
        return true;
    }

    private void submitRegister() {
        APIService mWebService = APIUtils.getWebService();
        showLoading();
        mWebService.postRegister(email, pwd, firstname, lastname, gender, sDoB).enqueue(new Callback<MyMessage>() {
            @Override
            public void onResponse(Call<MyMessage> call, Response<MyMessage> response) {
                progress.dismiss();
                if (response.code() == HttpsURLConnection.HTTP_OK) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else {

                    Type type = new TypeToken<MyMessage>() {
                    }.getType();
                    Gson gson = new Gson();
                    try {
                        MyMessage message = gson.fromJson(response.errorBody().string(), type);
                        if (message.getMessage().trim().equalsIgnoreCase("existed email!")) {
                            TextInputEditText edtEmail = (TextInputEditText) findViewById(R.id.edtEmail);
                            TextInputLayout layout = (TextInputLayout) findViewById(R.id.emailLayout);
                            edtEmail.addTextChangedListener(new MyTextWatcher(layout, edtEmail));
                            setFailStatus(edtEmail, layout, "Địa chỉ email đã được đăng ký");
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyMessage> call, Throwable throwable) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading() {
        progress = new ProgressDialog(this);
        progress.setTitle("Đăng ký");
        progress.setMessage("Đang hoàn tất đăng ký...");
        progress.setCancelable(false);
        progress.show();
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
