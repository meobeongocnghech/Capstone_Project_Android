package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import com.google.gson.reflect.TypeToken;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.NewsFeedAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.post.NewsFeedFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostAddActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.PlanLocation;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.SearchMember;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.Session;
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
    private ConstraintLayout layoutViewMember;
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
    //    private TextView tvMoreAction;
    private FloatingActionButton addPost;
    private TextView tvCarry;
    private MaterialButton btnDeletePlan;
    private MaterialButton btnLeavePlan;
    private MaterialButton btnViewMaps;
    private MaterialButton btnVehicle;
    private MaterialButton btnConfirm;
    private MaterialButton btnAddMember;
    private NestedScrollView planPostScrollview;
    private Switch swPublic;

    private boolean changeActivity = true;

    private boolean setStart = false;
    private boolean setEnd = false;
    private boolean pickedSourceLocation = false;
    private boolean pickedDestinationLocation = false;

    private PlanLocation srcLocation = new PlanLocation();
    private PlanLocation desLocation = new PlanLocation();

    private int id;
    private int directionId;

    private String token;
    private int userId;
    private int userRole;

    private List<Place> places;
    private List<User> users;
    private int placeId;

    private Calendar startDate;
    private Calendar endDate;

    private Plan plan;
    private MembersListAdapter membersListAdapter;
    private NewsFeedAdapter newsFeedAdapter;
    AlertDialog.Builder alertDialogBuilder;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int state;

    private final int READY = 0;
    private final int ONGOING = 1;
    private final int FINISH = 2;

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
        //getPlan
        id = getIntent().getIntExtra("id", 0);
        token = Session.getToken(this);
        userId = getIntent().getIntExtra("userId", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.viewPlanToolbar);
        alertDialogBuilder = new AlertDialog.Builder(PlanDetailActivity.this);
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
        layoutViewMember = (ConstraintLayout) findViewById(R.id.layoutViewMember);
        btnTimeLines = (Button) findViewById(R.id.btnTimeLines);
        btnCheckList = (Button) findViewById(R.id.btnCheckList);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvPlanName = (TextView) findViewById(R.id.tvPlanName);
        btnEditablePlan = (MaterialButton) findViewById(R.id.btnEditablePlan);
        tvMemberCount = (TextView) findViewById(R.id.tvMemberCount);
        addPost = (FloatingActionButton) findViewById(R.id.addPost);
