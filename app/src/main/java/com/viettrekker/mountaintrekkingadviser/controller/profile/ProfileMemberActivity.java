package com.viettrekker.mountaintrekkingadviser.controller.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.Snackbar;
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
import android.view.MotionEvent;
import android.view.View;
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
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.controller.register.RegisterActivity;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private boolean isChange;
    private boolean viewProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);

        id = getIntent().getIntExtra("id", 0);
        String token = getIntent().getStringExtra("token");
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
                    gender = user.getGender() == 1 ? "Nam" : "Nữ";
                    owner = getIntent().getBooleanExtra("owner", false);
                    email = user.getEmail();
                    viewProfile = getIntent().getBooleanExtra("viewProfile", false);
                    avatar = user.getGallery() != null ? user.getGallery().getMedia().get(0).getPath() : "";

                    init();
                    loadData();
                    setEventChangeName();
                    if (viewProfile) {
                        selectProfileTab();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Xảy ra lỗi", Toast.LENGTH_SHORT).show();
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

        setSupportActionBar(profileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadData() {
        adapter = new ProfileMemberPostAdapter(getSupportFragmentManager());
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

    public int getId() {
        return id;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}