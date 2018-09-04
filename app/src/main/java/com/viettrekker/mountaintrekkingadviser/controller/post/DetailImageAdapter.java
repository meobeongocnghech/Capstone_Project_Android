package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DetailImageAdapter extends RecyclerView.Adapter<DetailImageAdapter.ViewHolder> {

    private HashMap<Integer, float[]> map;
    private int limit = 3;
    private int idx = 0;
    private int width;
    private List<MyMedia> medias;
    private List<String> lists;
    private Context context;
    private Pair<View, String>[] transitionPairs;
    private PostDetailActivity activity;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMap(HashMap<Integer, float[]> map) {
        this.map = map;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setActivity(PostDetailActivity activity) {
        this.activity = activity;
    }

    public void setMedias(List<MyMedia> medias) {
        this.medias = medias;
        lists = new ArrayList<>();
        for (MyMedia m : medias) {
            lists.add(m.getPath());
        }
        transitionPairs = new Pair[lists.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_detail_image, viewGroup, false);
        return new DetailImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        float[] ratios = map.get(i);
        switch (ratios.length) {
            case 1:
                viewHolder.img1.setVisibility(View.VISIBLE);
                viewHolder.img1.getLayoutParams().width = width;
                viewHolder.img1.getLayoutParams().height = (int) (width * ratios[0]);
                viewHolder.img1.requestLayout();
                int i1 = idx;
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + width)
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(viewHolder.img1);

                viewHolder.img2.setVisibility(View.GONE);
                viewHolder.img3.setVisibility(View.GONE);

                viewHolder.layout.getLayoutParams().height = (int) (width * ratios[0]);
                viewHolder.layout.requestLayout();
                viewHolder.img1.setTransitionName(medias.get(idx).getPath());
                transitionPairs[idx] = Pair.create(viewHolder.img1, medias.get(idx).getPath());
                viewHolder.img1.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailImageActivity.class);
                    intent.putExtra("listImages", (Serializable) lists);
                    intent.putExtra("title", title);
                    intent.putExtra("current", i1);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, transitionPairs);
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                });
                idx++;
                break;
            case 2:
                int height = (int) ((width * ratios[0] * ratios[1]) / (ratios[0] + ratios[1]));

                viewHolder.img1.setVisibility(View.VISIBLE);
                viewHolder.img1.getLayoutParams().width = (int) (height / ratios[0]);
                viewHolder.img1.getLayoutParams().height = height;
                viewHolder.img1.requestLayout();
                int i2 = idx;
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height / ratios[0]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(viewHolder.img1);
                viewHolder.img1.setTransitionName(medias.get(idx).getPath());
                transitionPairs[idx] = Pair.create(viewHolder.img1, medias.get(idx).getPath());
                viewHolder.img1.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailImageActivity.class);
                    intent.putExtra("listImages", (Serializable) lists);
                    intent.putExtra("title", title);
                    intent.putExtra("current", i2);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, transitionPairs);
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                });
                idx++;

                viewHolder.img2.setVisibility(View.VISIBLE);
                viewHolder.img2.getLayoutParams().width = (int) (height / ratios[1]);
                viewHolder.img2.getLayoutParams().height = height;
                viewHolder.img2.requestLayout();
                int i3 = idx;
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height / ratios[1]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(viewHolder.img2);
                viewHolder.img2.setTransitionName(medias.get(idx).getPath());
                transitionPairs[idx] = Pair.create(viewHolder.img2, medias.get(idx).getPath());

                viewHolder.img2.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailImageActivity.class);
                    intent.putExtra("listImages", (Serializable) lists);
                    intent.putExtra("title", title);
                    intent.putExtra("current", i3);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, transitionPairs);
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                });

                viewHolder.img3.setVisibility(View.GONE);

                viewHolder.layout.getLayoutParams().height = height;
                viewHolder.layout.requestLayout();
                idx++;
                break;
            case 3:
                float sum = ratios[0] * ratios[1] + ratios[1] * ratios[2] + ratios[0] * ratios[2];
                int height1 = (int) ((width * ratios[0] * ratios[1] * ratios[2]) / sum);

                viewHolder.img1.setVisibility(View.VISIBLE);
                viewHolder.img1.getLayoutParams().width = (int) (height1 / ratios[0]);
                viewHolder.img1.getLayoutParams().height = height1;
                viewHolder.img1.requestLayout();
                int i4 = idx;
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height1 / ratios[0]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(viewHolder.img1);
                viewHolder.img1.setTransitionName(medias.get(idx).getPath());
                transitionPairs[idx] = Pair.create(viewHolder.img1, medias.get(idx).getPath());

                viewHolder.img1.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailImageActivity.class);
                    intent.putExtra("listImages", (Serializable) lists);
                    intent.putExtra("title", title);
                    intent.putExtra("current", i4);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, transitionPairs);
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                });
                idx++;

                viewHolder.img2.setVisibility(View.VISIBLE);
                viewHolder.img2.getLayoutParams().width = (int) (height1 / ratios[1]);
                viewHolder.img2.getLayoutParams().height = height1;
                viewHolder.img2.requestLayout();
                int i5 = idx;
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height1 / ratios[1]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(viewHolder.img2);
                viewHolder.img2.setTransitionName(medias.get(idx).getPath());
                transitionPairs[idx] = Pair.create(viewHolder.img2, medias.get(idx).getPath());

                viewHolder.img2.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailImageActivity.class);
                    intent.putExtra("listImages", (Serializable) lists);
                    intent.putExtra("title", title);
                    intent.putExtra("current", i5);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, transitionPairs);
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                });
                idx++;

                viewHolder.img3.setVisibility(View.VISIBLE);
                viewHolder.img3.getLayoutParams().width = (int) (height1 / ratios[2]);
                viewHolder.img3.getLayoutParams().height = height1;
                viewHolder.img3.requestLayout();
                int i6 = idx;
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height1 / ratios[2]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(viewHolder.img3);
                viewHolder.img3.setTransitionName(medias.get(idx).getPath());
                transitionPairs[idx] = Pair.create(viewHolder.img3, medias.get(idx).getPath());

                viewHolder.img3.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailImageActivity.class);
                    intent.putExtra("listImages", (Serializable) lists);
                    intent.putExtra("title", title);
                    intent.putExtra("current", i6);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, transitionPairs);
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                });
                idx++;

                viewHolder.layout.getLayoutParams().height = height1;
                viewHolder.layout.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img1;
        ImageView img2;
        ImageView img3;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img1 = (ImageView) itemView.findViewById(R.id.img1);
            img2 = (ImageView) itemView.findViewById(R.id.img2);
            img3 = (ImageView) itemView.findViewById(R.id.img3);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }
}
