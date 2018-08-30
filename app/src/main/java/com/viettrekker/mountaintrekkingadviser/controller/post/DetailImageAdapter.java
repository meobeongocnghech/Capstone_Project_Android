package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.HashMap;
import java.util.List;

public class DetailImageAdapter extends RecyclerView.Adapter<DetailImageAdapter.ViewHolder> {

    private HashMap<Integer, float[]> map;
    private int limit = 3;
    private int idx = 0;
    private int width;
    private List<MyMedia> medias;

    public void setMap(HashMap<Integer, float[]> map) {
        this.map = map;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setMedias(List<MyMedia> medias) {
        this.medias = medias;
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
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + width)
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.img1);

                viewHolder.img2.setVisibility(View.GONE);
                viewHolder.img3.setVisibility(View.GONE);

                viewHolder.layout.getLayoutParams().height = (int) (width * ratios[0]);
                viewHolder.layout.requestLayout();
                idx++;
                break;
            case 2:
                int height = (int) ((width * ratios[0] * ratios[1]) / (ratios[0] + ratios[1]));

                viewHolder.img1.setVisibility(View.VISIBLE);
                viewHolder.img1.getLayoutParams().width = (int) (height / ratios[0]);
                viewHolder.img1.getLayoutParams().height = height;
                viewHolder.img1.requestLayout();
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height / ratios[0]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.img1);
                idx++;

                viewHolder.img2.setVisibility(View.VISIBLE);
                viewHolder.img2.getLayoutParams().width = (int) (height / ratios[1]);
                viewHolder.img2.getLayoutParams().height = height;
                viewHolder.img2.requestLayout();
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height / ratios[1]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.img2);

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
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height1 / ratios[0]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.img1);
                idx++;

                viewHolder.img2.setVisibility(View.VISIBLE);
                viewHolder.img2.getLayoutParams().width = (int) (height1 / ratios[1]);
                viewHolder.img2.getLayoutParams().height = height1;
                viewHolder.img2.requestLayout();
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height1 / ratios[1]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.img2);
                idx++;

                viewHolder.img3.setVisibility(View.VISIBLE);
                viewHolder.img3.getLayoutParams().width = (int) (height1 / ratios[2]);
                viewHolder.img3.getLayoutParams().height = height1;
                viewHolder.img3.requestLayout();
                GlideApp.with(viewHolder.itemView)
                        .load(APIUtils.BASE_URL_API + medias.get(idx).getPath().substring(4) + "&w=" + (int) (height1 / ratios[2]))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.img3);
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
