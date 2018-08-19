package com.viettrekker.mountaintrekkingadviser.controller.plan;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostAddActivity;
import com.viettrekker.mountaintrekkingadviser.model.CheckList;
import com.viettrekker.mountaintrekkingadviser.model.ChecklistItem;
import com.viettrekker.mountaintrekkingadviser.model.Group;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.SearchMember;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPlanActivity extends AppCompatActivity{
    EditText edtPlanName;
    MaterialButton btnStartLoc;
    TextView tvDateStart;
    TextView tvDurationDate;
    TextView tvDistance;
    TextView tvDateEnd;
    TextView tvMemberCount;
    RecyclerView rcvListMembersPlan;
    Switch swPublic;
    Button btnTimeLines;
    Button btnCheckList;
    RelativeLayout layoutMembers;
    ImageView imgAddMember;
    MaterialButton btnStartDate;
    MaterialButton btnStartTime;
    MaterialButton btnEndDate;
    MaterialButton btnEndTime;
    TextView tvEndTime;
    TextView tvStartTime;
    MaterialButton btnEndLoc;
    TextView tvNewPlan;

    int placeId;
    Calendar startDate;
    Calendar endDate;
    List<User> allMember;
    List<Member> members;
    Plan plan;
    ArrayList<Place> places;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static List<ChecklistItem> checkLists = new ArrayList<>();
    public static List<TimeLines> timeLines  = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);
        APIService mWebService = APIUtils.getWebService();
        edtPlanName = (EditText) findViewById(R.id.edtPlanName);
        btnStartLoc = (MaterialButton) findViewById(R.id.btnStartLoc);
        tvDateStart = (TextView) findViewById(R.id.tvDateStart);
        tvDurationDate = (TextView) findViewById(R.id.tvDurationDate);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDateEnd = (TextView) findViewById(R.id.tvDateEnd);
        tvMemberCount = (TextView) findViewById(R.id.tvMemberCount);
        rcvListMembersPlan = (RecyclerView) findViewById(R.id.rcvListMembersPlan);
        swPublic = (Switch) findViewById(R.id.swPublic);
        btnTimeLines = (Button) findViewById(R.id.btnTimeLines);
        btnCheckList = (Button) findViewById(R.id.btnCheckList);
        layoutMembers = (RelativeLayout) findViewById(R.id.layoutViewMember);
        imgAddMember = (ImageView) findViewById(R.id.imgAddMember);
        btnStartDate = (MaterialButton) findViewById(R.id.btnStartDate);
        btnStartTime = (MaterialButton) findViewById(R.id.btnStartTime);
        btnEndDate = (MaterialButton) findViewById(R.id.btnEndDate);
        btnEndTime = (MaterialButton) findViewById(R.id.btnEndTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        btnEndLoc = (MaterialButton) findViewById(R.id.btnEndLoc);
        tvNewPlan = (TextView) findViewById(R.id.tvNewPlan);
        members = new ArrayList<>();
        timeLines = new ArrayList<>();
        checkLists = new ArrayList<>();
        btnStartLoc.requestFocus();
        edtPlanName.setFocusable(false);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        Member m = new Member();
        m.setUserId(MainActivity.user.getId());
        m.setRoleInGroupId(1);
        m.setVehicule("");
        members.add(m);
        rcvListMembersPlan.setLayoutManager(new LinearLayoutManager(NewPlanActivity.this));
        MembersListAdapter membersListAdapter = new MembersListAdapter(NewPlanActivity.this);
        rcvListMembersPlan.setVisibility(View.GONE);
        membersListAdapter.setUsers(members);
        membersListAdapter.notifyDataSetChanged();
        rcvListMembersPlan.setNestedScrollingEnabled(false);
        rcvListMembersPlan.setAdapter(membersListAdapter);

        layoutMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcvListMembersPlan.getVisibility() == View.GONE){
                    rcvListMembersPlan.setVisibility(View.VISIBLE);
                } else {
                    rcvListMembersPlan.setVisibility(View.GONE);
                }
            }
        });

        imgAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcvListMembersPlan.getVisibility() == View.GONE){
                    rcvListMembersPlan.setVisibility(View.VISIBLE);
                }
                new SimpleSearchDialogCompat(NewPlanActivity.this, "Tìm kiếm bạn đồng hành",
                        "Nhập tên có dấu...", null, createMemberData(),
                        new SearchResultListener<SearchMember>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchMember searchMember, int i) {
                                Member mem = new Member();
                                mem.setRoleInGroupId(3);
                                mem.setUserId(searchMember.getId());
                                mem.setVehicule("");
                                if (!checkMemberExist(mem)){
                                    members.add(mem);
                                    membersListAdapter.notifyDataSetChanged();
                                    tvMemberCount.setText(members.size() + " người");
                                } else {
                                    Toast.makeText(NewPlanActivity.this,"Bạn không thể mời thêm thành viên này!",Toast.LENGTH_SHORT).show();
                                }

                                baseSearchDialogCompat.dismiss();
                            }
                        }).show();

            }
        });
        allMember = new ArrayList<>();
        mWebService.searchMember(MainActivity.user.getToken(),"").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                allMember = response.body();
                allMember.remove(0);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
        places = new ArrayList<>();
        mWebService.searchPlace(MainActivity.user.getToken(),1,100,"id","").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                places = response.body();
                places.remove(0);
            }

            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {

            }
        });
        //Place picker
        btnEndLoc.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 new SimpleSearchDialogCompat(NewPlanActivity.this, "Tìm kiếm địa điểm",
                         "Nhập tên địa điểm có dấu...", null, createSampleData(),
                         new SearchResultListener<SearchPlace>() {
                             @Override
                             public void onSelected(BaseSearchDialogCompat dialog, SearchPlace item, int position) {
                                 btnEndLoc.setText(item.getTitle());
                                 placeId = item.getId();
                                 dialog.dismiss();
                             }
                         }).show();
             }
         });

         btnTimeLines.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(NewPlanActivity.this, TimeLinesActivity.class);
                 intent.putExtra("state","new");
                 startActivity(intent);
             }
         });
         btnCheckList.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(NewPlanActivity.this, ChecklistActivity.class);
                 intent.putExtra("state", "new");
                 startActivity(intent);
             }
         });
        btnStartLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStartLoc.requestFocus();
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(NewPlanActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
         //Datepicker
         btnStartDate.setOnClickListener((v) -> datePick(1));
         btnStartTime.setOnClickListener((v) -> timePick(1));
        btnEndDate.setOnClickListener((v) -> datePick(2));
        btnEndTime.setOnClickListener((v) -> timePick(2));
        tvNewPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = 0;
                plan = new Plan();
                Group g = new Group();
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(NewPlanActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                alertDialogBuilder.setTitle("Cảnh báo");
                if (tvStartTime.getText().toString().equalsIgnoreCase("--:--") || tvDateStart.getText().toString().equalsIgnoreCase("--/--/----")){
                    alertDialogBuilder.setMessage("Vui lòng chọn thời gian khởi hành")
                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else
                    if (tvEndTime.getText().toString().equalsIgnoreCase("--:--") || tvDateEnd.getText().toString().equalsIgnoreCase("--/--/----")){
                        alertDialogBuilder.setMessage("Vui lòng chọn thời gian hoàn thành")
                                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    } else {
                    check++;
                       Date d1 = startDate.getTime();
                       Date d2 = endDate.getTime();
                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                       plan.setStartTime(sdf.format(d1));
                       plan.setFinishTime(sdf.format(d2));
//                       Toast.makeText(NewPlanActivity.this,plan.getStartTime(),Toast.LENGTH_LONG).show();
                    }
                    if (!edtPlanName.getText().toString().isEmpty()){
                        check++;
                        g.setName(edtPlanName.getText().toString());
                    }

                    g.setMembers(members);
                plan.setGroup(g);
                if (swPublic.isChecked()){
                    plan.setIsPublic(1);
                }else {
                    plan.setIsPublic(0);
                }
                plan.setState(0);
                CheckList cl = new CheckList();
                cl.setDescription("");
                cl.setItems(checkLists);
                plan.setChecklist(cl);
                Gson gson = new Gson();
                String jsonTimeline = gson.toJson(timeLines);
                plan.setTimelines(jsonTimeline);
//                String json = gson.toJson(plan);
//                JsonElement jelement = new JsonParser().parse(json);
//                JsonObject jobject = jelement.getAsJsonObject();
//                jobject.remove("id");
//                jobject.getAsJsonObject("group").remove("id");
//                JsonArray jsonArray =  jobject.getAsJsonObject("group").getAsJsonArray("members");
//                JsonObject jobject1;
//                for (int i = 0; i < jsonArray.size(); i++ ){
//                    jobject1 = jsonArray.get(i).getAsJsonObject();
//                    jobject1.remove("lastName");
//                    jobject1.remove("firstName");
//                    jobject.getAsJsonObject("group").getAsJsonArray("members").remove(i);
//                    jobject.getAsJsonObject("group").getAsJsonArray("members").add(jobject1);
//                }
//                JsonObject je = jobject.getAsJsonObject("group").getAsJsonArray("members").get(0).getAsJsonObject();
//                je.remove("lastName");
//                je.remove("firstName");
//                jobject.getAsJsonObject("group").getAsJsonArray("members").remove(0);
//                jobject.getAsJsonObject("group").getAsJsonArray("members").add(je);
//                System.out.println("test thu : " + jobject.toString());


                if (check >= 2) {
                    mWebService.createPlan(MainActivity.user.getToken(), plan).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            Toast.makeText(NewPlanActivity.this, "Thành công", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            Toast.makeText(NewPlanActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }

    private void datePick(int index) {
        Calendar currentDate = Calendar.getInstance();
        switch(index) {
            case 1 :
                SpinnerDatePickerDialogBuilder datepicker = new SpinnerDatePickerDialogBuilder()
                        .context(NewPlanActivity.this)
                        .callback(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvDateStart.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
                                startDate.set(Calendar.YEAR,year);
                                startDate.set(Calendar.MONTH,monthOfYear);
                                startDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                            }
                        })
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .maxDate(currentDate.get(Calendar.YEAR) + 1, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
                        .minDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                datepicker.defaultDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datepicker.build().show();
                break;
            case 2:
                SpinnerDatePickerDialogBuilder datepicker1 = new SpinnerDatePickerDialogBuilder()
                        .context(NewPlanActivity.this)
                        .callback(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvDateEnd.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
                                endDate.set(Calendar.YEAR,year);
                                endDate.set(Calendar.MONTH,monthOfYear);
                                endDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                            }
                        })
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .maxDate(currentDate.get(Calendar.YEAR) + 1, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
                        .minDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                datepicker1.defaultDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datepicker1.build().show();
                break;
        }
    }

    private void timePick(int index){
        Calendar currentDate = Calendar.getInstance();
        switch (index){
            case 1 :

                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            startDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            startDate.set(Calendar.MINUTE,minute);
                            currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            currentDate.set(Calendar.MINUTE, minute);
                            tvStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewPlanActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        myTimeListener, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);
                timePickerDialog.setTitle("Chọn giờ");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
                break;
            case 2:
                Calendar currentDate1 = Calendar.getInstance();
                TimePickerDialog.OnTimeSetListener myTimeListener1 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            currentDate.set(Calendar.MINUTE, minute);
                            endDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            endDate.set(Calendar.MINUTE,minute);
                            tvEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                        }
                    }
                };
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(NewPlanActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        myTimeListener1, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);
                timePickerDialog1.setTitle("Chọn giờ");
                timePickerDialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog1.show();
        }

    }

    private ArrayList<SearchPlace> createSampleData(){
        ArrayList<SearchPlace> items = new ArrayList<>();
        for (Place p : places) {
            items.add(new SearchPlace(p.getName(), p.getId()));
        }
        return items;
    }
    private ArrayList<SearchMember> createMemberData(){
        ArrayList<SearchMember> items = new ArrayList<>();
        for (User p : allMember) {
            String title = p.getFirstName() + " " + p.getLastName();
            items.add(new SearchMember(title,  p.getId(), p.getFirstName(), p.getLastName()));
        }
        return items;
    }
    private boolean checkMemberExist(Member m){
        for (Member memb: members) {
            if (memb.getUserId()==m.getUserId())
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this,data);
                btnStartLoc.setText("duoc roi");
            } else  if (resultCode == RESULT_CANCELED){
                btnStartLoc.setText("chua duoc");
            }
        } else {
            btnStartLoc.setText("chua duoc nua");
        }
    }
}
