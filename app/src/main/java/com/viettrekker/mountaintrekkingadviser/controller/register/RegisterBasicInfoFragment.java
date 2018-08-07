package com.viettrekker.mountaintrekkingadviser.controller.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterBasicInfoFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    TextInputEditText edtBirthdate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_basic_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtBirthdate = (TextInputEditText) view.findViewById(R.id.edtDoB);

        TextInputEditText edtBirthdate = (TextInputEditText) view.findViewById(R.id.edtDoB);
        edtBirthdate.setOnClickListener((v) -> {
            ((TextInputLayout) view.findViewById(R.id.birthdateLayout)).setErrorEnabled(false);
            edtBirthdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            Calendar currentDate = Calendar.getInstance();
            SpinnerDatePickerDialogBuilder datepicker = new SpinnerDatePickerDialogBuilder()
                    .context(getContext())
                    .callback(this)
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .maxDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
                    .minDate(currentDate.get(Calendar.YEAR) - 100, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            if (edtBirthdate.getText().toString().trim().isEmpty()) {
                datepicker.defaultDate(currentDate.get(Calendar.YEAR) - 15, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = sdf.parse(edtBirthdate.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datepicker.defaultDate(date.getYear() + 1900, date.getMonth(), date.getDate());
            }
            datepicker.build().show();
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        edtBirthdate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
    }
}
