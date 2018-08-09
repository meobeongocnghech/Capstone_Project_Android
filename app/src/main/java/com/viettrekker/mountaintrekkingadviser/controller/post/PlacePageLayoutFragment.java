package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import android.support.design.button.MaterialButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.MyLocation;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import android.support.v4.app.Fragment;

import java.io.IOException;
import java.util.List;

public class PlacePageLayoutFragment extends Fragment {
    private Place place;
    private ShimmerFrameLayout mShimmer;
    private ImageView imgCover;
    private TextView tvPlaceName;
    private TextView tvPlaceAddress;
    private TextView tvPlaceDistance;
    private TextView tvPlaceTotalPlan;
    private TextView tvPlaceDescription;
    private MaterialButton btnViewDetail;

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_page_place_item, container, false);
        imgCover = (ImageView) view.findViewById(R.id.imgPlaceCover);
        mShimmer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer);
        tvPlaceName = (TextView) view.findViewById(R.id.tvPlaceName);
        tvPlaceAddress = (TextView) view.findViewById(R.id.tvPlaceAddress);
        tvPlaceDistance = (TextView) view.findViewById(R.id.tvPlaceDistance);
        tvPlaceTotalPlan = (TextView) view.findViewById(R.id.tvPlaceTotalPlan);
        tvPlaceDescription = (TextView) view.findViewById(R.id.tvPlaceDescription);
        btnViewDetail = (MaterialButton) view.findViewById(R.id.btnPlaceView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmer.startShimmer();
    }

    @Override
    public void onPause() {
        mShimmer.stopShimmer();
        super.onPause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Point size;
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        size = new Point();
        wm.getDefaultDisplay().getSize(size);
        imgCover.getLayoutParams().height = (int) (size.x * 1.5 / 3);
        imgCover.requestLayout();

        ImageView holder = (ImageView) view.findViewById(R.id.imgPlaceCoverHolder);
        holder.getLayoutParams().height = (int) (size.x * 1.5 / 3);
        holder.requestLayout();

        btnViewDetail.setOnClickListener((v) -> {
            Intent i = new Intent(view.getContext(), PlaceDetailActivity.class);
            i.putExtra("id", place.getId());
            i.putExtra("name", place.getName());
            i.putExtra("img", APIUtils.BASE_URL_API + place.getGallery().getMedia().get(0).getPath().substring(4));
            startActivity(i);
        });

        bindData(view);
    }

    private void bindData(View view) {
        String addressName = "Chưa rõ";
        LocationManager location;
        Geocoder geoCoder = new Geocoder(getContext());
        double lat = place.getLocation().getLatitude();
        double lng = place.getLocation().getLongitude();
        try {
            List<Address> list = geoCoder.getFromLocation(lat,lng,1);
            String address = list.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            addressName = address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Calculate distance
        location = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // CHecking network provider enable
        if (location.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            System.out.println("Vao den network");
            location.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    double tlat =  location.getLatitude();
                    double tlng =  location.getLongitude();
                    android.location.Location myLoc = new android.location.Location("");
                    myLoc.setLatitude(tlat);
                    myLoc.setLongitude(tlng);
                    android.location.Location targetLoc = new android.location.Location("");
                    targetLoc.setLatitude(lat);
                    targetLoc.setLongitude(lng);
                    tvPlaceDistance.setText("Khoảng "+(int)myLoc.distanceTo(targetLoc)/1000 + " km");


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
        } else if (location.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    double tlat =  location.getLatitude();
                    double tlng =  location.getLongitude();
                    android.location.Location myLoc = new android.location.Location("");
                    myLoc.setLatitude(tlat);
                    myLoc.setLongitude(tlng);
                    android.location.Location targetLoc = new android.location.Location("");
                    targetLoc.setLatitude(lat);
                    targetLoc.setLongitude(lng);
                    tvPlaceDistance.setText("Khoảng "+(int)myLoc.distanceTo(targetLoc)/1000 + " km");
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


        MyLocation userLoc = new MyLocation();
        tvPlaceName.setText(place.getName());
        tvPlaceAddress.setText(addressName);
        tvPlaceDistance.setText("Chưa rõ");
        tvPlaceTotalPlan.setText("Chưa rõ");
        tvPlaceDescription.setText(place.getDescription());

        if (place.getGallery().getMedia().size() != 0) {
            Picasso.get()
                    .load(APIUtils.BASE_URL_API + place.getGallery().getMedia().get(0).getPath().substring(4))
                    .into(imgCover, new Callback() {
                        @Override
                        public void onSuccess() {
                            mShimmer.stopShimmer();
                            mShimmer.setVisibility(View.GONE);
                            view.findViewById(R.id.constrainPlaceItem).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        } else {
            view.findViewById(R.id.constrainPlaceItem).setVisibility(View.VISIBLE);
        }
    }
}
