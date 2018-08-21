package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.ChecklistItem;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.PlanLocation;
import com.viettrekker.mountaintrekkingadviser.model.SearchMember;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.lang.reflect.Type;
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

public class PlanDetailActivity extends AppCompatActivity {

    private static final String TAG = PlanDetailActivity.class.getSimpleName();

    private TextView tvStartPlace;
    private TextView tvEndPlace;
    private TextView tvDateStart;
    private TextView tvTimeStart;
    private TextView tvDateEnd;
    private TextView tvTimeEnd;
    private TextView tvDurationDate;
    private APIService mWebService;
    private RecyclerView rcvListMembersPlan;
    private RelativeLayout layoutViewMember;
    private Button btnTimeLines;
    private Button btnCheckList;
    private TextView tvDistance;
    private TextView tvPlanName;
    private MaterialButton btnEditablePlan;
    private TextView tvMemberCount;
    private TextView tvStatePlan;
    private RecyclerView rcvPlanPost;
    private MaterialButton startPicker;
    private MaterialButton endPicker;
    private FloatingActionButton addViewMaps;

    private boolean changeActivity = true;

    private boolean setStart = false;
    private boolean setEnd = false;
    private boolean pickedSourceLocation = false;
    private boolean pickedDestinationLocation = false;

    private PlanLocation srcLocation = new PlanLocation();
    private PlanLocation desLocation = new PlanLocation();

    private int id;

    private String token;
    private int userId;

    private List<Place> places;
    private int placeId;

    private Calendar startDate;
    private Calendar endDate;

