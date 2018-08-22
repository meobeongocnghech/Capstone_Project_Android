package com.viettrekker.mountaintrekkingadviser.controller;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationFragment;
import com.viettrekker.mountaintrekkingadviser.controller.plan.NewPlanActivity;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostAddActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.controller.search.BaseExampleFragment;
import com.viettrekker.mountaintrekkingadviser.controller.search.SlidingSearchResultsFragment;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseExampleFragment.BaseExampleFragmentCallbacks {
    private TabLayout tabs;
    private ViewPager viewPager;
    private ImageView imgAvatar;
    private MainScreenPagerAdapter adapter;
    private DrawerLayout drawer;
    private User user;
    private FrameLayout frame;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private LocationManager location;
    private LatLng mLatLng;
    private TextView tvMainTitle;
    private SwipeRefreshLayout swipeContainer;
    private NavigationView navigationView;
    private android.support.design.widget.FloatingActionButton btnAddNew;

    private ImageButton search;

    private int PLAN_RESULT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initLocationListener();
    }

    public void swipeTab(int index) {
        viewPager.setCurrentItem(index);
    }

    private void init() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        btnAddNew = (FloatingActionButton) findViewById(R.id.btnAddNew);
        imgAvatar = (ImageView) findViewById(R.id.imgMainAvatar);
        viewPager = (ViewPager) findViewById(R.id.container);
        tabs = (TabLayout) findViewById(R.id.tabs);
        tvMainTitle = (TextView) findViewById(R.id.tvMainTitle);

        navigationView.setNavigationItemSelectedListener(this);
        imgAvatar.setOnClickListener((v) -> drawer.openDrawer(GravityCompat.START));

        View header = navigationView.getHeaderView(0);
        TextView tvNavName = (TextView) header.findViewById(R.id.tvNavName);
        TextView tvNavEmail = (TextView) header.findViewById(R.id.tvNavEmail);
        ImageView imgNavAvatar = (ImageView) header.findViewById(R.id.imgNavAvatar);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabs.getSelectedTabPosition() == 0) {
                    Intent intent = new Intent(MainActivity.this, PostAddActivity.class);
                    startActivity(intent);
                } else if (tabs.getSelectedTabPosition() == 1) {
                    Intent intent = new Intent(MainActivity.this, NewPlanActivity.class);
                    intent.putExtra("token", Session.getToken(MainActivity.this));
                    startActivityForResult(intent, PLAN_RESULT);
                }
            }
        });

        imgNavAvatar.setOnClickListener((v) -> {
            Intent i = new Intent(this, ProfileMemberActivity.class);
            i.putExtra("owner", true);
            i.putExtra("id", user.getId());
            i.putExtra("token", Session.getToken(MainActivity.this));
            drawer.closeDrawer(GravityCompat.START);
            startActivity(i);
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                tab.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

                if (tab.getPosition() == 0) {
                    tvMainTitle.setText("Trang chủ");
                    btnAddNew.getLayoutParams().height = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
                    btnAddNew.getLayoutParams().width = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
                    btnAddNew.requestLayout();
                } else if (tab.getPosition() == 1) {
                    tvMainTitle.setText("Kế hoạch");
                    btnAddNew.getLayoutParams().height = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
                    btnAddNew.getLayoutParams().width = CoordinatorLayout.LayoutParams.WRAP_CONTENT;
                    btnAddNew.requestLayout();
                } else if (tab.getPosition() == 2) {
                    tvMainTitle.setText("Thông báo");
                    btnAddNew.getLayoutParams().height = 0;
                    btnAddNew.getLayoutParams().width = 0;
                    btnAddNew.requestLayout();
                } else if (tab.getPosition() == 3) {
                    tvMainTitle.setText("Tìm kiếm");
                    btnAddNew.getLayoutParams().height = 0;
                    btnAddNew.getLayoutParams().width = 0;
                    btnAddNew.requestLayout();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorBlack);
                tab.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        if (((PostFragment) fragment).getCurrentScrollY() != 0) {
                            ((PostFragment) fragment).scrollToTop();
                        } else {
                            swipeContainer.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeContainer.setRefreshing(true);
                                    refreshData();
                                }
                            });
                        }
                        break;
                    case 1:
                        if (((PlanFragment) fragment).getCurrentScrollY() != 0) {
                            ((PlanFragment) fragment).scrollToTop();
                        } else {
                            swipeContainer.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeContainer.setRefreshing(true);
                                    refreshData();
                                }
                            });
                        }
                        break;
                    case 2:
                        if (((NotificationFragment) fragment).getCurrentScrollY() != 0) {
                            ((NotificationFragment) fragment).scrollToTop();
                        } else {
                            swipeContainer.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeContainer.setRefreshing(true);
                                    refreshData();
                                }
                            });
                        }
                        break;
                }
            }
        });

        initRefreshLayout();

        adapter = new MainScreenPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        tabs.getTabAt(0).getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

        viewPager.setOffscreenPageLimit(4);
    }

    private void initRefreshLayout() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
    }

    private void refreshData() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                adapter.getPostFragment().refreshData();
                break;
            case 1:
                adapter.getPlanFragment().initLoad();
                break;
            case 2:
                adapter.getNotificationFragment().initLoad();
                break;
