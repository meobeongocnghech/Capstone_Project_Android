package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.io.IOException;
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
    private final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
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
//        tvPlaceAddress.setText(getIntent().getStringExtra("address"));
        tvPlaceDistance = (TextView) findViewById(R.id.tvPlaceDistance);
//        tvPlaceDistance.setText(getIntent().getStringExtra("distance"));
        tvPlaceTotalPlan = (TextView) findViewById(R.id.tvPlaceTotalPlan);
//        tvPlaceTotalPlan.setText(getIntent().getStringExtra("total"));
        tvPlaceDescription = (TextView) findViewById(R.id.tvPlaceDescription);
//        tvPlaceDescription.setText(getIntent().getStringExtra("description"));

        rvGuide = (RecyclerView) findViewById(R.id.rvListGuide);
        rvGuide.setLayoutManager(new LinearLayoutManager(this));
        NewsFeedAdapter adapter = new NewsFeedAdapter(this, null, Session.getToken(this));

        int id = getIntent().getIntExtra("id", 0);
        String token = Session.getToken(this);

        APIService mWebService = APIUtils.getWebService();
        mWebService.getPlaceById(token, id).enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                if (response.code() == 200) {
                    place = response.body();
                    medias = place.getGallery().getMedia();
                    title = place.getName();
                    tvPlaceDescription.setText(place.getDescription());
                    tvPlaceTotalPlan.setText("Chưa có");
                    Geocoder geoCoder = new Geocoder(PlaceDetailActivity.this);
                    double lat = place.getLocation().getLatitude();
                    double lng = place.getLocation().getLongitude();
                    Location targetLoc = new Location("");
                    targetLoc.setLatitude(lat);
                    targetLoc.setLongitude(lng);
                    try {
                        List<Address> list = geoCoder.getFromLocation(lat, lng, 1);
                        if (list.isEmpty()) {
                            tvPlaceAddress.setText("Chưa có");
                        } else {
                            String rsAddress = list.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            tvPlaceAddress.setText(rsAddress);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    LatLng mLatLng = new LatLng(0 ,0);

                    if (location.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        if (ActivityCompat.checkSelfPermission(PlaceDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PlaceDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            buildAlertDialogNoGPS();
                            return;
                        }
                        mLatLng = new LatLng(location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(),
                                location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
                    } else if (location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        mLatLng = new LatLng(location.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
                                location.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
                    }
                    if (mLatLng != null) {
                        Location myLoc = new Location("");
                        myLoc.setLatitude(mLatLng.latitude);
                        myLoc.setLongitude(mLatLng.longitude);
                        float dist = myLoc.distanceTo(targetLoc) / 1000;
                        tvPlaceDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + "km");
                    } else {
                        tvPlaceDistance.setText("Chưa có");
                    }

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

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {

            }
        });

        adapter.setUserId(Session.getUserId(this));
        adapter.setByPlaceId(true);
        adapter.setPlaceId(id);
        mWebService.getPostPageByPlaceId(token, 1, 5, id, "DESC").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null && !response.body().isEmpty()) {
                    List<Post> list = response.body();
                    list.remove(0);
                    adapter.setListPost(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });

        rvGuide.setAdapter(adapter);

        rvGuide.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    adapter.incrementalLoad();
                }
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

    private void buildAlertDialogNoGPS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }
}
