package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.ImageUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    List<Post> listPost;
    int pageCount = 2;
    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};
    private boolean isByUserId = false;
    private int userId;
    private Context context;

    public NewsFeedAdapter(Context context) {
        this.context = context;
        this.fragment = fragment;
        this.width = LocalDisplay.getScreenWidth(context) - LocalDisplay.dp2px(40, context);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setListPost(List<Post> listPost) {
        this.listPost = listPost;
    }

    public void setByUserId(boolean b) {
        isByUserId = b;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_post_item, viewGroup, false);

        ViewHolder viewHolder = new NewsFeedAdapter.ViewHolder(view);
        viewHolder.context = context;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int postion) {
        viewHolder.user = listPost.get(postion).getUser();
        viewHolder.postId = listPost.get(postion).getId();
        String typPost = "null";
        int imgSize;
        Post post = listPost.get(postion);
        DateTimeUtils datetime = new DateTimeUtils();
        List<MyMedia> medias = post.getGallery().getMedia();
        List<ImageView> listImgView = new ArrayList<>();
        listImgView.add(viewHolder.imgPreview1);
        listImgView.add(viewHolder.imgPreview2);
        listImgView.add(viewHolder.imgPreview3);
        listImgView.add(viewHolder.imgPreview4);
        typPost = postType[post.getTypeId() - 1];


        if (post.getUser().getGallery() != null) {
            GlideApp.with(context)
                    .load(post.getUser().getGallery().getMedia().get(0).getPath())
                    .apply(RequestOptions.circleCropTransform())
                    .fallback(R.drawable.avatar_default)
                    .into(viewHolder.imgPostAvatar);
        }

        viewHolder.tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
        viewHolder.tvPostCategory.setText(typPost);
        viewHolder.tvPostTitle.setText(post.getName());
        viewHolder.tvPostContent.setText(post.getContent());
        viewHolder.btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like));
        viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorBlack));
        viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorBlack));
        if (post.getLiked() != 0) {
            viewHolder.btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like_pressed));
            viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorPrimary));
            viewHolder.btnPostLike.setText("Đã thích");
        }
        try {
            viewHolder.tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
        } catch (ParseException e) {

        }
        viewHolder.btnReadMore.setVisibility(View.INVISIBLE);
        ViewTreeObserver vto = viewHolder.tvPostContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout l = viewHolder.tvPostContent.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0) {
                        if (l.getEllipsisCount(lines - 1) > 0) {
                            viewHolder.btnReadMore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
//        int lineContent = viewHolder.tvPostContent.getLineCount();
//        if (lineContent > 0){
//            if (viewHolder.tvPostContent.getLayout().getEllipsisCount(lineContent-1) > 0){
//                viewHolder.btnReadMore.setVisibility(View.VISIBLE);
//            }
//        }

        viewHolder.tvCount.setVisibility(View.GONE);
        imgSize = medias.size();
        if (imgSize >= 5) {
            if (Math.random() > 0.5d) {
                imgSize = 5;
            } else {
                imgSize = 4;
            }
        }

        switch (imgSize) {
            case 0:
                break;
            case 1:
                GlideApp.with(context)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                viewHolder.imgPreview1.setImageBitmap(resource);
                                if (resource.getHeight() / resource.getWidth() > 2) {
                                    viewHolder.preview1.getLayoutParams().height = resource.getWidth() * 2;
                                } else if (resource.getWidth() / resource.getHeight() > 2) {
                                    viewHolder.preview1.getLayoutParams().height = resource.getWidth() / 2;
                                }
                                viewHolder.preview1.requestLayout();
                                viewHolder.preview2.setVisibility(View.GONE);
                                viewHolder.preview3.setVisibility(View.GONE);
                                viewHolder.preview4.setVisibility(View.GONE);
                                viewHolder.preview5.setVisibility(View.GONE);
                                viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                            }
                        });
                break;
            case 2:
                float[] size1 = new float[]{1, 2};
                float[] size2 = new float[]{1, 2};
