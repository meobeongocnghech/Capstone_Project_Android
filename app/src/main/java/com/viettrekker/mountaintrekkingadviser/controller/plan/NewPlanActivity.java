package com.viettrekker.mountaintrekkingadviser.controller.plan;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.CheckList;
import com.viettrekker.mountaintrekkingadviser.model.ChecklistItem;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.Group;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.PlanLocation;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.Session;
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

public class NewPlanActivity extends AppCompatActivity {
    private EditText edtPlanName;
    private MaterialButton btnStartLoc;
    private TextView tvDateStart;
    private TextView tvDurationDate;
    private TextView tvDistance;
    private TextView tvDateEnd;
    private Switch swPublic;
    private Button btnTimeLines;
    private Button btnCheckList;
    private MaterialButton btnStartDateTime;
    private MaterialButton btnEndDateTime;
    private TextView tvEndTime;
    private TextView tvStartTime;
    private MaterialButton btnEndLoc;
    private TextView tvNewPlan;

    private int placeId;
    private Calendar startDate;
    private Calendar endDate;
    private Plan plan;
    private ArrayList<Place> places;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static List<ChecklistItem> checkLists;
    public static List<TimeLines> timeLines;

    private boolean pickedSourceLocation = false;
    private boolean pickedDestinationLocation = false;
    private PlanLocation srcLocation = new PlanLocation();
    private PlanLocation desLocation = new PlanLocation();

    private boolean setStart = false;
    private boolean setEnd = false;

