package com.viettrekker.mountaintrekkingadviser.controller;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.animator.CircleTransform;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.post.PagePlaceFragment;
import com.viettrekker.mountaintrekkingadviser.model.User;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.ViewPager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabs;
    private ViewPager viewPager;
    private ImageView imgAvatar;
    private MainScreenPagerAdapter adapter;
    private DrawerLayout drawer;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imgAvatar = (ImageView) findViewById(R.id.imgMainAvatar);
        imgAvatar.setOnClickListener((v) -> drawer.openDrawer(GravityCompat.START));

        View header = navigationView.getHeaderView(0);
        ImageView imgNavAvatar = (ImageView) header.findViewById(R.id.imgNavAvatar);

        if (user.getGallery() != null) {
            Picasso.get().load("https://imgur.com/jKq7grX123.png")
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.avatar_default)
                    .into(imgAvatar);

            Picasso.get().load("https://imgur.com/jKq7grX123.png")
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.avatar_default)
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

        int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        tabs.getTabAt(0).getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                tab.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.colorBlack);
                tab.getIcon().setColorFilter(selectColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //setupWindowAnimations();
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
