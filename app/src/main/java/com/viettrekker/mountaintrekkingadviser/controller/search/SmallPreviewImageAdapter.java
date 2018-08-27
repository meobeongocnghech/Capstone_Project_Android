package com.viettrekker.mountaintrekkingadviser.controller.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.arasthel.spannedgridlayoutmanager.SpanLayoutParams;
import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.viettrekker.mountaintrekkingadviser.controller.post.GridItemView;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

public class SmallPreviewImageAdapter extends RecyclerView.Adapter<SmallPreviewImageAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private List<MyMedia> medias;
    private Context context;
    private RecyclerView rcv;

    private float ratio1 = 1;
    private float ratio2 = 1;

    public void setRatio2(float ratio2) {
        this.ratio2 = ratio2;
    }

    public void setRatio1(float ratio1) {
        this.ratio1 = ratio1;
    }

    public void setRcv(RecyclerView rcv) {
        this.rcv = rcv;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setMedias(List<MyMedia> medias) {
        this.medias = medias;
    }

    @NonNull
    @Override
    public SmallPreviewImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        GridItemView item = new GridItemView(viewGroup.getContext());
        return new SmallPreviewImageAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull SmallPreviewImageAdapter.ViewHolder viewHolder, int i) {
        int size = medias.size();
        int width = LocalDisplay.dp2px(160, context);

        switch (size) {
            case 1:
                if (ratio1 >= 1.5) {
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                            + medias.get(0).getPath().substring(4) + "&h=" + width);
                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(6, 9)));
                } else if (ratio1 < 1) {
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                            + medias.get(0).getPath().substring(4) + "&w=" + width);
                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(9, Math.round(9 * ratio1))));
                } else {
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                            + medias.get(0).getPath().substring(4) + "&h=" + width);
                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(Math.round(9 / ratio1), 9)));
                }
                break;
            case 2:
                if ((ratio1 + ratio2) / 2 < 1) {
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                            + medias.get(i).getPath().substring(4) + "&w=" + width);
                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 1)));
                } else {
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                            + medias.get(i).getPath().substring(4) + "&h=" + width);
                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(1, 2)));
                }
                break;
            default:
                if (ratio1 <= 1) {
                    if (i == 0) {
                        ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                + medias.get(i).getPath().substring(4) + "&w=" + width);
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(4, 2)));
                    } else {
                        ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                + medias.get(i).getPath().substring(4) + "&w=" + width/2);
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 2)));
                    }
                } else {
                    if (i == 0) {
                        ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                + medias.get(i).getPath().substring(4) + "&h=" + width);
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 4)));
                    } else {
                        ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                + medias.get(i).getPath().substring(4) + "&h=" + width/2);
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 2)));
                    }
                }
                if (i == 2 && size > 3) {
                    ((GridItemView) viewHolder.itemView).setText(size - 3, 16);
                }

        }
    }

    @Override
    public int getItemCount() {
        if (medias == null) {
            medias = new ArrayList<>();
        }
        return medias.size();
    }
}
