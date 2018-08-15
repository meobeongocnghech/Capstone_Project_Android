package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.arasthel.spannedgridlayoutmanager.SpanLayoutParams;
import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.viettrekker.mountaintrekkingadviser.model.ImageSize;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {
    private List<MyMedia> medias;
    private Context context;
    private boolean[] itemClicked;
    private int position;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMedias(List<MyMedia> medias) {
        this.medias = medias;
        this.itemClicked = new boolean[medias.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        GridItemView1 item = new GridItemView1(viewGroup.getContext());
        return new PostImageAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MyMedia media = medias.get(i);
        APIService mWebService = APIUtils.getWebService();
        mWebService.getImageSize(media.getPath()).enqueue(new Callback<ImageSize>() {
            @Override
            public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                ((GridItemView1) viewHolder.itemView).setImage(APIUtils.BASE_URL_API + media.getPath().substring(4) + "&w=" + LocalDisplay.getScreenWidth(context));
            }

            @Override
            public void onFailure(Call<ImageSize> call, Throwable t) {

            }
        });

        int width = itemClicked[i] ? 3 : 1;
        int height = itemClicked[i] ? 3 : 1;
        SpanSize spanSize = new SpanSize(width, height);
        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(spanSize));

//        viewHolder.itemView.setOnClickListener((v)-> {
////            itemClicked[position] = false;
////            itemClicked[i] = true;
////            notifyItemChanged(position);
////            notifyItemChanged(i);
////            position = i;
//            Collections.swap(medias, medias.size() - 1, i);
//            notifyItemChanged(medias.size() - 1);
//            notifyItemChanged(i);
//        });
    }

    @Override
    public int getItemCount() {
        if (medias == null) {
            medias = new ArrayList<>();
        }
        return medias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
