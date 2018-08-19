package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeLinesActivity  extends AppCompatActivity{
    APIService mWebService;
    RecyclerView rcvTimelineList;
    EditText edtTimelineTitle;
    EditText edtTimelineContent;
    ImageView btnAddTimeline;
    Button btnTLDate;
    Button btnTLTime;
    TextView tvTLTime;
    int id;
    String state= "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_details);
        mWebService = APIUtils.getWebService();
        edtTimelineTitle = (EditText) findViewById(R.id.edtTimelineTitle);
        edtTimelineContent = (EditText) findViewById(R.id.edtTimelineContent);
        btnAddTimeline = (ImageView) findViewById(R.id.btnAddTimeline);
        btnTLDate = (Button) findViewById(R.id.btnTLDate);
        btnTLTime = (Button) findViewById(R.id.btnTLTime);
        tvTLTime = (TextView) findViewById(R.id.tvTLTime);

        state = getIntent().getStringExtra("state") == null ? "" : getIntent().getStringExtra("state");
        id = getIntent().getIntExtra("id",-1);
        edtTimelineTitle.setVisibility(View.GONE);
        edtTimelineContent.setVisibility(View.GONE);
        btnAddTimeline.setVisibility(View.GONE);
        rcvTimelineList = findViewById(R.id.rcvTimelineList);
        NewPlanActivity newPlanActivity = new NewPlanActivity();
        TimelinesListAdapter timelinesListAdapter = new TimelinesListAdapter(TimeLinesActivity.this);
        rcvTimelineList.setLayoutManager(new LinearLayoutManager(this));
        rcvTimelineList.setNestedScrollingEnabled(false);
        rcvTimelineList.setAdapter(timelinesListAdapter);

        if (id > -1){
            mWebService.getPlanById(MainActivity.user.getToken(),id).enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    String timeline = response.body().getTimelines();
                    Type type = new TypeToken<ArrayList<TimeLines>>(){}.getType();
                    Gson gson = new Gson();
                    List<TimeLines> timeLines = gson.fromJson(timeline, type);
                    timelinesListAdapter.setList(timeLines);
                    timelinesListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {
                    Toast.makeText(TimeLinesActivity.this,"Có lỗi xảy ra vui lòng thử lại...",Toast.LENGTH_LONG).show();
                }
            });

        }

        if (state.equalsIgnoreCase("new")){
            edtTimelineTitle.setVisibility(View.VISIBLE);
            edtTimelineContent.setVisibility(View.VISIBLE);
            btnAddTimeline.setVisibility(View.VISIBLE);
            timelinesListAdapter.setList(newPlanActivity.timeLines);
            timelinesListAdapter.notifyDataSetChanged();
            btnTLDate.setOnClickListener((v) -> datePick());
            btnTLTime.setOnClickListener((v) -> timePick());
            btnAddTimeline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO here
                    TimeLines t = new TimeLines();
                    t.setName(edtTimelineTitle.getText().toString());
                    t.setContent(edtTimelineContent.getText().toString());
                    t.setTime(tvTLTime.getText().toString());
                    newPlanActivity.timeLines.add(t);
                    timelinesListAdapter.setList(newPlanActivity.timeLines);
                    timelinesListAdapter.notifyDataSetChanged();
                }
            });

        }




    }
    private void datePick(){
        Calendar currentDate = Calendar.getInstance();
        SpinnerDatePickerDialogBuilder datepicker = new SpinnerDatePickerDialogBuilder()
                .context(TimeLinesActivity.this)
                .callback(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tvTLTime.setText(String.format("%d-%02d-%02d",year , monthOfYear + 1, dayOfMonth));
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
                    tvTLTime.append(String.format(" %02d:%02d:00", hourOfDay, minute));

                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(TimeLinesActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                myTimeListener, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Chọn giờ");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }
}
