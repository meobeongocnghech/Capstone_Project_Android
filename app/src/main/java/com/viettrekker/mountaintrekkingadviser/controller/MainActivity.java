package com.viettrekker.mountaintrekkingadviser.controller;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.animator.CircleTransform;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostFragment;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.User;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

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



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
