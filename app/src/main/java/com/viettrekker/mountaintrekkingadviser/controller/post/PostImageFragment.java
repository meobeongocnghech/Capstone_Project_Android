package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

public class PostImageFragment extends Fragment {

    public static PostImageFragment newInstance(String path) {
        PostImageFragment fragment = new PostImageFragment();
        Bundle arguments = new Bundle();
        arguments.putString("transitionName", path);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_detail_image_item, container, false);

        Bundle bundle = getArguments();
        String path = bundle.getString("transitionName");
        view.findViewById(R.id.myZoomageView).setTransitionName(path);

        GlideApp.with(this)
                .load(APIUtils.BASE_URL_API + path.substring(4) + "&w=" + LocalDisplay.getScreenWidth(getContext()))
                .into((ImageView) view.findViewById(R.id.myZoomageView));
        return view;
    }
}