    private String token;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            if (setEnd) {
                try {
                    if (date.before(startDate.getTime())) {
                        Toast.makeText(NewPlanActivity.this, "Lỗi: Sớm hơn gian thời gian xuất phát", Toast.LENGTH_LONG).show();
                    } else {
                        tvEndTime.setText(DateTimeUtils.parseStringTime(date));
                        tvDateEnd.setText(DateTimeUtils.parseStringDate(date));
                        endDate.setTime(date);

                        tvDurationDate.setText(DateTimeUtils.caculatorStringTime(startDate.getTime(), endDate.getTime()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else if (setStart) {
                tvStartTime.setText(DateTimeUtils.parseStringTime(date));
                tvDateStart.setText(DateTimeUtils.parseStringDate(date));
                startDate.setTime(date);

                if (startDate.after(endDate)) {
                    endDate.setTime(startDate.getTime());

                    tvEndTime.setText(DateTimeUtils.parseStringTime(endDate.getTime()));
                    tvDateEnd.setText(DateTimeUtils.parseStringDate(endDate.getTime()));

                    try {
                        tvDurationDate.setText(DateTimeUtils.caculatorStringTime(startDate.getTime(), endDate.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onDateTimeCancel() {
            if (setEnd) {
                setEnd = false;
            } else if (setStart) {
                setStart = false;
            }
        }
    };

    private static final String TAG = NewPlanActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addPlanToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        APIService mWebService = APIUtils.getWebService();
        edtPlanName = (EditText) findViewById(R.id.edtPlanName);
        btnStartLoc = (MaterialButton) findViewById(R.id.btnStartLoc);
        tvDateStart = (TextView) findViewById(R.id.tvDateStart);
        tvDurationDate = (TextView) findViewById(R.id.tvDurationDate);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDateEnd = (TextView) findViewById(R.id.tvDateEnd);
        swPublic = (Switch) findViewById(R.id.swPublic);
        btnTimeLines = (Button) findViewById(R.id.btnTimeLines);
        btnCheckList = (Button) findViewById(R.id.btnCheckList);
        btnStartDateTime = (MaterialButton) findViewById(R.id.btnStartDateTime);
        btnEndDateTime = (MaterialButton) findViewById(R.id.btnEndDateTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        btnEndLoc = (MaterialButton) findViewById(R.id.btnEndLoc);
        tvNewPlan = (TextView) findViewById(R.id.tvNewPlan);
        timeLines = new ArrayList<>();
        checkLists = new ArrayList<>();

        token = getIntent().getStringExtra("token");

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        Member m = new Member();
        m.setUserId(Session.getUserId(this));
        m.setRoleInGroupId(1);
        m.setVehicule("");

        places = new ArrayList<>();
        mWebService.searchPlace(token, 1, 100, "id", "").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                places = response.body();
                if (places != null)
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
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                btnEndLoc.setText(item.getTitle());
                                placeId = item.getId();
                                mWebService.getPlaceById(token, placeId).enqueue(new Callback<Place>() {
                                    @Override
                                    public void onResponse(Call<Place> call, Response<Place> response) {
                                        Place place = response.body();
                                        desLocation.setName(place.getName());
                                        desLocation.setLat(place.getLocation().getLatitude());
                                        desLocation.setLng(place.getLocation().getLongitude());

                                        if (pickedSourceLocation) {
                                            Location start = new Location("");
                                            start.setLatitude(srcLocation.getLat());
                                            start.setLongitude(srcLocation.getLng());
                                            Location end = new Location("");
                                            end.setLatitude(desLocation.getLat());
                                            end.setLongitude(desLocation.getLng());
                                            float dist = start.distanceTo(end) / 1000;
                                            tvDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + " km");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Place> call, Throwable t) {

                                    }
                                });
                                pickedDestinationLocation = true;
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnTimeLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewPlanActivity.this, TimeLinesActivity.class);
                intent.putExtra("state", "new");
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
        btnStartLoc.setOnClickListener((v) -> openAutocompleteActivity());
        //Datepicker
        btnStartDateTime.setOnClickListener((v) -> {
            setStart = true;
            setEnd = false;
            Date maxDate = Calendar.getInstance().getTime();
            maxDate.setYear(maxDate.getYear() + 1);
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(listener)
                    .setInitialDate(startDate.getTime())
                    .setMinDate(Calendar.getInstance().getTime())
                    .setMaxDate(maxDate)
                    .setIs24HourTime(false)
                    .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                    .build()
                    .show();
        });
        btnEndDateTime.setOnClickListener((v) -> {
            if (setStart) {
                setEnd = true;
                Date maxDate = new Date();
                maxDate.setYear(maxDate.getYear() + 1);
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(endDate.getTime())
                        .setMinDate(Calendar.getInstance().getTime())
                        .setMaxDate(maxDate)
                        .setIs24HourTime(false)
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .build()
                        .show();
            } else {
                Toast.makeText(NewPlanActivity.this, "Chưa có gian thời gian xuất phát" ,Toast.LENGTH_LONG).show();
            }
        });
        tvNewPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = 0;
                plan = new Plan();
                Group g = new Group();
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(NewPlanActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                alertDialogBuilder.setTitle("Cảnh báo");

                if (edtPlanName.getText().toString().trim().isEmpty()) {
                    alertDialogBuilder.setMessage("Hãy nhập tên kế hoạch")
                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return;
                } else {
                    check++;
                    g.setName(edtPlanName.getText().toString().trim());
                }

                if (pickedSourceLocation && pickedDestinationLocation) {
                    check++;
                } else {
                    alertDialogBuilder.setMessage("Vui lòng chọn địa điểm đi và đến")
                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return;
                }

                if (tvStartTime.getText().toString().equalsIgnoreCase("--:--") || tvDateStart.getText().toString().equalsIgnoreCase("--/--/----")) {
                    alertDialogBuilder.setMessage("Vui lòng chọn thời gian khởi hành")
                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return;
                } else if (tvEndTime.getText().toString().equalsIgnoreCase("--:--") || tvDateEnd.getText().toString().equalsIgnoreCase("--/--/----")) {
                    alertDialogBuilder.setMessage("Vui lòng chọn thời gian hoàn thành")
                            .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return;
                } else {
                    check++;
                    Date d1 = startDate.getTime();
                    Date d2 = endDate.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    plan.setStartTime(sdf.format(d1));
                    plan.setFinishTime(sdf.format(d2));
                }


                List<Member> members = new ArrayList<>();
                members.add(m);

                g.setMembers(members);
                plan.setGroup(g);
                if (swPublic.isChecked()) {
                    plan.setIsPublic(1);
                } else {
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


                if (check >= 3) {
                    List<PlanLocation> locs = new ArrayList<>();
                    locs.add(srcLocation);
                    locs.add(desLocation);
                    Direction direction = new Direction();
                    direction.setPlaceId(placeId);
                    direction.setPlanLocation(new Gson().toJson(locs));
                    plan.setDirection(direction);
                    final ProgressDialog progressDialog = new ProgressDialog(NewPlanActivity.this, R.style.DialogStyle);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mWebService.createPlan(token, plan).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            progressDialog.dismiss();
                            setResult(RESULT_OK, null);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(NewPlanActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                            setResult(RESULT_CANCELED, null);
                            finish();
                        }
                    });
                }

            }
        });
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private ArrayList<SearchPlace> createSampleData() {
        ArrayList<SearchPlace> items = new ArrayList<>();
        if (places != null)
        for (Place p : places) {
            items.add(new SearchPlace(p.getName(), p.getId()));
        }
        return items;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this, data);
                btnStartLoc.setText(place.getName());
                pickedSourceLocation = true;
                srcLocation.setName(place.getName().toString());
                srcLocation.setLat(place.getLatLng().latitude);
                srcLocation.setLng(place.getLatLng().longitude);

                if (pickedDestinationLocation) {
                    Location start = new Location("");
                    start.setLatitude(srcLocation.getLat());
                    start.setLongitude(srcLocation.getLng());
                    Location end = new Location("");
                    end.setLatitude(desLocation.getLat());
                    end.setLongitude(desLocation.getLng());
                    float dist = start.distanceTo(end) / 1000;
                    tvDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + " km");
                }
            } else if (resultCode == RESULT_CANCELED) {
                btnStartLoc.setText("Điểm đi");
                pickedSourceLocation = false;
            }
        } else {
            btnStartLoc.setText("Điểm đi");
            pickedSourceLocation = false;
        }
    }
}