    private Plan plan;
    private MembersListAdapter membersListAdapter;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            if (setEnd) {
                try {
                    if (date.before(startDate.getTime())) {
                        Toast.makeText(PlanDetailActivity.this, "Lỗi: Sớm hơn gian thời gian khởi hành", Toast.LENGTH_LONG).show();
                    } else {
                        tvTimeEnd.setText(DateTimeUtils.parseStringTime(date));
                        tvDateEnd.setText(DateTimeUtils.parseStringDate(date));
                        endDate.setTime(date);

                        tvDurationDate.setText(DateTimeUtils.caculatorStringTime(startDate.getTime(), endDate.getTime()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (setStart) {
                tvTimeStart.setText(DateTimeUtils.parseStringTime(date));
                tvDateStart.setText(DateTimeUtils.parseStringDate(date));
                startDate.setTime(date);

                if (startDate.after(endDate)) {
                    endDate.setTime(startDate.getTime());

                    tvTimeEnd.setText(DateTimeUtils.parseStringTime(endDate.getTime()));
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

    public PlanDetailActivity() {
    }

    public int getId() {
        return id;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_plan_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.viewPlanToolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tvStartPlace = (TextView) findViewById(R.id.tvStartPlace);
        tvEndPlace = (TextView) findViewById(R.id.tvEndPlace);
        tvDateStart = (TextView) findViewById(R.id.tvDateStart);
        tvTimeStart = (TextView) findViewById(R.id.tvTimeStart);
        tvDateEnd = (TextView) findViewById(R.id.tvDateEnd);
        tvTimeEnd = (TextView) findViewById(R.id.tvTimeEnd);
        tvDurationDate = (TextView) findViewById(R.id.tvDurationDate);
        layoutViewMember = (RelativeLayout) findViewById(R.id.layoutViewMember);
        btnTimeLines = (Button) findViewById(R.id.btnTimeLines);
        btnCheckList = (Button) findViewById(R.id.btnCheckList);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvPlanName = (TextView) findViewById(R.id.tvPlanName);
        btnEditablePlan = (MaterialButton) findViewById(R.id.btnEditablePlan);
        tvMemberCount = (TextView) findViewById(R.id.tvMemberCount);
        addViewMaps = (FloatingActionButton) findViewById(R.id.addViewMaps);

        rcvPlanPost = (RecyclerView) findViewById(R.id.rcvPlanPost);

        tvStatePlan = (TextView) findViewById(R.id.tvStatePlan);
        mWebService = APIUtils.getWebService();
        rcvListMembersPlan = (RecyclerView) findViewById(R.id.rcvListMembersPlan);
        rcvListMembersPlan.setLayoutManager(new LinearLayoutManager(PlanDetailActivity.this));
        membersListAdapter = new MembersListAdapter(PlanDetailActivity.this);
        rcvListMembersPlan.setVisibility(View.GONE);

        startPicker = (MaterialButton) findViewById(R.id.startDateTimePicker);
        endPicker = (MaterialButton) findViewById(R.id.endDateTimePicker);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        //getPlan
        id = getIntent().getIntExtra("id", 0);
        token = getIntent().getStringExtra("token");
        userId = getIntent().getIntExtra("userId", 0);

        places = new ArrayList<>();
        mWebService.searchPlace(token, 1, 100, "id", "").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                places = response.body();
                places.remove(0);
            }

            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {

            }
        });

        addViewMaps.setOnClickListener((v) -> {
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + srcLocation.getLat() + "," + srcLocation.getLng() + "&daddr=" + desLocation.getLat() + "," + desLocation.getLng());
            //Uri gmmIntentUri = Uri.parse("geo:0,0?q=1600 Amphitheatre Parkway, Mountain+View, California");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        startPicker.setOnClickListener((v) -> {
            setStart = true;
            setEnd = false;
            Date maxDate = Calendar.getInstance().getTime();
            maxDate.setYear(maxDate.getYear() + 1);
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(listener)
                    .setInitialDate(startDate.getTime())
                    .setMinDate(new Date())
                    .setMaxDate(maxDate)
                    .setIs24HourTime(false)
                    .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                    .build()
                    .show();
        });

        endPicker.setOnClickListener((v) -> {
            setEnd = true;
            Date maxDate = startDate.getTime();
            maxDate.setYear(maxDate.getYear() + 1);
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(listener)
                    .setInitialDate(startDate.getTime())
                    .setMinDate(startDate.getTime())
                    .setMaxDate(maxDate)
                    .setIs24HourTime(false)
                    .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                    .build()
                    .show();

        });

        btnEditablePlan.setOnClickListener((v) -> {
            if (btnEditablePlan.getText().toString().equalsIgnoreCase("sửa")) {
                startPicker.setVisibility(View.VISIBLE);
                endPicker.setVisibility(View.VISIBLE);
                tvStartPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_primary_24dp, null), null);
                tvEndPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_primary_24dp, null), null);
                btnEditablePlan.setText("Xong");

                tvStartPlace.setOnClickListener((view) -> {
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
                });

                tvEndPlace.setOnClickListener((view) -> {
                    new SimpleSearchDialogCompat(PlanDetailActivity.this, "Tìm kiếm địa điểm",
                            "Nhập tên địa điểm có dấu...", null, createSampleData(),
                            new SearchResultListener<SearchPlace>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog, SearchPlace item, int position) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    tvEndPlace.setText(item.getTitle());
                                    placeId = item.getId();
                                    mWebService.getPlaceById(token, placeId).enqueue(new Callback<Place>() {
                                        @Override
                                        public void onResponse(Call<Place> call, Response<Place> response) {
                                            Place place = response.body();
                                            desLocation.setName(place.getName());
                                            desLocation.setLat(place.getLocation().getLatitude());
                                            desLocation.setLng(place.getLocation().getLongitude());

                                            Location start = new Location("");
                                            start.setLatitude(srcLocation.getLat());
                                            start.setLongitude(srcLocation.getLng());
                                            Location end = new Location("");
                                            end.setLatitude(desLocation.getLat());
                                            end.setLongitude(desLocation.getLng());
                                            float dist = start.distanceTo(end) / 1000;
                                            tvDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + " km");
                                        }

                                        @Override
                                        public void onFailure(Call<Place> call, Throwable t) {

                                        }
                                    });
                                    pickedDestinationLocation = true;
                                    dialog.dismiss();
                                }
                            }).show();
                });
            } else {
                tvStartPlace.setOnClickListener(null);
                tvEndPlace.setOnClickListener(null);
                tvStartPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tvEndPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                startPicker.setVisibility(View.GONE);
                endPicker.setVisibility(View.GONE);
                btnEditablePlan.setText("Sửa");

                Date d1 = startDate.getTime();
                Date d2 = endDate.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                plan.setStartTime(sdf.format(d1));
                plan.setFinishTime(sdf.format(d2));

                List<PlanLocation> locs = new ArrayList<>();
                locs.add(srcLocation);
                locs.add(desLocation);
                Direction direction = new Direction();
                direction.setId(plan.getDirection().getId());
                direction.setPlaceId(placeId);
                direction.setPlanLocation(new Gson().toJson(locs));
                plan.setDirection(direction);
                final ProgressDialog progressDialog = new ProgressDialog(PlanDetailActivity.this, R.style.DialogStyle);
                progressDialog.setCancelable(false);
                progressDialog.show();

                mWebService.updatePlan(token, plan).enqueue(new Callback<Plan>() {
                    @Override
                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                        progressDialog.dismiss();
                        Toast.makeText(PlanDetailActivity.this, "Thành công", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, null);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Plan> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(PlanDetailActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                        setResult(RESULT_CANCELED, null);
                        finish();
                    }
                });
            }
        });

        rcvListMembersPlan.setNestedScrollingEnabled(false);
        rcvListMembersPlan.setAdapter(membersListAdapter);
        layoutViewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcvListMembersPlan.getVisibility() == View.GONE) {
                    rcvListMembersPlan.setVisibility(View.VISIBLE);
                } else {
                    rcvListMembersPlan.setVisibility(View.GONE);
                }
            }
        });
        btnTimeLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanDetailActivity.this, TimeLinesActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("token", token);
                changeActivity = true;
                startActivity(intent);
            }
        });
        btnCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanDetailActivity.this, ChecklistActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("token", token);
                changeActivity = true;
                startActivity(intent);
            }
        });
    }

    private ArrayList<SearchPlace> createSampleData() {
        ArrayList<SearchPlace> items = new ArrayList<>();
        for (Place p : places) {
            items.add(new SearchPlace(p.getName(), p.getId()));
        }
        return items;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this, data);
                tvStartPlace.setText(place.getName());
                srcLocation.setName(place.getName().toString());
                srcLocation.setLat(place.getLatLng().latitude);
                srcLocation.setLng(place.getLatLng().longitude);

                Location start = new Location("");
                start.setLatitude(srcLocation.getLat());
                start.setLongitude(srcLocation.getLng());
                Location end = new Location("");
                end.setLatitude(desLocation.getLat());
                end.setLongitude(desLocation.getLng());
                float dist = start.distanceTo(end) / 1000;
                tvDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + " km");
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else {
        }
    }

    private void loadPlan() {
        mWebService.getPlanById(token, id).enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                plan = response.body();
                if (plan.getState() == 1) {
                    tvStatePlan.setText("Đang thực hiện");
                    tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorOrange));
                } else if (plan.getState() == -1) {
                    tvStatePlan.setText("Kết thúc");
                    tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorGray));
                } else {
                    tvStatePlan.setText("Sẵn sàng");
                    tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorPrimary));
                }
                try {
                    tvDateStart.setText(DateTimeUtils.parseStringDate(DateTimeUtils.changeTimeToLocale(plan.getStartTime())));
                    tvTimeStart.setText(DateTimeUtils.parseStringTime(DateTimeUtils.changeTimeToLocale(plan.getStartTime())));
                    tvDateEnd.setText(DateTimeUtils.parseStringDate(DateTimeUtils.changeTimeToLocale(plan.getFinishTime())));
                    tvTimeEnd.setText(DateTimeUtils.parseStringTime(DateTimeUtils.changeTimeToLocale(plan.getFinishTime())));
                    startDate.setTime(DateTimeUtils.changeTimeToLocale(plan.getStartTime()));
                    endDate.setTime(DateTimeUtils.changeTimeToLocale(plan.getFinishTime()));
                    tvDurationDate.setText(DateTimeUtils.caculatorStringTime(startDate.getTime(), endDate.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvPlanName.setText(plan.getGroup().getName());
                List<Member> members = plan.getGroup().getMembers();

                for (Member member : members) {
                    if (member.getUserId() == userId) {
                        if (member.getRoleInGroupId() != 1) {
                            btnEditablePlan.setVisibility(View.GONE);
                        }
                    }
                }

                membersListAdapter.setUsers(members);
                membersListAdapter.notifyDataSetChanged();
                tvMemberCount.setText(members.size() + " người");

                Type type = new TypeToken<ArrayList<PlanLocation>>() {
                }.getType();
                Gson gson = new Gson();
                List<PlanLocation> locs = gson.fromJson(plan.getDirection().getPlanLocation(), type);
                srcLocation = locs.get(0);
                desLocation = locs.get(1);
                tvStartPlace.setText(srcLocation.getName());
                tvEndPlace.setText(desLocation.getName());
                Location start = new Location("");
                start.setLatitude(srcLocation.getLat());
                start.setLongitude(srcLocation.getLng());
                Location end = new Location("");
                end.setLatitude(desLocation.getLat());
                end.setLongitude(desLocation.getLng());
                float dist = start.distanceTo(end) / 1000;
                tvDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + " km");

                placeId = plan.getDirection().getPlaceId();
            }


            @Override
            public void onFailure(Call<Plan> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadPlan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (changeActivity) {
            changeActivity = false;
            loadPlan();
        }
    }
}