//            case 3:
//                adapter.setMessageFragment(new MessageFragment());
//                break;
            case 3:
                //adapter.setSearchFragment(new SearchFragment());
                break;
        }
        swipeContainer.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    public void setTitle(String title) {
        tvMainTitle.setText(title);
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        getWindow().setExitTransition(fade);

        ChangeImageTransform cif = new ChangeImageTransform();
        getWindow().setSharedElementExitTransition(new Explode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindData();

        SlidingSearchResultsFragment fragment = new SlidingSearchResultsFragment();
        search = (ImageButton) findViewById(R.id.search);
        frame = (FrameLayout) findViewById(R.id.search_frame);
        fragment.setSearch(search);
        fragment.setFrame(frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.search_frame, fragment).commit();
        fragment.setSearchFragment(adapter.getSearchFragment());
    }

    private void bindData() {
        user = Session.getUser(this);

        APIService service = APIUtils.getWebService();
        service.getUserById(Session.getToken(this), user.getId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    user.setFirstName(response.body().getFirstName());
                    user.setLastName(response.body().getLastName());
                    user.setGallery(response.body().getGallery());
                }

                View header = navigationView.getHeaderView(0);
                TextView tvNavName = (TextView) header.findViewById(R.id.tvNavName);
                TextView tvNavEmail = (TextView) header.findViewById(R.id.tvNavEmail);
                ImageView imgNavAvatar = (ImageView) header.findViewById(R.id.imgNavAvatar);

                if (user.getGallery() != null && !user.getGallery().getMedia().get(0).getPath().isEmpty()) {

                    GlideApp.with(MainActivity.this)
                            .load(APIUtils.BASE_URL_API + user.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + LocalDisplay.dp2px(80, MainActivity.this))
                            .placeholder(getDrawable(R.drawable.avatar_default))
                            .fallback(getDrawable(R.drawable.avatar_default))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgAvatar);

                    GlideApp.with(MainActivity.this)
                            .load(APIUtils.BASE_URL_API + user.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + LocalDisplay.dp2px(80, MainActivity.this))
                            .placeholder(getDrawable(R.drawable.avatar_default))
                            .fallback(getDrawable(R.drawable.avatar_default))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgNavAvatar);
                }

                tvNavName.setText(user.getLastName() + " " + user.getFirstName());
                tvNavEmail.setText(user.getEmail());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                View header = navigationView.getHeaderView(0);
                TextView tvNavName = (TextView) header.findViewById(R.id.tvNavName);
                TextView tvNavEmail = (TextView) header.findViewById(R.id.tvNavEmail);
                ImageView imgNavAvatar = (ImageView) header.findViewById(R.id.imgNavAvatar);

                if (user.getGallery() != null && user.getGallery().getMedia().get(0).getPath().isEmpty()) {

                    GlideApp.with(MainActivity.this)
                            .load(APIUtils.BASE_URL_API + user.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + LocalDisplay.dp2px(80, MainActivity.this))
                            .placeholder(getDrawable(R.drawable.avatar_default))
                            .fallback(getDrawable(R.drawable.avatar_default))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgAvatar);

                    GlideApp.with(MainActivity.this)
                            .load(APIUtils.BASE_URL_API + user.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + LocalDisplay.dp2px(80, MainActivity.this))
                            .placeholder(getDrawable(R.drawable.avatar_default))
                            .fallback(getDrawable(R.drawable.avatar_default))
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgNavAvatar);
                }

                tvNavName.setText(user.getLastName() + " " + user.getFirstName());
                tvNavEmail.setText(user.getEmail());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        //item.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

        MenuItem item1 = navigationView.getMenu().findItem(R.id.nav_profile);
        MenuItem item2 = navigationView.getMenu().findItem(R.id.nav_logout);

        if (item.equals(item1)) {
            Intent i = new Intent(this, ProfileMemberActivity.class);
            i.putExtra("owner", true);
            i.putExtra("id", user.getId());
            i.putExtra("viewProfile", true);
            i.putExtra("token", Session.getToken(this));
            if (user.getGallery() != null) {
                i.putExtra("avatar", user.getGallery().getMedia().get(0).getPath());
            }
            drawer.closeDrawer(GravityCompat.START);
            startActivity(i);
        } else if (item.equals(item2)) {
            Session.clearSession(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return false;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    private void initLocationListener() {

        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (!location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertDialogNoGPS();
//        } else {
        if (location.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                buildAlertDialogNoGPS();
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    if (location != null) {
                        double tlat = location.getLatitude();
                        double tlng = location.getLongitude();
                        mLatLng = new LatLng(tlat, tlng);
                    } else {
                        Toast.makeText(getApplicationContext(), "Location loading...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        } else if (location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    if (location != null) {
                        double tlat = location.getLatitude();
                        double tlng = location.getLongitude();
                        mLatLng = new LatLng(tlat, tlng);
                    } else {
                        Toast.makeText(getApplicationContext(), "Location loading...", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
    }

    private void buildAlertDialogNoGPS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(drawer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAN_RESULT) {
            if (resultCode == RESULT_OK) {
                adapter.getPlanFragment().initLoad();
            }
        }
    }
}



