package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.arasthel.spannedgridlayoutmanager.SpanLayoutParams;
import com.arasthel.spannedgridlayoutmanager.SpanSize;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.model.ImageSize;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.util.ImageUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {
    private List<MyMedia> medias;
    private Context context;
    private boolean[] itemClicked;
    private int position;
    private boolean isPreview;
    private RecyclerView rcv;
    private float ratio1 = 1;
    private float ratio2 = 1;
    private int size = 0;
    private int width = 0;

    public void setRatio1(float ratio1) {
        this.ratio1 = ratio1;
    }

    public void setRatio2(float ratio2) {
        this.ratio2 = ratio2;
    }

    public void setRcv(RecyclerView rcv) {
        this.rcv = rcv;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public void setContext(Context context) {
        this.context = context;
        width = LocalDisplay.getScreenWidth(context);
    }

    public void setMedias(List<MyMedia> medias) {
        this.medias = medias;
        this.itemClicked = new boolean[medias.size()];
        if (medias.size() > 4) {
            if (ratio1 >= 1 & ratio1 <= 1.1) {
                size =  4;
            } else {
                if (Math.random() < 0.5) {
                    size =  4;
                } else {
                    size = 5;
                }
            }
        } else {
            size = medias.size();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        GridItemView item = new GridItemView(viewGroup.getContext());
        return new PostImageAdapter.ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        APIService mWebService = APIUtils.getWebService();
        if (isPreview) {
            switch (size) {
                case 2:
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                            + medias.get(i).getPath().substring(4) + "&w=" + width);
                    if ((ratio1 + ratio2) / 2 < 1) {
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 1)));
                    } else  {
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(1, 2)));
                    }
                    break;
                case 3:
                    if (i == 0) {
                        if (ratio1 < 1) {
                            ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                    + medias.get(i).getPath().substring(4) + "&w=" + width);
                            viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 1)));
                        } else {
                            ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                    + medias.get(i).getPath().substring(4) + "&w=" + width);
                            viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(1, 2)));
                        }
                    } else {
                        ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                + medias.get(i).getPath().substring(4) + "&w=" + width);
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(1, 1)));
                    }

                    break;
                default:
                    if (ratio1 < 1.1 & ratio1 >= 1) {
                        ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                + medias.get(i).getPath().substring(4) + "&w=" + width);
                        if ( i == 3) {
                            ((GridItemView) viewHolder.itemView).setText(medias.size() - 4, 21);
                        }
                        viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(1, 1)));
                    } else {
                        if (size == 4) {
                            if (i == 0) {
                                if (ratio1 < 1) {
                                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                            + medias.get(i).getPath().substring(4) + "&w=" + width);
                                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(6, 4)));
                                } else {
                                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                            + medias.get(i).getPath().substring(4) + "&w=" + width);
                                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(4, 6)));
                                }
                            } else {
                                ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                        + medias.get(i).getPath().substring(4) + "&w=" + width);
                                if (i == 3 && medias.size() > 4) {
                                    ((GridItemView) viewHolder.itemView).setText(medias.size() - 4, 21);
                                }
                                viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 2)));
                            }
                        } else {
                            if (i == 0 || i == 1) {
                                if (ratio1 >= 1) {
                                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                            + medias.get(i).getPath().substring(4) + "&w=" + width);
                                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(3, 4)));
                                } else {
                                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                            + medias.get(i).getPath().substring(4) + "&w=" + width);
                                    viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(4, 3)));
                                }
                            } else {
                                ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API
                                        + medias.get(i).getPath().substring(4) + "&w=" + width);
                                if (i == 4 & medias.size() > 5) {
                                    ((GridItemView) viewHolder.itemView).setText(medias.size() - 5, 21);
                                }
                                viewHolder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(2, 2)));
                            }
                        }
                    }
            }
        } else {
            MyMedia media = medias.get(i);
            mWebService.getImageSize(media.getPath()).enqueue(new Callback<ImageSize>() {
                @Override
                public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                    ((GridItemView) viewHolder.itemView).setImage(APIUtils.BASE_URL_API + media.getPath().substring(4) + "&w=" + LocalDisplay.getScreenWidth(context));
                }

                @Override
                public void onFailure(Call<ImageSize> call, Throwable t) {

                }
            });

            int width = itemClicked[i] ? 2 : 1;
            int height = itemClicked[i] ? 2 : 1;
            SpanSize spanSize = new SpanSize(width, height);
            viewHolder.itemView.setLayoutParams(new SpanLayoutParams(spanSize));

            viewHolder.itemView.setOnClickListener((v) -> {
                itemClicked[position] = false;
                itemClicked[i] = true;
                notifyItemChanged(position);
                notifyItemChanged(i);
                position = i;
            });
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
