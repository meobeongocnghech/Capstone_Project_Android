package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import androidx.fragment.app.Fragment;

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

        btnViewDetail.setOnClickListener((v) -> {
            Intent i = new Intent(view.getContext(), PlaceDetailActivity.class);
            i.putExtra("id", place.getId());
            i.putExtra("name", place.getName());
            i.putExtra("img", APIUtils.BASE_URL_API + place.getGallery().getMedia().get(0).getPath().substring(4));
            startActivity(i);
        });

        bindData();
    }

    private void bindData() {
        tvPlaceName.setText(place.getName());
        tvPlaceAddress.setText("Chưa rõ");
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
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
    }
}
