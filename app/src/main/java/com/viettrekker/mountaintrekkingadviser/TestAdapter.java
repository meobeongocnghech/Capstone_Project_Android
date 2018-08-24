package com.viettrekker.mountaintrekkingadviser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    List<String> list = new ArrayList<>();
    HashMap<Integer, float[]> map = new HashMap<>();
    int idx = 0;
    int size = 1;
    int width;
    Drawable cache;
    boolean firstLoad = true;
    int limit = 2;
    Context context;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TestAdapter() {
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
        list.add("https://i.imgur.com/1OicD5E.jpg");
        list.add("http://channel.vcmedia.vn/thumb_w/640/prupload/441/2017/03/img20170307154327048.jpg");
        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
        list.add("https://i.imgur.com/1OicD5E.jpg");
        list.add("http://channel.vcmedia.vn/thumb_w/640/prupload/441/2017/03/img20170307154327048.jpg");
        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
        list.add("https://i.imgur.com/1OicD5E.jpg");
        list.add("http://channel.vcmedia.vn/thumb_w/640/prupload/441/2017/03/img20170307154327048.jpg");
//        list.add("http://channel.vcmedia.vn/thumb_w/640/prupload/441/2017/03/img20170307154327048.jpg");
//        list.add("https://i.pinimg.com/originals/54/ce/ed/54ceed06a729801f1e11836c1e3fbf09.jpg");
//        list.add("https://cdn.pose.com.vn/assets/2018/03/pl10-1521175025290257121468.jpg");
//        list.add("http://channel.vcmedia.vn/thumb_w/640/prupload/441/2017/03/img20170307154327048.jpg");
//        list.add("https://znews-photo-td.zadn.vn/w660/Uploaded/kcwvouvs/2018_01_17/phuongly.jpg");
//        list.add("https://znews-photo-td.zadn.vn/w660/Uploaded/kcwvouvs/2018_01_17/phuongly.jpg");
//        list.add("https://i.imgur.com/1OicD5E.jpg");
//        list.add("https://i.pinimg.com/originals/d9/24/b4/d924b4a6ce59d934b85eeee72770dad4.jpg");
//        list.add("https://pbs.twimg.com/media/DaFkHepUQAElAum.jpg");
//        list.add("https://i.ytimg.com/vi/mInZ6LqVsWA/maxresdefault.jpg");
//        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
//        list.add("https://cdn.pose.com.vn/assets/2018/03/pl10-1521175025290257121468.jpg");
//        list.add("https://znews-photo-td.zadn.vn/w660/Uploaded/kcwvouvs/2018_01_17/phuongly.jpg");
//        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
//        list.add("https://i.pinimg.com/originals/54/ce/ed/54ceed06a729801f1e11836c1e3fbf09.jpg");
//        list.add("http://channel.vcmedia.vn/thumb_w/640/prupload/441/2017/03/img20170307154327048.jpg");
//        list.add("https://znews-photo-td.zadn.vn/w1024/Uploaded/unvjuas/2018_01_14/NGUYEN_BA_NGOC2264_ZING.jpg");
//        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
//        list.add("https://cdn.pose.com.vn/assets/2018/03/pl10-1521175025290257121468.jpg");
//        list.add("https://anh.eva.vn//upload/1-2015/images/2015-03-09/1425887344-10.jpg");
//        list.add("https://znews-photo-td.zadn.vn/w1024/Uploaded/unvjuas/2018_01_14/NGUYEN_BA_NGOC2264_ZING.jpg");
//        list.add("http://afamilycdn.com/2018/3/18/photo-7-1521337218757141205539.jpg");
//        list.add("https://i2.wp.com/tieusunguoinoitieng.com/wp-content/uploads/2018/06/tieu-su-nha-phuong-ly-lich-nha-phuong-day-du-chi-tiet-nhat-26.jpg");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_test, viewGroup, false);
        return new TestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        GlideApp.with(viewHolder.itemView)
//                .load(list.get(i))
//                .into(viewHolder.img1);
//        viewHolder.img1.setVisibility(View.VISIBLE);
//        viewHolder.img1.getLayoutParams().width = width;
//        ((TestActivity) context).getRcv().getLayoutParams().height = ((TestActivity) context).getRcv().getLayoutParams().height + LocalDisplay.dp2px(200, context);
//        ((TestActivity) context).getRcv().requestLayout();
//        if (size < list.size()) {
//            size++;
//            notifyItemInserted(size - 1);
//        }

        if (idx < list.size() && firstLoad) {
            if (cache == null) {
                GlideApp.with(viewHolder.itemView)
                        .load(list.get(idx))
                        .placeholder(R.drawable.default_background)
                        .fallback(R.drawable.default_background)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                int tempIdx = idx;
                                idx++;
                                viewHolder.img1.setVisibility(View.VISIBLE);
                                viewHolder.img1.setImageDrawable(resource);
                                float ratio1 = (float) resource.getIntrinsicHeight() / resource.getIntrinsicWidth();

                                if (ratio1 >= 1.2) {
                                    GlideApp.with(viewHolder.itemView)
                                            .load(list.get(idx))
                                            .placeholder(R.drawable.default_background)
                                            .fallback(R.drawable.default_background)
                                            .into(new SimpleTarget<Drawable>() {
                                                @Override
                                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                    idx++;
                                                    viewHolder.img2.setVisibility(View.VISIBLE);
                                                    viewHolder.img2.setImageDrawable(resource);
                                                    float ratio2 = (float) resource.getIntrinsicHeight() / resource.getIntrinsicWidth();

                                                    if (ratio2 >= 1.2) {
                                                        GlideApp.with(viewHolder.itemView)
                                                                .load(list.get(idx))
                                                                .placeholder(R.drawable.default_background)
                                                                .fallback(R.drawable.default_background)
                                                                .into(new SimpleTarget<Drawable>() {
                                                                    @Override
                                                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                                        float ratio3 = (float) resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                                                                        if (ratio3 < 1.2) {
                                                                            cache = null;
                                                                            idx++;
                                                                            viewHolder.img3.setVisibility(View.VISIBLE);
                                                                            viewHolder.img3.setImageDrawable(resource);
                                                                            float sum = ratio1 * ratio2 + ratio2 * ratio3 + ratio1 * ratio3;
                                                                            int height = (int) ((width * ratio1 * ratio2 * ratio3) / sum);

                                                                            viewHolder.img1.getLayoutParams().width = (int ) (height / ratio1);
                                                                            viewHolder.img1.getLayoutParams().height = height;
                                                                            viewHolder.img1.requestLayout();

                                                                            viewHolder.img2.getLayoutParams().width = (int )(height / ratio2);
                                                                            viewHolder.img2.getLayoutParams().height = height;
                                                                            viewHolder.img2.requestLayout();

                                                                            viewHolder.img3.getLayoutParams().width = (int )(height / ratio3);
                                                                            viewHolder.img3.getLayoutParams().height = height;
                                                                            viewHolder.img3.requestLayout();

                                                                            viewHolder.layout.getLayoutParams().height = height;
                                                                            viewHolder.layout.requestLayout();

                                                                            if (idx < list.size() && size <= limit) {
                                                                                size++;
                                                                                notifyItemInserted(size - 1);
                                                                            }

                                                                            map.put(i, new float[]{tempIdx, ratio1, ratio2, ratio3});
                                                                        } else {
                                                                            cache = resource;

                                                                            float sum = ratio1 + ratio2;
                                                                            int height = (int) ((width * ratio1 * ratio2) / sum);
                                                                            viewHolder.img1.getLayoutParams().width = (int) (height / ratio1);
                                                                            viewHolder.img1.getLayoutParams().height = height;
                                                                            viewHolder.img1.requestLayout();

                                                                            viewHolder.img2.getLayoutParams().width = (int) (height / ratio2);
                                                                            viewHolder.img2.getLayoutParams().height = height;
                                                                            viewHolder.img2.requestLayout();

                                                                            viewHolder.layout.getLayoutParams().height = height;
                                                                            viewHolder.layout.requestLayout();

                                                                            if (idx < list.size() && size <= limit) {
                                                                                size++;
                                                                                notifyItemInserted(size - 1);
                                                                            }

                                                                            map.put(i, new float[]{tempIdx, ratio1, ratio2});
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        cache = null;

                                                        float sum = ratio1 + ratio2;
                                                        int height = (int) ((width * ratio1 * ratio2) / sum);
                                                        viewHolder.img1.getLayoutParams().width = (int) (height / ratio1);
                                                        viewHolder.img1.getLayoutParams().height = height;
                                                        viewHolder.img1.requestLayout();

                                                        viewHolder.img2.getLayoutParams().width = (int) (height / ratio2);
                                                        viewHolder.img2.getLayoutParams().height = height;
                                                        viewHolder.img2.requestLayout();

                                                        viewHolder.layout.getLayoutParams().height = height;
                                                        viewHolder.layout.requestLayout();

                                                        if (idx < list.size() && size <= limit) {
                                                            size++;
                                                            notifyItemInserted(size - 1);
                                                        }

                                                        map.put(i, new float[]{tempIdx, ratio1, ratio2});
                                                    }
                                                }
                                            });
                                } else {
                                    cache = null;

                                    viewHolder.img1.getLayoutParams().width = width;
                                    viewHolder.img1.getLayoutParams().height = (int) (width * ratio1);
                                    viewHolder.img1.requestLayout();
                                    viewHolder.layout.getLayoutParams().height = (int) (width * ratio1);
                                    viewHolder.layout.requestLayout();

                                    if (idx < list.size() && size <= limit) {
                                        size++;
                                        notifyItemInserted(size - 1);
                                    }

                                    map.put(i, new float[]{tempIdx, ratio1});
                                }
                            }
                        });
            } else {
                int tempIdx = idx;
                idx++;
                viewHolder.img1.setVisibility(View.VISIBLE);
                viewHolder.img1.setImageDrawable(cache);
                float ratio1 = (float) cache.getIntrinsicHeight() / cache.getIntrinsicWidth();

                if (ratio1 >= 1.2) {
                    GlideApp.with(viewHolder.itemView)
                            .load(list.get(idx))
                            .placeholder(R.drawable.default_background)
                            .fallback(R.drawable.default_background)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    idx++;
                                    viewHolder.img2.setVisibility(View.VISIBLE);
                                    viewHolder.img2.setImageDrawable(resource);
                                    float ratio2 = (float) resource.getIntrinsicHeight() / resource.getIntrinsicWidth();

                                    if (ratio2 >= 1.2) {
                                        GlideApp.with(viewHolder.itemView)
                                                .load(list.get(idx))
                                                .placeholder(R.drawable.default_background)
                                                .fallback(R.drawable.default_background)
                                                .into(new SimpleTarget<Drawable>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                        float ratio3 = (float) resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                                                        if (ratio3 < 1.2) {
                                                            cache = null;
                                                            idx++;
                                                            viewHolder.img3.setVisibility(View.VISIBLE);
                                                            viewHolder.img3.setImageDrawable(resource);
                                                            float sum = ratio1 * ratio2 + ratio2 * ratio3 + ratio1 * ratio3;
                                                            int height = (int) ((width * ratio1 * ratio2 * ratio3) / sum);

                                                            viewHolder.img1.getLayoutParams().width = (int ) (height / ratio1);
                                                            viewHolder.img1.getLayoutParams().height = height;
                                                            viewHolder.img1.requestLayout();

                                                            viewHolder.img2.getLayoutParams().width = (int )(height / ratio2);
                                                            viewHolder.img2.getLayoutParams().height = height;
                                                            viewHolder.img2.requestLayout();

                                                            viewHolder.img3.getLayoutParams().width = (int )(height / ratio3);
                                                            viewHolder.img3.getLayoutParams().height = height;
                                                            viewHolder.img3.requestLayout();

                                                            viewHolder.layout.getLayoutParams().height = height;
                                                            viewHolder.layout.requestLayout();

                                                            if (idx < list.size() && size <= limit) {
                                                                size++;
                                                                notifyItemInserted(size - 1);
                                                            }

                                                            map.put(i, new float[]{tempIdx, ratio1, ratio2, ratio3});
                                                        } else {
                                                            cache = resource;

                                                            float sum = ratio1 + ratio2;
                                                            int height = (int) ((width * ratio1 * ratio2) / sum);
                                                            viewHolder.img1.getLayoutParams().width = (int) (height / ratio1);
                                                            viewHolder.img1.getLayoutParams().height = height;
                                                            viewHolder.img1.requestLayout();

                                                            viewHolder.img2.getLayoutParams().width = (int) (height / ratio2);
                                                            viewHolder.img2.getLayoutParams().height = height;
                                                            viewHolder.img2.requestLayout();

                                                            viewHolder.layout.getLayoutParams().height = height;
                                                            viewHolder.layout.requestLayout();

                                                            if (idx < list.size() && size <= limit) {
                                                                size++;
                                                                notifyItemInserted(size - 1);
                                                            }

                                                            map.put(i, new float[]{tempIdx, ratio1, ratio2});
                                                        }
                                                    }
                                                });
                                    } else {
                                        cache = null;

                                        float sum = ratio1 + ratio2;
                                        int height = (int) ((width * ratio1 * ratio2) / sum);
                                        viewHolder.img1.getLayoutParams().width = (int) (height / ratio1);
                                        viewHolder.img1.getLayoutParams().height = height;
                                        viewHolder.img1.requestLayout();

                                        viewHolder.img2.getLayoutParams().width = (int) (height / ratio2);
                                        viewHolder.img2.getLayoutParams().height = height;
                                        viewHolder.img2.requestLayout();

                                        viewHolder.layout.getLayoutParams().height = height;
                                        viewHolder.layout.requestLayout();

                                        if (idx < list.size() && size <= limit) {
                                            size++;
                                            notifyItemInserted(size - 1);
                                        }

                                        map.put(i, new float[]{tempIdx, ratio1, ratio2});
                                    }
                                }
                            });
                } else {
                    cache = null;

                    viewHolder.img1.getLayoutParams().width = width;
                    viewHolder.img1.getLayoutParams().height = (int) (width * ratio1);
                    viewHolder.img1.requestLayout();
                    viewHolder.layout.getLayoutParams().height = (int) (width * ratio1);
                    viewHolder.layout.requestLayout();

                    if (idx < list.size() && size <= limit) {
                        size++;
                        notifyItemInserted(size - 1);
                    }

                    map.put(i, new float[]{tempIdx, ratio1});
                }
            }
        } else {
            if (firstLoad) {
                firstLoad = false;
                ((TestActivity) context).hideLoadMore();
            } else {
                float[] ratios = map.get(i);
                switch (ratios.length) {
                    case 2:
                        viewHolder.img1.setVisibility(View.VISIBLE);
                        GlideApp.with(viewHolder.itemView)
                                .load(list.get((int) ratios[0]))
                                .placeholder(R.drawable.default_background)
                                .fallback(R.drawable.default_background)
                                .into(viewHolder.img1);
                        viewHolder.img1.getLayoutParams().width = width;
                        viewHolder.img1.getLayoutParams().height = (int) (width * ratios[1]);
                        viewHolder.img1.requestLayout();

                        viewHolder.img2.setVisibility(View.GONE);
                        viewHolder.img3.setVisibility(View.GONE);

                        viewHolder.layout.getLayoutParams().height = (int) (width * ratios[1]);
                        viewHolder.layout.requestLayout();
                        break;
                    case 3:
                        int height = (int) ((width * ratios[1] * ratios[2]) / (ratios[1] + ratios[2]));

                        viewHolder.img1.setVisibility(View.VISIBLE);
                        GlideApp.with(viewHolder.itemView)
                                .load(list.get((int) ratios[0]))
                                .placeholder(R.drawable.default_background)
                                .fallback(R.drawable.default_background)
                                .into(viewHolder.img1);
                        viewHolder.img1.getLayoutParams().width = (int) (height / ratios[1]);
                        viewHolder.img1.getLayoutParams().height = height;
                        viewHolder.img1.requestLayout();

                        viewHolder.img2.setVisibility(View.VISIBLE);
                        GlideApp.with(viewHolder.itemView)
                                .load(list.get((int) ratios[0] + 1))
                                .placeholder(R.drawable.default_background)
                                .fallback(R.drawable.default_background)
                                .into(viewHolder.img2);
                        viewHolder.img2.getLayoutParams().width = (int) (height / ratios[2]);
                        viewHolder.img2.getLayoutParams().height = height;
                        viewHolder.img2.requestLayout();

                        viewHolder.img3.setVisibility(View.GONE);

                        viewHolder.layout.getLayoutParams().height = height;
                        viewHolder.layout.requestLayout();
                        break;
                    case 4:
                        float sum = ratios[1] * ratios[2] + ratios[2] * ratios[3] + ratios[1] * ratios[3];
                        int height1 = (int) ((width * ratios[1] * ratios[2] * ratios[3]) / sum);

                        viewHolder.img1.setVisibility(View.VISIBLE);
                        GlideApp.with(viewHolder.itemView)
                                .load(list.get((int) ratios[0]))
                                .placeholder(R.drawable.default_background)
                                .fallback(R.drawable.default_background)
                                .into(viewHolder.img1);
                        viewHolder.img1.getLayoutParams().width = (int) (height1 / ratios[1]);
                        viewHolder.img1.getLayoutParams().height = height1;
                        viewHolder.img1.requestLayout();

                        viewHolder.img2.setVisibility(View.VISIBLE);
                        GlideApp.with(viewHolder.itemView)
                                .load(list.get((int) ratios[0] + 1))
                                .placeholder(R.drawable.default_background)
                                .fallback(R.drawable.default_background)
                                .into(viewHolder.img2);
                        viewHolder.img2.getLayoutParams().width = (int) (height1 / ratios[2]);
                        viewHolder.img2.getLayoutParams().height = height1;
                        viewHolder.img2.requestLayout();

                        viewHolder.img3.setVisibility(View.VISIBLE);
                        GlideApp.with(viewHolder.itemView)
                                .load(list.get((int) ratios[0] + 2))
                                .placeholder(R.drawable.default_background)
                                .fallback(R.drawable.default_background)
                                .into(viewHolder.img3);
                        viewHolder.img3.getLayoutParams().width = (int) (height1 / ratios[3]);
                        viewHolder.img3.getLayoutParams().height = height1;
                        viewHolder.img3.requestLayout();

                        viewHolder.layout.getLayoutParams().height = height1;
                        viewHolder.layout.requestLayout();
                }
            }
        }
    }

    public void loadMore() {
        limit += 3;
        size++;
        notifyItemInserted(size - 1);
    }

    @Override
    public int getItemCount() {
        return size;
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