//                GlideApp.with(context)
//                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4))
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
//                                size1[0] = resource.getHeight();
//                                size1[1] = resource.getWidth();
//                                GlideApp.with(context)
//                                        .asBitmap()
//                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                        .skipMemoryCache(true)
//                                        .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4))
//                                        .into(new SimpleTarget<Bitmap>() {
//                                            @Override
//                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
//                                                size2[0] = resource.getHeight();
//                                                size2[1] = resource.getWidth();
//
//                                            }
//                                        });
//                            }
//                        });

                if (ImageUtils.measureGridTwoItems(size1, size2) == ImageUtils.TWO_VERTICAL) {
                    float[] trueSize = new float[]{16, 9};

                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.preview1.getLayoutParams().height = (int)((trueSize[0] / trueSize[1]) * width);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview2);
                    viewHolder.preview2.getLayoutParams().height = (int)((trueSize[0] / trueSize[1]) * width);
                    viewHolder.preview1.requestLayout();
                    viewHolder.preview2.requestLayout();
                    viewHolder.preview3.setVisibility(View.GONE);
                    viewHolder.preview4.setVisibility(View.GONE);
                    viewHolder.preview5.setVisibility(View.GONE);
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                } else {
                    float[] trueSize = new float[]{1, 1.3f};
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.preview1.getLayoutParams().height = (int)((trueSize[0] / trueSize[1]) * width);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview3);
                    viewHolder.preview3.getLayoutParams().height = (int)((trueSize[0] / trueSize[1]) * width);
                    viewHolder.preview1.requestLayout();
                    viewHolder.preview3.requestLayout();
                    viewHolder.preview2.setVisibility(View.GONE);
                    viewHolder.preview4.setVisibility(View.GONE);
                    viewHolder.preview5.setVisibility(View.GONE);
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                }

                break;
            case 3:
