package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileOwnFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private MaterialButton btnBirthdate;
    private MaterialButton btnPhone;
    private MaterialButton btnGender;
    private MaterialButton btnChangeProfile;
    private MaterialButton btnCancelChange;
    private ProgressBar updateProgressbar;
    private ConstraintLayout layout;

    private String birth;
    private String phoneNum;
    private String gender;
    private boolean isChange = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_own, container, false);
        btnCancelChange = (MaterialButton) view.findViewById(R.id.btnCancelChange);
        btnBirthdate = (MaterialButton) view.findViewById(R.id.birthdate);
        updateProgressbar = (ProgressBar) view.findViewById(R.id.updateProgressbar);
        btnPhone = (MaterialButton) view.findViewById(R.id.phone);
        btnGender = (MaterialButton) view.findViewById(R.id.gender);
        btnChangeProfile = (MaterialButton) view.findViewById(R.id.btnChangeProfile);
        layout = (ConstraintLayout) view.findViewById(R.id.layout);
        disableUpdate();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        disableUpdate();
        ProfileMemberActivity activity = (ProfileMemberActivity) getActivity();

        birth = activity.getBirthdate();
        btnBirthdate.setText(birth);

        phoneNum = activity.getPhone();
        if (phoneNum == null || phoneNum.isEmpty()) {
            phoneNum = "Chưa có";
        }
        btnPhone.setText(phoneNum);

        gender = activity.getGender();
        btnGender.setText(gender);

        btnChangeProfile.setOnClickListener((v) -> {
            if (isChange) {
                btnChangeProfile.setTextColor(Color.parseColor("#00ffffff"));
                animationLoading();
                APIService mWebService = APIUtils.getWebService();
                new Handler().postDelayed(() -> {
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    try {
                        builder.addFormDataPart("birthdate", DateTimeUtils.formatISO(btnBirthdate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        builder.addFormDataPart("birthdate", "1990-12-23T17:00:00.000Z");
                    }
                    builder.addFormDataPart("id", ((ProfileMemberActivity) getActivity()).getId() + "");
                    builder.addFormDataPart("firstname", ((ProfileMemberActivity) getActivity()).getFirstName());
                    builder.addFormDataPart("lastname", ((ProfileMemberActivity) getActivity()).getLastName());
                    builder.addFormDataPart("phone", btnPhone.getText().toString().equalsIgnoreCase("chưa có") ? "" : btnPhone.getText().toString());
                    builder.addFormDataPart("gender", btnGender.getText().toString().equalsIgnoreCase("nam") ? "0" : "1");
                    builder.addFormDataPart("sosContact", "null");
                    builder.addFormDataPart("facebookAuth", "null");
                    String newAvatar = ((ProfileMemberActivity) getActivity()).getNewAvatar();
                    String newCover = ((ProfileMemberActivity) getActivity()).getNewCover();
                    boolean hasAvatar = false;
                    if (newAvatar !=null && !newAvatar.isEmpty()) {
                        File file = new File(newAvatar);
                        RequestBody medias = RequestBody.create(MediaType.parse("image"), file);
                        builder.addFormDataPart("medias", "medias", medias);
                        builder.addFormDataPart("avatar", "true");
                        hasAvatar = true;
                        ((ProfileMemberActivity) getActivity()).setNewAvatar("");
                        ((ProfileMemberActivity) getActivity()).setAvatar(newAvatar);
                    }

                    if (newCover != null && !newCover.isEmpty()) {
                        File file = new File(newCover);
                        RequestBody medias = RequestBody.create(MediaType.parse("image"), file);
                        builder.addFormDataPart("medias", "medias", medias);
                        hasAvatar = true;
                        ((ProfileMemberActivity) getActivity()).setNewCover("");
                        ((ProfileMemberActivity) getActivity()).setCover(newCover);
                    }
                    if (hasAvatar) {
                        mWebService.updateUserProfileWithAvatar(Session.getToken(getActivity()), builder.build()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                stopAnimation();
                                User user = response.body();
                                if (user != null) {
                                    Snackbar.make(layout, "Thay đổi thành công", Toast.LENGTH_SHORT).show();
                                    user.setToken(Session.getToken(getActivity()));
                                    Session.setSession(getActivity(), user);
                                } else {
                                    Snackbar.make(layout, "Thay đổi thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                stopAnimation();
                                Snackbar.make(layout, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mWebService.updateUserProfile(Session.getToken(getActivity()), builder.build()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                stopAnimation();
                                User user = response.body();
                                if (user != null) {
                                    Snackbar.make(layout, "Thay đổi thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(layout, "Thay đổi thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                stopAnimation();
                                Snackbar.make(layout, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 1000);
            } else {
                btnBirthdate.setClickable(true);
                btnBirthdate.setTextColor(Color.parseColor("#00c853"));

                btnPhone.setClickable(true);
                btnPhone.setTextColor(Color.parseColor("#00c853"));

                btnGender.setClickable(true);
                btnGender.setTextColor(Color.parseColor("#00c853"));

                btnChangeProfile.setText("Lưu chỉnh sửa");
                ColorStateList stateList = new ColorStateList(
                        new int[][]{{}}, new int[]{Color.parseColor("#00c853")});
                btnChangeProfile.setBackgroundTintList(stateList);
                btnChangeProfile.setTextColor(Color.parseColor("#ffffff"));

                ((ProfileMemberActivity) getActivity()).enableUpdate();
                btnCancelChange.setVisibility(View.VISIBLE);
                isChange = true;
            }
        });

        btnCancelChange.setOnClickListener((v) -> {
            disableUpdate();
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
                String phone = btnPhone.getText().toString();
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
                                inputPhone = inputPhone.replaceAll("-", "");
                                inputPhone = inputPhone.replaceAll(".", "");
                                inputPhone = inputPhone.replaceAll(" ", "");
                                if (!inputPhone.isEmpty() && inputPhone.matches(pattern)) {
                                    btnPhone.setText(inputPhone);
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(getContext(), "Không hợp lệ", Toast.LENGTH_LONG).show();
                                    btnPhone.setText(phone);
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
                    female.setChecked(true);
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

    public void disableUpdate() {

        btnBirthdate.setClickable(false);
        btnBirthdate.setTextColor(Color.parseColor("#99A1AC"));

        btnPhone.setClickable(false);
        btnPhone.setTextColor(Color.parseColor("#99A1AC"));

        btnGender.setClickable(false);
        btnGender.setTextColor(Color.parseColor("#99A1AC"));

        btnChangeProfile.setText("Lưu chỉnh sửa");
        final ColorStateList stateList = new ColorStateList(
                new int[][]{{}}, new int[]{Color.parseColor("#FFFFFF")});
        btnChangeProfile.setBackgroundTintList(stateList);
        btnChangeProfile.setTextColor(Color.parseColor("#00c853"));
        btnChangeProfile.setText("Sửa thông tin");

        ((ProfileMemberActivity) getActivity()).disableUpdate();
        btnCancelChange.setVisibility(View.GONE);
        isChange = false;

    }

    private void animationLoading() {
        ValueAnimator anim = ValueAnimator.ofInt(LocalDisplay.getScreenWidth(getContext()) - LocalDisplay.dp2px(64, getContext()),
                LocalDisplay.dp2px(45, getContext()));

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnChangeProfile.getLayoutParams();
                layoutParams.width = val;
                btnChangeProfile.setLayoutParams(layoutParams);
                btnChangeProfile.requestLayout();
            }
        });
        anim.setDuration(100);
        anim.start();

        updateProgressbar.animate().alpha(1f).setStartDelay(100).start();
        updateProgressbar.setVisibility(View.VISIBLE);
    }

    private void stopAnimation() {
        updateProgressbar.setVisibility(View.INVISIBLE);
        updateProgressbar.animate().alpha(0f).setDuration(1).start();
        int width = LocalDisplay.getScreenWidth(getContext()) - LocalDisplay.dp2px(64, getContext());
        ValueAnimator anim = ValueAnimator.ofInt(LocalDisplay.dp2px(45, getContext()), width);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnChangeProfile.getLayoutParams();
                layoutParams.width = val;
                btnChangeProfile.setLayoutParams(layoutParams);
                btnChangeProfile.requestLayout();
                if (val == width) {
                    btnChangeProfile.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });
        anim.setDuration(100);
        anim.start();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        btnBirthdate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
    }
}
