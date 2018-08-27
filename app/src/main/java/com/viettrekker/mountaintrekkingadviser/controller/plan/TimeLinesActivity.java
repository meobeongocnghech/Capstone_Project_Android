package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeLinesActivity  extends AppCompatActivity{
    private APIService mWebService;
    private RecyclerView rcvTimelineList;
    private EditText edtTimelineTitle;
    private EditText edtTimelineContent;
    private MaterialButton btnAddTimeline;
    private MaterialButton btnTLDate;
    private MaterialButton btnTLTime;
    private TextView tvTLTime;
    private TextView tvTLDate;
    private int id;
    private String state= "";

    private Plan plan;
    private String token;

    List<TimeLines> timeLines;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timelinesToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWebService = APIUtils.getWebService();
        edtTimelineTitle = (EditText) findViewById(R.id.edtTimelineTitle);
        edtTimelineContent = (EditText) findViewById(R.id.edtTimelineContent);
        btnAddTimeline = (MaterialButton) findViewById(R.id.btnAddTimeline);
        btnTLDate = (MaterialButton) findViewById(R.id.btnTLDate);
        btnTLTime = (MaterialButton) findViewById(R.id.btnTLTime);
        tvTLTime = (TextView) findViewById(R.id.tvTLTime);
        tvTLDate = (TextView) findViewById(R.id.tvTLDate);

        state = getIntent().getStringExtra("state") == null ? "" : getIntent().getStringExtra("state");
        id = getIntent().getIntExtra("id",-1);
        edtTimelineTitle.setVisibility(View.GONE);
        edtTimelineContent.setVisibility(View.GONE);
        btnAddTimeline.setVisibility(View.GONE);
        btnTLDate.setVisibility(View.GONE);
        btnTLTime.setVisibility(View.GONE);
        rcvTimelineList = findViewById(R.id.rcvTimelineList);
        NewPlanActivity newPlanActivity = new NewPlanActivity();
        TimelinesListAdapter timelinesListAdapter = new TimelinesListAdapter(TimeLinesActivity.this);
        rcvTimelineList.setLayoutManager(new LinearLayoutManager(this));
        rcvTimelineList.setNestedScrollingEnabled(false);
        rcvTimelineList.setAdapter(timelinesListAdapter);

        token = getIntent().getStringExtra("token");

        MaterialButton btnEditTimeline = (MaterialButton) findViewById(R.id.btnEditTimeline);
        btnEditTimeline.setOnClickListener((v) -> {
            if (btnEditTimeline.getText().toString().equalsIgnoreCase("sửa")) {
                edtTimelineTitle.setVisibility(View.VISIBLE);
                edtTimelineContent.setVisibility(View.VISIBLE);
                btnAddTimeline.setVisibility(View.VISIBLE);
                btnTLDate.setVisibility(View.VISIBLE);
                btnTLTime.setVisibility(View.VISIBLE);
                btnEditTimeline.setText("Xong");
            } else {
                edtTimelineTitle.setVisibility(View.GONE);
                edtTimelineContent.setVisibility(View.GONE);
                btnAddTimeline.setVisibility(View.GONE);
                btnTLDate.setVisibility(View.GONE);
                btnTLTime.setVisibility(View.GONE);
                btnEditTimeline.setText("Sửa");
                tvTLDate.setVisibility(View.INVISIBLE);
                tvTLDate.setText("");
                tvTLTime.setVisibility(View.INVISIBLE);
                tvTLTime.setText("");

                plan.setTimelines(new Gson().toJson(timeLines));
                Log.d("ABC", new Gson().toJson(plan));
                mWebService.updatePlan(token, plan).enqueue(new Callback<Plan>() {
                    @Override
                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                        Toast.makeText(TimeLinesActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Plan> call, Throwable t) {
                        Toast.makeText(TimeLinesActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (id > -1){
            mWebService.getPlanById(token,id).enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    plan = response.body();
                    String timeline = response.body().getTimelines();
                    Type type = new TypeToken<ArrayList<TimeLines>>(){}.getType();
                    Gson gson = new Gson();
                    timeLines = gson.fromJson(timeline, type);
                    timelinesListAdapter.setList(timeLines);
                    timelinesListAdapter.sortTimelines();
                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {
                    Toast.makeText(TimeLinesActivity.this,"Có lỗi xảy ra vui lòng thử lại...",Toast.LENGTH_LONG).show();
                }
            });

        }

        if (state.equalsIgnoreCase("new")) {
            edtTimelineTitle.setVisibility(View.VISIBLE);
            edtTimelineContent.setVisibility(View.VISIBLE);
            btnAddTimeline.setVisibility(View.VISIBLE);
            btnTLDate.setVisibility(View.VISIBLE);
            btnTLTime.setVisibility(View.VISIBLE);
            btnEditTimeline.setVisibility(View.GONE);
            timelinesListAdapter.setList(newPlanActivity.timeLines);
            timelinesListAdapter.sortTimelines();
        }
        btnTLDate.setOnClickListener((v) -> datePick());
        btnTLTime.setOnClickListener((v) -> timePick());

        btnAddTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO here
                TimeLines t = new TimeLines();
                t.setName(edtTimelineTitle.getText().toString());
                t.setContent(edtTimelineContent.getText().toString());
                t.setTime(tvTLDate.getText().toString() + tvTLTime.getText().toString());
                if (state.equalsIgnoreCase("new")) {
                    newPlanActivity.timeLines.add(t);
                } else {
                    timeLines.add(t);
                }
                timelinesListAdapter.sortTimelines();
            }
        });
    }
    private void datePick(){
        Calendar currentDate = Calendar.getInstance();
        SpinnerDatePickerDialogBuilder datepicker = new SpinnerDatePickerDialogBuilder()
                .context(TimeLinesActivity.this)
                .callback(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tvTLDate.setText(String.format("%d-%02d-%02d",year , monthOfYear + 1, dayOfMonth));
                        tvTLDate.setVisibility(View.VISIBLE);
                    }
                })
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .maxDate(currentDate.get(Calendar.YEAR) + 1, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
                .minDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        datepicker.defaultDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        datepicker.build().show();
    }

    private void timePick() {
        Calendar currentDate = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    tvTLTime.setText(String.format(" %02d:%02d:00", hourOfDay, minute));
                    tvTLTime.setVisibility(View.VISIBLE);
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(TimeLinesActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                myTimeListener, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Chọn giờ");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
