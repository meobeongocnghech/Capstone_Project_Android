package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.viettrekker.mountaintrekkingadviser.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileOwnFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private MaterialButton btnBirthdate;
    private MaterialButton btnPhone;
    private MaterialButton btnGender;
    private MaterialButton btnChangeProfile;

    private String birth;
    private String phoneNum;
    private String gender;
    private boolean isChange = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_own, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ProfileMemberActivity activity = (ProfileMemberActivity) getActivity();
        btnBirthdate = (MaterialButton) view.findViewById(R.id.birthdate);
        birth = activity.getBirthdate();
        btnBirthdate.setText(birth);

        btnPhone = (MaterialButton) view.findViewById(R.id.phone);
        phoneNum = activity.getPhone();
        btnPhone.setText(phoneNum);

        btnGender = (MaterialButton) view.findViewById(R.id.gender);
        gender = activity.getGender();
        btnGender.setText(gender);

        btnChangeProfile = (MaterialButton) view.findViewById(R.id.btnChangeProfile);
        btnChangeProfile.setOnClickListener((v) -> {
            if (isChange) {

            } else {
                btnBirthdate.setClickable(true);
                btnBirthdate.setTextColor(Color.parseColor("#00c853"));

                btnPhone.setClickable(true);
                btnPhone.setTextColor(Color.parseColor("#00c853"));

                btnGender.setClickable(true);
                btnGender.setTextColor(Color.parseColor("#00c853"));

                btnChangeProfile.setText("Lưu chỉnh sửa");
                final ColorStateList stateList = new ColorStateList(
                        new int[][]{{}}, new int[]{Color.parseColor("#00c853")});
                btnChangeProfile.setBackgroundTintList(stateList);
                btnChangeProfile.setTextColor(Color.parseColor("#ffffff"));
                isChange = true;
            }
        });

        btnBirthdate.setOnClickListener((v) -> update(0));
        btnPhone.setOnClickListener((v) -> update(1));
        btnGender.setOnClickListener((v) -> update(2));
    }

    private void update(int index) {
        switch (index) {
            case 0:
                Calendar currentDate = Calendar.getInstance();
                SpinnerDatePickerDialogBuilder datepicker = new SpinnerDatePickerDialogBuilder()
                        .context(getContext())
                        .callback(this)
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .maxDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
                        .minDate(currentDate.get(Calendar.YEAR) - 100, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = sdf.parse(btnBirthdate.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datepicker.defaultDate(date.getYear() + 1900, date.getMonth(), date.getDate());
                datepicker.build().show();
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Nhập số điện thoại");
                final EditText input = new EditText(getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);
                builder.setCancelable(false)
                        .setPositiveButton("Hoàn tất", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String inputPhone = input.getText().toString().trim();
                                String pattern = "/^(01[2689]|09)[0-9]{8}$/";
                                pattern = pattern.replaceAll("-", "");
                                pattern = pattern.replaceAll(".", "");
                                pattern = pattern.replaceAll(" ", "");
                                if (inputPhone.matches(pattern)) {
                                    btnPhone.setText(inputPhone);
                                    dialog.cancel();
                                } else {
                                    input.setError("Không hợp lệ", getResources().getDrawable(R.drawable.error_icon, null));
                                }
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                break;
            case 2:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle("Chọn giới tính");
                final RadioButton male = new RadioButton(getContext());
                male.setText("Nam");
                final RadioButton female = new RadioButton(getContext());
                female.setText("Nữ");

                if (btnGender.getText().toString().trim().equalsIgnoreCase("Nam")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(false);
                }

                male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            female.setChecked(false);
                        }
                    }
                });
                female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            male.setChecked(false);
                        }
                    }
                });
//                male.setOnClickListener((v) -> {
//                    if (!male.isChecked()) {
//                        male.setChecked(true);
//                        female.setChecked(false);
//                    }
//                });
//
//                female.setOnClickListener((v) -> {
//                    if (!female.isChecked()) {
//                        female.setChecked(true);
//                        male.setChecked(false);
//                    }
//                });

                final RadioGroup group = new RadioGroup(getContext());
                group.setOrientation(RadioGroup.HORIZONTAL);
                group.addView(male);
                group.addView(female);
                builder1.setView(group);

//                male.getLayoutParams().height = -2;
//                male.getLayoutParams().width = -2;
//                male.setGravity(Gravity.CENTER_VERTICAL);
//                male.requestLayout();
//                female.getLayoutParams().height = -2;
//                female.getLayoutParams().width = -2;
//                female.setGravity(Gravity.CENTER_VERTICAL);
//                female.requestLayout();
//
//                group.getLayoutParams().width = - 1;
//                group.getLayoutParams().height = -2;
//                group.requestLayout();


                builder1.setCancelable(false)
                        .setPositiveButton("Hoàn tất", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (female.isChecked()) {
                                    btnGender.setText("Nữ");
                                } else {
                                    btnGender.setText("Nam");
                                }
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog1 = builder1.create();

                alertDialog1.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        btnBirthdate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
    }
}
