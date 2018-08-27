package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.viettrekker.mountaintrekkingadviser.GlideApp;

public class PlaceDetailActivity extends AppCompatActivity {
    private RecyclerView rvGuide;
    private String title;
    private List<MyMedia> medias;
    private TextView tvPlaceAddress;
    private TextView tvPlaceDistance;
    private TextView tvPlaceTotalPlan;
    private TextView tvPlaceDescription;
    Place place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        init();
        //setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        getWindow().setExitTransition(fade);
        getWindow().setSharedElementExitTransition(new Explode());
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.placeToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvPlaceAddress = (TextView) findViewById(R.id.tvPlaceAddress);
        tvPlaceAddress.setText(getIntent().getStringExtra("address"));
        tvPlaceDistance = (TextView) findViewById(R.id.tvPlaceDistance);
        tvPlaceDistance.setText(getIntent().getStringExtra("distance"));
        tvPlaceTotalPlan = (TextView) findViewById(R.id.tvPlaceTotalPlan);
        tvPlaceTotalPlan.setText(getIntent().getStringExtra("total"));
        tvPlaceDescription = (TextView) findViewById(R.id.tvPlaceDescription);
        tvPlaceDescription.setText(getIntent().getStringExtra("description"));

        rvGuide = (RecyclerView) findViewById(R.id.rvListGuide);
        rvGuide.setLayoutManager(new LinearLayoutManager(this));
        NewsFeedAdapter adapter = new NewsFeedAdapter(getApplicationContext(), null, Session.getToken(this));

        int id = getIntent().getIntExtra("id", 0 );
        String token = Session.getToken(this);

        APIService mWebService = APIUtils.getWebService();
        mWebService.getPlaceById(token, id).enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                place = response.body();
                medias = place.getGallery().getMedia();
                title = place.getName();

                CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.placeCollapsingToolbar);
                toolbarLayout.setTitle(title);
                ImageView imgCover = (ImageView) findViewById(R.id.placeToolbarImage);
                Point size;
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                size = new Point();
                wm.getDefaultDisplay().getSize(size);
                imgCover.getLayoutParams().height = (int) (size.x * 1.5 / 3);
                imgCover.requestLayout();
                toolbarLayout.getLayoutParams().height = (int) (size.x * 1.5 / 3);
                toolbarLayout.requestLayout();
                GlideApp.with(PlaceDetailActivity.this)
                        .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + LocalDisplay.getScreenWidth(getBaseContext()))
                        .fallback(R.drawable.default_background)
                        .placeholder(R.drawable.default_background)
                        .into(imgCover);
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {

            }
        });

        adapter.setByPlaceId(true);
        adapter.setPlaceId(id);
        mWebService.getPostPageByPlaceId(token, 1, 5, id, "DESC").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null && !response.body().isEmpty()) {
                    List<Post> list = response.body();
                    list.remove(0);
                    adapter.setListPost(list);
                }
                rvGuide.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });

//        mWebService.searchPostSuggestion()
    }

    @Override
    public boolean onSupportNavigateUp() {
        //supportFinishAfterTransition();
        onBackPressed();
        return true;
    }
}