//                GlideApp.with(context)
//                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width)
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//                            }
//                        });
//                int mHeight = resource.getHeight();
//                int mWidth = resource.getWidth();
                float mRatio = 0.5f;
                if (mRatio < 1) {
//                                    GridLayout.LayoutParams params =
//                                            new GridLayout.LayoutParams(viewHolder.imgPreview1.getLayoutParams());
//                                    params.rowSpec = GridLayout.spec(0, 2);
//                                    viewHolder.imgPreview1.setLayoutParams(params);
//                                    viewHolder.imgPreview1.requestLayout();
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.preview1.getLayoutParams().height = (int) (width / 2);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview3);
                    viewHolder.preview3.getLayoutParams().height = (int) (width / 2);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview4);
                    viewHolder.preview4.getLayoutParams().height = (int) (width / 2);
                    viewHolder.preview1.requestLayout();
                    viewHolder.preview3.requestLayout();
                    viewHolder.preview4.requestLayout();
                    viewHolder.preview2.setVisibility(View.GONE);
                    viewHolder.preview5.setVisibility(View.GONE);
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                } else {
//                                    GridLayout.LayoutParams params =
//                                            new GridLayout.LayoutParams(viewHolder.imgPreview1.getLayoutParams());
//                                    params.columnSpec = GridLayout.spec(0, 2);
//                                    viewHolder.gridPostPicture;
//                                    viewHolder.imgPreview1.setLayoutParams(params);
//                                    viewHolder.imgPreview1.requestLayout();
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.imgPreview1.getLayoutParams().height = width;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview2);
                    viewHolder.imgPreview2.getLayoutParams().height = width / 2;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview4);
                    viewHolder.imgPreview4.getLayoutParams().height = width / 2;
                    ((ConstraintLayout.LayoutParams) viewHolder.imgPreview4.getLayoutParams()).startToEnd = R.id.preview1;
                    ((ConstraintLayout.LayoutParams) viewHolder.imgPreview4.getLayoutParams()).topToBottom = R.id.preview2;

                    viewHolder.preview1.requestLayout();
                    viewHolder.preview2.requestLayout();
                    viewHolder.preview4.requestLayout();
                    viewHolder.preview3.setVisibility(View.GONE);
                    viewHolder.preview5.setVisibility(View.GONE);
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
//                GlideApp.with(context)
//                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4))
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                int mHeight = resource.getHeight();
//                                int mWidth = resource.getWidth();
//
//                            }
//                        });

                float mRatio1 = 0.5f;
                if (mRatio1 < 1) {
//                                    GridLayout.LayoutParams params =
//                                            new GridLayout.LayoutParams(viewHolder.imgPreview1.getLayoutParams());
//                                    params.rowSpec = GridLayout.spec(0, 2);
//                                    viewHolder.imgPreview1.setLayoutParams(params);
//                                    viewHolder.imgPreview1.requestLayout();
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.preview1.getLayoutParams().height = (int) (width * 2 / 3);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width / 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview3);
                    viewHolder.preview3.getLayoutParams().height = (int) (width / 3);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview4);
                    viewHolder.preview4.getLayoutParams().height = (int) (width / 3);
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(3).getPath().substring(4) + "&w=" + width / 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview5);
                    viewHolder.preview5.getLayoutParams().height = (int) (width / 3);

                    viewHolder.preview1.requestLayout();
                    viewHolder.preview3.requestLayout();
                    viewHolder.preview4.requestLayout();
                    viewHolder.preview5.requestLayout();
                    viewHolder.preview2.setVisibility(View.GONE);
                    if (medias.size() > 4) {
                        viewHolder.tvCount.setVisibility(View.VISIBLE);
                        viewHolder.tvCount.setText("+ " + (medias.size() - 4));
                    }
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                } else if (mRatio1 > 1) {
//                                    GridLayout.LayoutParams params =
//                                            new GridLayout.LayoutParams(viewHolder.imgPreview1.getLayoutParams());
//                                    params.columnSpec = GridLayout.spec(0, 2);
//                                    viewHolder.gridPostPicture;
//                                    viewHolder.imgPreview1.setLayoutParams(params);
//                                    viewHolder.imgPreview1.requestLayout();
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width / 2 * 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.preview1.getLayoutParams().height = width;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width / 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview3);
                    viewHolder.preview3.getLayoutParams().height = width / 3;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview4);
                    viewHolder.preview4.getLayoutParams().height = width / 3;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 3)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview5);
                    viewHolder.preview5.getLayoutParams().height = width / 3;

                    viewHolder.preview1.requestLayout();
                    viewHolder.preview3.requestLayout();
                    viewHolder.preview4.requestLayout();
                    viewHolder.preview5.requestLayout();
                    viewHolder.preview2.setVisibility(View.GONE);
                    if (medias.size() > 4) {
                        viewHolder.tvCount.setVisibility(View.VISIBLE);
                        viewHolder.tvCount.setText("+ " + (medias.size() - 4));
                    }
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                } else {
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview1);
                    viewHolder.preview1.getLayoutParams().height = width / 2;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview2);
                    viewHolder.preview2.getLayoutParams().height = width / 2;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview3);
                    viewHolder.preview3.getLayoutParams().height = width / 2;
                    GlideApp.with(context)
                            .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 2)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.imgPreview4);
                    viewHolder.preview4.getLayoutParams().height = width / 2;

                    viewHolder.preview1.requestLayout();
                    viewHolder.preview2.requestLayout();
                    viewHolder.preview3.requestLayout();
                    viewHolder.preview4.requestLayout();
                    viewHolder.preview5.setVisibility(View.GONE);
                    if (medias.size() > 4) {
                        viewHolder.tvCount.setVisibility(View.VISIBLE);
                        viewHolder.tvCount.setText("+ " + (medias.size() - 4));
                    }
                    viewHolder.imagesLayout.setVisibility(View.VISIBLE);
                }
                break;
            default:
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + medias.get(0).getPath().substring(4) + "&w=" + width / 2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.imgPreview1);
                viewHolder.preview1.getLayoutParams().height = width / 2;
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + medias.get(1).getPath().substring(4) + "&w=" + width / 2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.imgPreview2);
                viewHolder.preview2.getLayoutParams().height = width / 2;
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.imgPreview3);
                viewHolder.preview3.getLayoutParams().height = width / 3;
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.imgPreview4);
                viewHolder.preview4.getLayoutParams().height = width / 3;
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + medias.get(2).getPath().substring(4) + "&w=" + width / 3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.imgPreview5);
                viewHolder.preview5.getLayoutParams().height = width / 3;

                viewHolder.preview1.requestLayout();
                viewHolder.preview2.requestLayout();
                viewHolder.preview3.requestLayout();
                viewHolder.preview4.requestLayout();
                viewHolder.preview4.requestLayout();
                if (medias.size() > 5) {
                    viewHolder.tvCount.setVisibility(View.VISIBLE);
                    viewHolder.tvCount.setText("+ " + (medias.size() - 5));
                }
                viewHolder.imagesLayout.setVisibility(View.VISIBLE);
        }

