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
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostAddActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.controller.search.BaseExampleFragment;
import com.viettrekker.mountaintrekkingadviser.controller.search.SlidingSearchResultsFragment;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseExampleFragment.BaseExampleFragmentCallbacks {
    private TabLayout tabs;
    private ViewPager viewPager;
    private ImageView imgAvatar;
    private MainScreenPagerAdapter adapter;
    private DrawerLayout drawer;
    public static User user;
    private FrameLayout frame;

    private LocationManager location;
    private LatLng mLatLng;
    private TextView tvMainTitle;
    private SwipeRefreshLayout swipeContainer;
    private NavigationView navigationView;
    private android.support.design.widget.FloatingActionButton btnAddNew;

    private ImageButton search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initSearchFrame();
        initLocationListener();
    }

    private void initSearchFrame() {

    }

    public void swipeTab(int index) {
        viewPager.setCurrentItem(index);
    }

    private void init() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnAddNew = (FloatingActionButton) findViewById(R.id.btnAddNew);

        imgAvatar = (ImageView) findViewById(R.id.imgMainAvatar);
        imgAvatar.setOnClickListener((v) -> drawer.openDrawer(GravityCompat.START));

        View header = navigationView.getHeaderView(0);
        ImageView imgNavAvatar = (ImageView) header.findViewById(R.id.imgNavAvatar);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostAddActivity.class);
                startActivity(intent);
            }
        });

        imgNavAvatar.setOnClickListener((v) -> {
            Intent i = new Intent(this, ProfileMemberActivity.class);
            i.putExtra("firstname", user.getFirstName());
            i.putExtra("lastname", user.getLastName());
            i.putExtra("email", user.getEmail());
            i.putExtra("birthdate", user.getBirthDate().getTime());
            i.putExtra("phone", user.getPhone());
            i.putExtra("gender", user.getGender());
            i.putExtra("owner", true);
            i.putExtra("id", user.getId());
            i.putExtra("token", user.getToken());
            //i.putExtra("avatar", user.getGallery().getMedia().get(0).getPath());
            drawer.closeDrawer(GravityCompat.START);
            startActivity(i);
        });

        if (!user.getGallery().getMedia().get(0).getPath().isEmpty()) {

            GlideApp.with(this)
                    .load(APIUtils.BASE_URL_API + user.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + LocalDisplay.dp2px(80, this))
                    .placeholder(getDrawable(R.drawable.avatar_default))
                    .fallback(getDrawable(R.drawable.avatar_default))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgAvatar);

            GlideApp.with(this)
                    .load(APIUtils.BASE_URL_API + user.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + LocalDisplay.dp2px(80, this))
                    .placeholder(getDrawable(R.drawable.avatar_default))
                    .fallback(getDrawable(R.drawable.avatar_default))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgNavAvatar);
        }

        TextView tvNavName = (TextView) header.findViewById(R.id.tvNavName);
        tvNavName.setText(user.getLastName() + " " + user.getFirstName());

        TextView tvNavEmail = (TextView) header.findViewById(R.id.tvNavEmail);
        tvNavEmail.setText(user.getEmail());

        viewPager = (ViewPager) findViewById(R.id.container);
        tabs = (TabLayout) findViewById(R.id.tabs);

        adapter = new MainScreenPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

//        viewPager.setOffscreenPageLimit(4);

        int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        tabs.getTabAt(0).getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                tab.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

                if (tab.getPosition() == 2) {
                    tvMainTitle.setText("Thông báo");
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
                    default:
                }
            }
        });


        //setupWindowAnimations();
        tvMainTitle = (TextView) findViewById(R.id.tvMainTitle);

        initRefreshLayout();
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
                adapter.setPlanFragment(new PlanFragment());
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
//        Bundle transitionBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, findViewById(R.id.imgPlaceCover), "placeImg").toBundle();
//        adapter.setTransitionBundle(transitionBundle);

        SlidingSearchResultsFragment fragment = new SlidingSearchResultsFragment();
        search = (ImageButton) findViewById(R.id.search);
        frame = (FrameLayout) findViewById(R.id.search_frame);
        fragment.setSearch(search);
        fragment.setFrame(frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.search_frame, fragment).commit();
//        adapter.setSearchFragment(searchFragment);
        fragment.setSearchFragment(adapter.getSearchFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        //item.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

        MenuItem item1 = navigationView.getMenu().findItem(R.id.nav_profile);

        if (item.equals(item1)) {
            Intent i = new Intent(this, ProfileMemberActivity.class);
            i.putExtra("firstname", user.getFirstName());
            i.putExtra("lastname", user.getLastName());
            i.putExtra("email", user.getEmail());
            i.putExtra("birthdate", user.getBirthDate().getTime());
            i.putExtra("phone", user.getPhone());
            i.putExtra("gender", user.getGender());
            i.putExtra("owner", true);
            i.putExtra("id", user.getId());
            i.putExtra("viewProfile", true);
            i.putExtra("token", user.getToken());
            if (user.getGallery() != null) {
                i.putExtra("avatar", user.getGallery().getMedia().get(0).getPath());
            }
            drawer.closeDrawer(GravityCompat.START);
            startActivity(i);
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
//            }
        }


    }

    private void buildAlertDialogNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vui lòng bật GPS trên thiết bị của bạn")
                .setCancelable(false)
                .setPositiveButton("Bật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(drawer);
    }
}



