package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.LoginActivity;
import com.viettrekker.mountaintrekkingadviser.controller.MainScreenPagerAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.controller.register.RegisterActivity;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;

import java.util.Date;

public class ProfileMemberActivity extends AppCompatActivity {

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

    private ProfileMemberPostAdapter adapter;

    private String firstName;
    private String lastName;
    private String birthdate;
    private String phone;
    private String gender;
    private boolean owner;
    private int id;
    private String email;

    private boolean isChange;
    private boolean viewProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);

        firstName = getIntent().getStringExtra("firstname");
        lastName = getIntent().getStringExtra("lastname");
        phone = getIntent().getStringExtra("phone");
        birthdate = DateTimeUtils.parseStringDate(new Date(getIntent().getLongExtra("birthdate", 0)));
        gender = getIntent().getIntExtra("gender", 0) == 1 ? "Nam" : "Nữ";
        owner = getIntent().getBooleanExtra("owner", false);
        id = getIntent().getIntExtra("id", 0);
        email = getIntent().getStringExtra("email");
        viewProfile = getIntent().getBooleanExtra("viewProfile", false);

        init();
        loadData();
        setEventChangeName();
        if (viewProfile) {
            selectProfileTab();
        }
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

        setSupportActionBar(profileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadData() {
        adapter = new ProfileMemberPostAdapter(getSupportFragmentManager());
        adapter.setByUserId(true);
        adapter.setUserId(id);
        profileViewpager.setAdapter(adapter);

        if (owner) {
            profileViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(profileTabs));
            profileTabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(profileViewpager));
        } else {
            profileTabs.setVisibility(View.GONE);
        }

        profileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    tvUserNamePrf.setPadding(LocalDisplay.dp2px(48, getApplicationContext()), 0, 0, 0);
                    tvUserNamePrf.setCompoundDrawablesWithIntrinsicBounds(null, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_edit_primary_24dp, null), null);
                    isChange = true;
                } else {
                    tvUserNamePrf.setCompoundDrawables(null, null, null, null);
                    tvUserNamePrf.setPadding(0, 0, 0, 0);
                    isChange = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}