//        PostImageFragment mFragment = PostImageFragment.newInstance(R.layout.layout_spannable_grid);
//        mFragment.setMedias(medias);
//        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
//        ft.replace(R.id.framePostImage, mFragment).commit();
    }

    public void incrementalLoad() {
        APIService mWebService = APIUtils.getWebService();
        if (isByUserId) {
            mWebService.getPostPageByUserId(MainActivity.user.getToken(), userId, pageCount++).enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    if (list != null) {
                        list.remove(0);
                        for (Post p : list) {
                            listPost.add(p);
                        }
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(context, "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mWebService.getPostPage(MainActivity.user.getToken(), pageCount++, 5, "id", "DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    if (list != null) {
                        list.remove(0);
                        for (Post p : list) {
                            listPost.add(p);
                        }
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(context, "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (listPost == null)
            listPost = new ArrayList<>();
        return listPost.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        User user;
        Context context;
        int postId;
        ImageView imgPostAvatar;
        TextView tvPostUserName;
        TextView tvTime;
        TextView tvPostCategory;
        ImageButton btnPostOption;
        TextView tvPostTitle;
        TextView tvPostContent;
        MaterialButton btnReadMore;
        ConstraintLayout imagesLayout;
        MaterialCardView preview1;
        ImageView imgPreview1;
        MaterialCardView preview2;
        ImageView imgPreview2;
        MaterialCardView preview3;
        ImageView imgPreview3;
        MaterialCardView preview4;
        ImageView imgPreview4;
        MaterialCardView preview5;
        ImageView imgPreview5;
        TextView tvCount;
        MaterialButton btnPostLike;
        MaterialButton btnPostComent;
        boolean likeFlag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPostAvatar = (ImageView) itemView.findViewById(R.id.imgPostAvatar);
            tvPostUserName = (TextView) itemView.findViewById(R.id.tvPostUserName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvPostCategory = (TextView) itemView.findViewById(R.id.tvPostCategory);
            btnPostOption = (ImageButton) itemView.findViewById(R.id.btnPostOption);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tvPostTitle);
            tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContent);
            btnReadMore = (MaterialButton) itemView.findViewById(R.id.btnReadMore);
            imagesLayout = (ConstraintLayout) itemView.findViewById(R.id.imagesLayout);
            imgPreview1 = (ImageView) itemView.findViewById(R.id.imgPreview1);
            imgPreview2 = (ImageView) itemView.findViewById(R.id.imgPreview2);
            imgPreview3 = (ImageView) itemView.findViewById(R.id.imgPreview3);
            imgPreview4 = (ImageView) itemView.findViewById(R.id.imgPreview4);
            imgPreview5 = (ImageView) itemView.findViewById(R.id.imgPreview5);
            preview1 = (MaterialCardView) itemView.findViewById(R.id.preview1);
            preview2 = (MaterialCardView) itemView.findViewById(R.id.preview2);
            preview3 = (MaterialCardView) itemView.findViewById(R.id.preview3);
            preview4 = (MaterialCardView) itemView.findViewById(R.id.preview4);
            preview5 = (MaterialCardView) itemView.findViewById(R.id.preview5);
            tvCount = (TextView) itemView.findViewById(R.id.tvCount);
            btnPostLike = (MaterialButton) itemView.findViewById(R.id.btnPostLike);
            btnPostComent = (MaterialButton) itemView.findViewById(R.id.btnPostComment);

//            frame = (FrameLayout) itemView.findViewById(R.id.framePostImage);

            imgPostAvatar.setOnClickListener((v) -> eventViewProfile());
            tvPostUserName.setOnClickListener((v) -> eventViewProfile());
            likeFlag = false;
            btnReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    context.startActivity(intent);
                }
            });
            tvPostContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    context.startActivity(intent);
                }
            });
            tvPostTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    context.startActivity(intent);
                }
            });
            btnPostComent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    boolean action = true;
                    intent.putExtra("id", postId);
                    intent.putExtra("action", action);
                    context.startActivity(intent);
                }
            });
            btnPostLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!likeFlag){
                        btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like_pressed));
                        btnPostLike.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorPrimary));
                        btnPostLike.setText("Bỏ thích");
                        likeFlag = true;
                    } else {
                        btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like));
                        btnPostLike.setTextColor(context.getResources().getColor(R.color.colorBlack));
                        btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorBlack));
                        btnPostLike.setText("Thích");
                        likeFlag = false;
                    }
                }
            });
        }

        private void eventViewProfile() {
            Intent i = new Intent(context, ProfileMemberActivity.class);
            i.putExtra("firstname", user.getFirstName());
            i.putExtra("lastname", user.getLastName());
            i.putExtra("owner", false);
            i.putExtra("id", user.getId());
            context.startActivity(i);
        }
    }
}
