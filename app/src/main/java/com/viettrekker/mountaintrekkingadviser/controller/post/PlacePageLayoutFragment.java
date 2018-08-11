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
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.model.LatLng;
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

    public ImageView getImgCover() {
        return imgCover;
    }

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
            if (list.isEmpty()) {
                addressName = "Chưa có";
            } else {
                String address = list.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                addressName = address;
            }
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
                    LatLng myLoc = new LatLng(tlat,tlng);
                    LatLng targetLoc = new LatLng(lat,lng);
//                    GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
////                    GoogleDirection.withServerKey("AIzaSyDtbcVZthXcQno7KYa-Rf-4jtVUgjYa_4s")
//                            .from(myLoc)
//                            .to(targetLoc)
//                            .transportMode(TransportMode.DRIVING).
//                            avoid(AvoidType.HIGHWAYS)
//                            .alternativeRoute(true)
//                            .execute(new DirectionCallback() {
//                                @Override
//                                public void onDirectionSuccess(Direction direction, String rawBody) {
//                                    String status = direction.getStatus();
//                                    if(status.equals(RequestResult.OK)) {
//                                        Route route = direction.getRouteList().get(0);
//                                        Leg leg = route.getLegList().get(0);
//                                        double distance = Double.parseDouble(leg.getDistance().getValue()) ;
//                                        distance = distance*0.001;
//                                        tvPlaceDistance.setText("Khoảng " + (double) Math.floor(distance * 10) / 10 + "km");
//                                    } else if(status.equals(RequestResult.NOT_FOUND)) {
//                                        // Do something
//                                    }
//                                }
//
//                                @Override
//                                public void onDirectionFailure(Throwable t) {
//                                }
//                            });


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
                    LatLng myLoc = new LatLng(tlat,tlng);
                    LatLng targetLoc = new LatLng(lat,lng);
//                    GoogleDirection.withServerKey("AIzaSyD8WTCySi0IpUz7RnamE1pqbLsCPi8R93w")
//                            .from(myLoc)
//                            .to(targetLoc)
//                            .transportMode(TransportMode.DRIVING).
//                            avoid(AvoidType.HIGHWAYS)
//                            .alternativeRoute(true)
//                            .execute(new DirectionCallback() {
//                                @Override
//                                public void onDirectionSuccess(Direction direction, String rawBody) {
//                                    String status = direction.getStatus();
//                                    if(status.equals(RequestResult.OK)) {
//                                        Route route = direction.getRouteList().get(0);
//                                        Leg leg = route.getLegList().get(0);
//                                        double distance = Double.parseDouble(leg.getDistance().getValue()) ;
//                                        distance = distance*0.001;
//                                        tvPlaceDistance.setText("Khoảng " + (double) Math.floor(distance * 10) / 10);
//                                    } else if(status.equals(RequestResult.NOT_FOUND)) {
//                                        // Do something
//                                    }
//                                }
//
//                                @Override
//                                public void onDirectionFailure(Throwable t) {
//
//                                }
//                            });
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