//        tvMoreAction = (TextView) findViewById(R.id.tvMoreAction);
        tvCarry = (TextView) findViewById(R.id.tvCarry);
        btnDeletePlan = (MaterialButton) findViewById(R.id.btnDeletePlan);
        btnLeavePlan = (MaterialButton) findViewById(R.id.btnLeavePlan);
        btnViewMaps = (MaterialButton) findViewById(R.id.btnMaps);
        btnVehicle = (MaterialButton) findViewById(R.id.btnVehicle);
        btnConfirm = (MaterialButton) findViewById(R.id.btnConfirm);
        btnAddMember = (MaterialButton) findViewById(R.id.btnAddMember);
        swPublic = (Switch) findViewById(R.id.swPublic);
        swPublic.setClickable(false);
        btnAddMember.setOnClickListener(null);
        planPostScrollview = (NestedScrollView) findViewById(R.id.planPostScrollview);

        rcvPlanPost = (RecyclerView) findViewById(R.id.rcvPlanPost);

        tvStatePlan = (TextView) findViewById(R.id.tvStatePlan);
        mWebService = APIUtils.getWebService();
        rcvListMembersPlan = (RecyclerView) findViewById(R.id.rcvListMembersPlan);
        rcvListMembersPlan.setLayoutManager(new LinearLayoutManager(PlanDetailActivity.this));
        membersListAdapter = new MembersListAdapter(PlanDetailActivity.this);
        membersListAdapter.setTvCountMember(tvMemberCount);
        rcvListMembersPlan.setVisibility(View.GONE);

        newsFeedAdapter = new NewsFeedAdapter(PlanDetailActivity.this, null, token);
        newsFeedAdapter.setByPlanId(true);
        newsFeedAdapter.setDirectionId(directionId);
        newsFeedAdapter.setUserId(userId);
        startPicker = (MaterialButton) findViewById(R.id.startDateTimePicker);
        endPicker = (MaterialButton) findViewById(R.id.endDateTimePicker);

        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();


        places = new ArrayList<>();
        mWebService.searchPlace(token, 1, 100, "id", "").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                if (response.code() == 200) {
                    places = response.body();
                    places.remove(0);
                } else {
                    Toast.makeText(PlanDetailActivity.this, "Không thể truy cập", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {

            }
        });
        users = new ArrayList<>();
        mWebService.getAllMembers(token, 1, 100).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {
                    users = response.body();
                    users.remove(0);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        btnVehicle.setOnClickListener((v) -> {
            alertDialogBuilder.setTitle("Cập nhật phương tiện di chuyển");
            alertDialogBuilder.setMessage("Bạn có phương tiện di chuyển không?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mWebService.updateTraffic(token, plan.getId(), 0, "Xe máy", 1).enqueue(new Callback<Plan>() {
                                @Override
                                public void onResponse(Call<Plan> call, Response<Plan> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(PlanDetailActivity.this, "Bạn đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        tvCarry.setText(response.body().getCarry() + " xe");
                                        membersListAdapter.setUsers(response.body().getGroup().getMembers());
                                        membersListAdapter.notifyDataSetChanged();
                                    } else
                                        Toast.makeText(PlanDetailActivity.this, "Thao tác thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Plan> call, Throwable t) {
                                    Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mWebService.updateTraffic(token, plan.getId(), 0, "Xe máy", 0).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            if (response.code() == 200) {
                                Toast.makeText(PlanDetailActivity.this, "Bạn đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                tvCarry.setText(response.body().getCarry() + " xe");
                            } else
                                Toast.makeText(PlanDetailActivity.this, "Thao tác thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogInterface.dismiss();
                }
            }).setNeutralButton("Đóng", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        });

        btnLeavePlan.setOnClickListener((v) -> {
            alertDialogBuilder.setTitle("Rời khỏi kế hoạch");
            alertDialogBuilder.setMessage("Bạn có muốn rời khỏi kế hoạch này không?")
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mWebService.quitPlan(token, plan.getId()).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            if (response.code() == 200) {
                                onBackPressed();
                                Toast.makeText(PlanDetailActivity.this, "Bạn đã rời khỏi kế hoạch.", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(PlanDetailActivity.this, "Thao tác thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                    });
                    dialogInterface.dismiss();
                }
            }).show();
        });


        btnDeletePlan.setOnClickListener((v) -> {
            alertDialogBuilder.setTitle("Xóa kế hoạch");
            alertDialogBuilder.setMessage("Bạn có chắc chắn muốn xóa kế hoạch này không?")
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mWebService.removePlan(token, plan.getId()).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            if (response.code() == 200) {
                                onBackPressed();
                                Toast.makeText(PlanDetailActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(PlanDetailActivity.this, "Thao tác thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogInterface.dismiss();
                }
            }).show();
        });

        btnViewMaps.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + srcLocation.getLat() + "," + srcLocation.getLng() + "&daddr=" + desLocation.getLat() + "," + desLocation.getLng());
            //Uri gmmIntentUri = Uri.parse("geo:0,0?q=1600 Amphitheatre Parkway, Mountain+View, California");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        btnConfirm.setOnClickListener(v -> {
            alertDialogBuilder.setTitle("Yêu cầu tham gia");
            alertDialogBuilder.setMessage("Bạn đã được mời tham gia vào kế hoạch này, Bạn có muốn tham gia hay không?")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mWebService.approveInvitation(token, id).enqueue(new Callback<Plan>() {
                                @Override
                                public void onResponse(Call<Plan> call, Response<Plan> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(PlanDetailActivity.this, "Tham gia nhóm thành công", Toast.LENGTH_SHORT).show();
                                        btnConfirm.setVisibility(View.GONE);
                                        membersListAdapter.setUsers(response.body().getGroup().getMembers());
                                        membersListAdapter.notifyDataSetChanged();
                                        btnLeavePlan.setVisibility(View.VISIBLE);
                                        btnVehicle.setVisibility(View.VISIBLE);
                                        addPost.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        addPost.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        addPost.requestLayout();
                                    } else {
                                        Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Plan> call, Throwable t) {
                                    Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mWebService.rejectInvitation(token, id).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            if (response.code() == 200) {
                                Toast.makeText(PlanDetailActivity.this, "Bạn đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                                btnConfirm.setVisibility(View.GONE);
                                membersListAdapter.setUsers(response.body().getGroup().getMembers());
                                membersListAdapter.notifyDataSetChanged();
                                tvMemberCount.setText(response.body().getGroup().getMembers().size() + " người");
                            } else {
                                Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogInterface.dismiss();
                }
            }).setNeutralButton("Đóng", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        });

        addPost.setOnClickListener((v) -> {
            Intent intent = new Intent(this, PostAddActivity.class);
            intent.putExtra("isPostPlan", true);
            intent.putExtra("planName", plan.getGroup().getName());
            intent.putExtra("planId", plan.getDirection().getId());
            changeActivity = true;
            startActivity(intent);
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
            Date maxDate = Calendar.getInstance().getTime();
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

        });

        btnEditablePlan.setOnClickListener((v) -> {
            if (btnEditablePlan.getText().toString().equalsIgnoreCase("sửa")) {
                startPicker.setVisibility(View.VISIBLE);
                endPicker.setVisibility(View.VISIBLE);
                swPublic.setClickable(true);
                tvStartPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_primary_24dp, null), null);
                tvEndPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_primary_24dp, null), null);
                tvPlanName.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_white_24dp, null), null);
                ColorStateList stateList = new ColorStateList(
                        new int[][]{{}}, new int[]{Color.parseColor("#ffffff")});
                tvPlanName.setOnClickListener((textView) -> {
                    String lastName = tvPlanName.getText().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Tên kế hoạch");
                    final EditText input = new EditText(this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setCancelable(false)
                            .setPositiveButton("Hoàn tất", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String name = input.getText().toString().trim();
                                    if (!name.isEmpty()) {
                                        tvPlanName.setText(name);
                                        dialog.cancel();
                                    } else {
                                        Toast.makeText(PlanDetailActivity.this, "Tên không được trống", Toast.LENGTH_LONG).show();
                                        tvPlanName.setText(lastName);
                                    }
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                }
                            })
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                }
                            });

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();
                });
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
                                    planPostScrollview.smoothScrollTo(0, 0);
                                    tvEndPlace.setText(item.getTitle());
                                    placeId = item.getId();
                                    mWebService.getPlaceById(token, placeId).enqueue(new Callback<Place>() {
                                        @Override
                                        public void onResponse(Call<Place> call, Response<Place> response) {
                                            if (response.code() == 200) {
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
                swPublic.setClickable(false);
                tvPlanName.setOnClickListener(null);
                tvStartPlace.setOnClickListener(null);
                tvEndPlace.setOnClickListener(null);
                tvStartPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tvEndPlace.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tvPlanName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                startPicker.setVisibility(View.GONE);
                endPicker.setVisibility(View.GONE);
                btnEditablePlan.setText("Sửa");

                plan.getGroup().setName(tvPlanName.getText().toString());
                Date d1 = startDate.getTime();
                Date d2 = endDate.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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

                if (swPublic.isChecked()) {
                    plan.setIsPublic(1);
                } else {
                    plan.setIsPublic(0);
                }

                mWebService.updatePlan(token, plan).enqueue(new Callback<Plan>() {
                    @Override
                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                        if (response.code() == 200) {
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id.coordinatorPlanDetail), "Thay đổi thành công", Snackbar.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<Plan> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(PlanDetailActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });
            }
        });

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        rcvPlanPost.setLayoutManager(mLayoutManager);
        rcvPlanPost.setNestedScrollingEnabled(false);
        rcvPlanPost.setAdapter(newsFeedAdapter);
        rcvPlanPost.setVisibility(View.VISIBLE);
        planPostScrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int i, int i1, int i2, int i3) {
                if (i1 > 0) {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((i1 >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                i1 > i3) {

                            int visibleItemCount = mLayoutManager.getChildCount();
                            int totalItemCount = mLayoutManager.getItemCount();
                            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                newsFeedAdapter.incrementalLoad();
                            }
                        }
                    }
                }
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
                intent.putExtra("planState", state);
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
                intent.putExtra("planState", state);
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
    public void onBackPressed() {
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
        }
    }

    private void loadPlan() {
        alertDialogBuilder = new AlertDialog.Builder(PlanDetailActivity.this);
        mWebService.getPlanById(token, id).enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.code() == 200) {
                    plan = response.body();
                    if (plan.getIsPublic() == 0) {
                        swPublic.setChecked(false);
                    } else {
                        swPublic.setChecked(true);
                    }
                    directionId = plan.getDirection().getId();
                    newsFeedAdapter.setDirectionId(directionId);
                    tvCarry.setText(plan.getCarry() + " xe");
                    List<Member> members = plan.getGroup().getMembers();
                    for (Member u : members) {
                        if (u.getUserId() == userId) {
                            userRole = u.getRoleInGroupId();
                            break;
                        }
                    }
                    switch (userRole) {
                        case 0:
                            state = 3;
                            rcvPlanPost.setVisibility(View.GONE);
                            break;
                        case 1:
                            btnDeletePlan.setVisibility(View.VISIBLE);
                            btnVehicle.setVisibility(View.VISIBLE);
                            addPost.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            addPost.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            addPost.requestLayout();
                            btnAddMember.setVisibility(View.VISIBLE);
                            btnAddMember.setOnClickListener(v -> {
                                new SimpleSearchDialogCompat(PlanDetailActivity.this, "Tìm kiếm thành viên",
                                        "Nhập tên thành viên có dấu...", null, createSampleMemberData(),
                                        new SearchResultListener<SearchMember>() {
                                            @Override
                                            public void onSelected(BaseSearchDialogCompat dialog, SearchMember item, int position) {
                                                planPostScrollview.smoothScrollTo(0, 0);
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                                mWebService.invitePlan(token, plan.getId(), item.getId(), 0, "", 0).enqueue(new Callback<Plan>() {
                                                    @Override
                                                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                                                        if (response.code() == 200) {
                                                            Toast.makeText(PlanDetailActivity.this, "Mời thành công", Toast.LENGTH_SHORT).show();
                                                            membersListAdapter.setUsers(response.body().getGroup().getMembers());
                                                            membersListAdapter.notifyDataSetChanged();
                                                            tvMemberCount.setText(response.body().getGroup().getMembers().size() + " người");
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Plan> call, Throwable t) {

                                                    }
                                                });
                                                dialog.dismiss();
                                            }
                                        }).show();
                            });
                            break;
                        case 3:
                            state = 3;
                            btnLeavePlan.setVisibility(View.VISIBLE);
                            btnVehicle.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            state = 3;
                            btnConfirm.setVisibility(View.VISIBLE);
                            rcvPlanPost.setVisibility(View.GONE);
                            break;
                        case 5:
                            state = 3;
                            rcvPlanPost.setVisibility(View.GONE);
                            break;
                    }

                    try {
                        if (DateTimeUtils.changeTimeToLocale(plan.getStartTime()).before(Calendar.getInstance().getTime())
                                && DateTimeUtils.changeTimeToLocale(plan.getFinishTime()).after(Calendar.getInstance().getTime())) {
                            tvStatePlan.setText("Đang diễn ra");
                            tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorOrange));
                            btnVehicle.setVisibility(View.GONE);
                            btnConfirm.setVisibility(View.GONE);
                            btnAddMember.setVisibility(View.GONE);
                            if (userRole == 1) {
                                state = ONGOING;
                            }
                        } else if (DateTimeUtils.changeTimeToLocale(plan.getFinishTime()).before(Calendar.getInstance().getTime())) {
                            tvStatePlan.setText("Đã hoàn thành");
                            tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorGray));
                            btnVehicle.setVisibility(View.GONE);
                            btnConfirm.setVisibility(View.GONE);
                            btnAddMember.setVisibility(View.GONE);
                            if (userRole == 1) {
                                state = FINISH;
                            }
                        } else {
                            tvStatePlan.setText("Sẵn sàng");
                            tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorPrimary));
                            if (userRole == 1) {
                                state = READY;
                                btnEditablePlan.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
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

                    if (userRole == 4) {
                        alertDialogBuilder.setTitle("Yêu cầu tham gia");
                        alertDialogBuilder.setMessage("Bạn đã được mời tham gia vào kế hoạch này, Bạn có muốn tham gia hay không?")
                                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mWebService.approveInvitation(token, id).enqueue(new Callback<Plan>() {
                                            @Override
                                            public void onResponse(Call<Plan> call, Response<Plan> response) {
                                                if (response.code() == 200) {
                                                    Toast.makeText(PlanDetailActivity.this, "Tham gia nhóm thành công", Toast.LENGTH_SHORT).show();
                                                    btnConfirm.setVisibility(View.GONE);
                                                    membersListAdapter.setUsers(response.body().getGroup().getMembers());
                                                    membersListAdapter.notifyDataSetChanged();
                                                    btnLeavePlan.setVisibility(View.VISIBLE);
                                                    btnVehicle.setVisibility(View.VISIBLE);
                                                    addPost.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                                    addPost.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                                    addPost.requestLayout();
                                                } else {
                                                    Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Plan> call, Throwable t) {
                                                Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mWebService.rejectInvitation(token, id).enqueue(new Callback<Plan>() {
                                    @Override
                                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                                        if (response.code() == 200) {
                                            Toast.makeText(PlanDetailActivity.this, "Bạn đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                                            btnConfirm.setVisibility(View.GONE);
                                            membersListAdapter.setUsers(response.body().getGroup().getMembers());
                                            membersListAdapter.notifyDataSetChanged();
                                            tvMemberCount.setText(response.body().getGroup().getMembers().size() + " người");
                                        } else {
                                            Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Plan> call, Throwable t) {
                                        Toast.makeText(PlanDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialogInterface.dismiss();
                            }
                        }).setNeutralButton("Đóng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }

                    membersListAdapter.setContext(PlanDetailActivity.this);
                    membersListAdapter.setUsers(members);
                    membersListAdapter.setPlanId(plan.getId());
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
                    mWebService.getPostPageByDirectionId(token, 1, 5, directionId, "DESC").enqueue(new Callback<List<Post>>() {
                        @Override
                        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                            if (response.code() == 200) {
                                List<Post> lp = response.body();
                                if (lp.size() > 1) {
                                    lp.remove(0);
                                    newsFeedAdapter.setListPost(lp);
                                    newsFeedAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(PlanDetailActivity.this, "Có lỗi khi tải bài viết.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Post>> call, Throwable t) {
                            Toast.makeText(PlanDetailActivity.this, "Có lỗi khi tải bài viết.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    placeId = plan.getDirection().getPlaceId();
                } else {
                    Toast.makeText(PlanDetailActivity.this, "Không thể truy cập", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

            }


            @Override
            public void onFailure(Call<Plan> call, Throwable t) {
                Toast.makeText(PlanDetailActivity.this, "Không thể truy cập", Toast.LENGTH_SHORT).show();
                onBackPressed();
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

    private ArrayList<SearchMember> createSampleMemberData() {
        ArrayList<SearchMember> items = new ArrayList<>();
        List<Member> members = membersListAdapter.getUsers();
        if (users != null && members != null) {
            outer:
            for (User u : users) {
                for (Member m : members) {
                    if (m.getUserId() == u.getId()) {
                        continue outer;
                    }
                }
                items.add(new SearchMember(u.getFirstName() + " " + u.getLastName(), u.getId()));
            }
        }

        return items;
    }
}