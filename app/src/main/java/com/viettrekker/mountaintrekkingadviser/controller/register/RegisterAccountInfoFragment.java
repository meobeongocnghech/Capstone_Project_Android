package com.viettrekker.mountaintrekkingadviser.controller.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.R;

public class RegisterAccountInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText edtPassword = (TextInputEditText) view.findViewById(R.id.edtPassword);
        TextInputEditText edtRepassword = (TextInputEditText) view.findViewById(R.id.edtRepassword);

        AppCompatCheckBox cb = (AppCompatCheckBox) view.findViewById(R.id.cbShowPassword);
        cb.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                edtPassword.setTransformationMethod(null);
                edtRepassword.setTransformationMethod(null);
            } else {
                edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                edtRepassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
    }
}
