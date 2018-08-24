package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.erikagtierrez.multiple_media_picker;
import com.bumptech.glide.request.RequestOptions;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.LoginActivity;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.MainScreenPagerAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.plan.NewPlanActivity;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.controller.register.RegisterActivity;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.PlanOwn;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileMemberActivity extends AppCompatActivity {

    static final int OPEN_MEDIA_PICKER = 1;

    private ImageView profileCoverImage;
    private ImageView profileAvatarImage;
    private Toolbar profileToolbar;
    private TextView tvUserNamePrf;
    private TextView tvEmailPrf;
    private TextView tvLikeCount;
    private TextView tvPostCount;
    private TextView tvPlanCount;
    private TabLayout profileTabs;
    private TabItem tabUserPost;
    private TabItem tabProfile;
    private ViewPager profileViewpager;
    private MaterialButton addImage;
    private MaterialButton invite;

    private ProfileMemberPostAdapter adapter;

    private String firstName;
    private String lastName;
    private String birthdate;
    private String phone;
    private String gender;
    private boolean owner;
    private int id;
    private String email;
    private String avatar;
    private String newAvatar;

    private boolean isChange;
    private boolean viewProfile;
    APIService mWebService = APIUtils.getWebService();
    String token;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);

        newAvatar = "";
		token = Session.getToken(this);
        id = getIntent().getIntExtra("id", 0);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileMemberActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.DialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.show();
        APIService mWebService = APIUtils.getWebService();
        mWebService.getUserById(token, id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                progressDialog.dismiss();
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "Tài khoản không tồn tại", Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    firstName = user.getFirstName();
                    lastName = user.getLastName();
                    phone = user.getPhone();
                    birthdate = DateTimeUtils.parseStringDate(user.getBirthDate());
                    gender = user.getGender() == 0 ? "Nam" : "Nữ";
                    owner = getIntent().getBooleanExtra("owner", false);
                    if (owner) {
                        email = Session.getEmail(ProfileMemberActivity.this);
                    } else {
                        email = "";
                    }
                    viewProfile = getIntent().getBooleanExtra("viewProfile", false);
                    avatar = user.getAvatar().getPath().isEmpty() ? "" : APIUtils.BASE_URL_API + user.getAvatar().getPath().substring(4);

                    init();
                    loadData();
                    setEventChangeName();
                    if (viewProfile) {
                        selectProfileTab();
                    }

                    if (owner) {
                        invite.setVisibility(View.GONE);
                    } else {
                        invite.setVisibility(View.VISIBLE);
                        invite.setOnClickListener((v) -> {
                            new SimpleSearchDialogCompat(ProfileMemberActivity.this, "Tìm kiếm kế hoạch của bạn",
                                    "Nhập tên kế hoạch có dấu...", null, createSampleData(user.getId()),
                                    new SearchResultListener<PlanOwn>() {
                                        @Override
                                        public void onSelected(BaseSearchDialogCompat dialog, PlanOwn item, int position) {
                                            alertDialogBuilder.setTitle("Mời thành viên tham gia kế hoạch của bạn");
                                            alertDialogBuilder.setMessage("Bạn có chắc chắn muốn mời "+ user.getFirstName() +" tham gia " + item.getmTitle())
                                                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    mWebService.invitePlan(token,item.getId(),user.getId(),0,"",0).enqueue(new Callback<Plan>() {
                                                                        @Override
                                                                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                                                                            if (response.code() == 200){
                                                                                Toast.makeText(ProfileMemberActivity.this,"Mời thành công",Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<Plan> call, Throwable t) {

                                                                        }
                                                                    });
                                                                }
                                                            }

                                                    ).setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                        }
                                    }).show();
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Xảy ra lỗi", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    private void init() {
        isChange = false;

        profileCoverImage = (ImageView) findViewById(R.id.profile_cover);
        profileAvatarImage = (ImageView) findViewById(R.id.profile_image);
        profileToolbar = (Toolbar) findViewById(R.id.profileToolbar);
        tvUserNamePrf = (TextView) findViewById(R.id.tvUserNamePrf);
        tvUserNamePrf.setText(firstName + " " + lastName);
        tvUserNamePrf.setPadding(LocalDisplay.dp2px(48, getApplicationContext()), 0, 0, 0);
        tvUserNamePrf.setPadding(0, 0, 0, 0);
        tvEmailPrf = (TextView) findViewById(R.id.tvEmailPrf);
        tvEmailPrf.setText(email);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvPostCount = (TextView) findViewById(R.id.tvPostCount);
        tvPlanCount = (TextView) findViewById(R.id.tvPlanCount);
        profileTabs = (TabLayout) findViewById(R.id.profileTabs);
        tabUserPost = (TabItem) findViewById(R.id.tabUserPost);
        tabProfile = (TabItem) findViewById(R.id.tabProfile);
        profileViewpager = (ViewPager) findViewById(R.id.profileViewpager);
        addImage = (MaterialButton) findViewById(R.id.addAvatar);
        invite = (MaterialButton) findViewById(R.id.btnInvite);

        setSupportActionBar(profileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void loadData() {
        adapter = new ProfileMemberPostAdapter(getSupportFragmentManager());
        adapter.setToken(Session.getToken(this));
        adapter.setByUserId(true);
        adapter.setUserId(id);
        profileViewpager.setAdapter(adapter);
        profileViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(profileTabs));
        profileTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(profileViewpager));

        if (owner) {
            profileTabs.setVisibility(View.VISIBLE);
        } else {
            profileViewpager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }


        profileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    disableUpdate();
                }
                ((ProfileOwnFragment) adapter.getItem(1)).disableUpdate();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        GlideApp.with(this)
                .load(avatar)
                .placeholder(R.drawable.avatar_default)
                .apply(RequestOptions.circleCropTransform())
                .fallback(R.drawable.avatar_default)
                .into(profileAvatarImage);

        addImage.setOnClickListener((v) -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileMemberActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(ProfileMemberActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            3);

                } else {
                    ActivityCompat.requestPermissions(ProfileMemberActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            3);
                }
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Intent intent = new Intent(this, Gallery.class);
            // Set the title
            intent.putExtra("title", "Chọn một ảnh");
            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
            intent.putExtra("mode", 2);
            intent.putExtra("maxSelection", 1); // Optional
            startActivityForResult(intent, OPEN_MEDIA_PICKER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                GlideApp.with(this)
                        .load(selectionResult.get(0))
                        .placeholder(R.drawable.avatar_default)
                        .fallback(R.drawable.avatar_default)
                        .centerCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .override(LocalDisplay.dp2px(80, this))
                        .into(profileAvatarImage);
                newAvatar = selectionResult.get(0);
            }
        }
    }

    public void enableUpdate() {
        tvUserNamePrf.setPadding(LocalDisplay.dp2px(48, getApplicationContext()), 0, 0, 0);
        tvUserNamePrf.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_primary_24dp, null), null);
        addImage.setVisibility(View.VISIBLE);
        isChange = true;
    }

    public void disableUpdate() {
        GlideApp.with(this)
                .load(avatar)
                .placeholder(R.drawable.avatar_default)
                .fallback(R.drawable.avatar_default)
                .apply(RequestOptions.circleCropTransform())
                .into(profileAvatarImage);
        tvUserNamePrf.setCompoundDrawables(null, null, null, null);
        tvUserNamePrf.setPadding(0, 0, 0, 0);
        tvUserNamePrf.setText(firstName + " " + lastName);
        addImage.setVisibility(View.GONE);
        isChange = false;
    }


    private void selectProfileTab() {
        profileTabs.getTabAt(1).select();
    }

    private void setEventChangeName() {
        tvUserNamePrf.setOnClickListener((v) -> {
            if (isChange) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Nhập họ tên");

                final EditText edtFirstName = new EditText(this);
                edtFirstName.setHint("Họ");
                edtFirstName.setSingleLine(true);
                final EditText edtLastName = new EditText(this);
                edtLastName.setHint("Tên");
                edtFirstName.setSingleLine(true);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(edtFirstName);
                linearLayout.addView(edtLastName);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                builder.setView(linearLayout);
                builder.setCancelable(false)
                        .setPositiveButton("Hoàn tất", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!edtFirstName.getText().toString().trim().isEmpty()
                                        && !edtLastName.getText().toString().trim().isEmpty()) {
                                    tvUserNamePrf.setText(edtFirstName.getText().toString().trim() + " "
                                            + edtLastName.getText().toString().trim());
                                    firstName = edtFirstName.getText().toString().trim();
                                    lastName = edtLastName.getText().toString().trim();
                                } else {

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
            }
        });
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getNewAvatar() {
        return newAvatar;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private ArrayList<PlanOwn> createSampleData(int id) {
        ArrayList<PlanOwn> items = new ArrayList<>();
        mWebService.getListPlan(token,1,20,"id").enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                if (response.body().size() > 1){
                    boolean isJoin = false;
                    List<Plan> p = response.body();
                    p.remove(0);
                    for (Plan pl: p) {
                        List<Member> m = pl.getGroup().getMembers();
                        for (Member me: m) {
                            if (id == me.getUserId()){
                                isJoin= true;
                            } else
                                isJoin = false;
                        }
                        if (pl.getGroup().getUserId() == Session.getUserId(ProfileMemberActivity.this) && !isJoin){
                            PlanOwn po = new PlanOwn(pl.getGroup().getName(),pl.getId());
                            items.add(po);
                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {

            }
        });
        return items;
    }
}