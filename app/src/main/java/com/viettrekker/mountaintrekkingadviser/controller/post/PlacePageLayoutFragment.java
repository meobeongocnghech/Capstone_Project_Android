package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.io.IOException;
import java.util.List;


public class PlacePageLayoutFragment extends Fragment {
    private LatLng mLatLng = null;
    private Place place;
    private ShimmerFrameLayout mShimmer;
    private ImageView imgCover;
    private TextView tvPlaceName;
    private TextView tvPlaceAddress;
    private TextView tvPlaceDistance;
    private TextView tvPlaceTotalPlan;
    private TextView tvPlaceDescription;

    private String address;
    private String distance;
    private String total;
    private String description;

    public String getAddress() {
        return address;
    }

    public String getDistance() {
        return distance;
    }

    public String getTotal() {
        return total;
    }

    public String getDescription() {
        return description;
    }

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
        distance ="Chưa rõ";
        address = "Chưa rõ";
        description = place.getDescription();
        total = "Chưa rõ";
        LocationManager location;
        Geocoder geoCoder = new Geocoder(getContext());
        double lat = place.getLocation().getLatitude();
        double lng = place.getLocation().getLongitude();
        Location targetLoc = new Location("");
        targetLoc.setLatitude(lat);
        targetLoc.setLongitude(lng);
        try {
            List<Address> list = geoCoder.getFromLocation(lat, lng, 1);
            if (list.isEmpty()) {
                address = "Chưa có";
            } else {
                String rsAddress = list.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                address = rsAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mLatLng = ((MainActivity) getActivity()).getLatLng();
        if (mLatLng != null) {
            Location myLoc = new Location("");
            myLoc.setLatitude(mLatLng.latitude);
            myLoc.setLongitude(mLatLng.longitude);
            float dist = myLoc.distanceTo(targetLoc)/1000;
            distance = "Khoảng " + (double) Math.floor(dist * 10) / 10 + "km";
        } else {
        }
        tvPlaceDistance.setText(distance);
        tvPlaceName.setText(place.getName());
        tvPlaceAddress.setText(address);
        tvPlaceTotalPlan.setText("Chưa rõ");
        tvPlaceDescription.setText(place.getDescription());

        if (place.getGallery().getMedia().size() != 0) {
            GlideApp.with(this)
                    .load(APIUtils.BASE_URL_API + place.getGallery().getMedia().get(0).getPath().substring(4))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mShimmer.stopShimmer();
                            mShimmer.setVisibility(View.GONE);
                            view.findViewById(R.id.constrainPlaceItem).setVisibility(View.VISIBLE);
                            imgCover.setImageDrawable(resource);
                        }
                    });
        } else {
            view.findViewById(R.id.constrainPlaceItem).setVisibility(View.VISIBLE);
        }
    }
}
