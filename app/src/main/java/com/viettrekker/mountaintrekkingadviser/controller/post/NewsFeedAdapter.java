package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Layout;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.controller.search.SmallPreviewImageAdapter;
import com.viettrekker.mountaintrekkingadviser.model.ImageSize;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.PostIdRemove;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
//import com.viettrekker.mountaintrekkingadviser.util.ImageUtils;
import com.viettrekker.mountaintrekkingadviser.util.ImageUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
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
    private int width;
    private Fragment fragment;
    private APIService mWebService = APIUtils.getWebService();
    private String token;

    public NewsFeedAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        token = Session.getToken(((NewsFeedFragment) fragment).getActivity());
        this.width = LocalDisplay.getScreenWidth(context);
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
        viewHolder.token = token;
        viewHolder.userId = userId;
        String typPost = "null";
        Post post = listPost.get(postion);
        DateTimeUtils datetime = new DateTimeUtils();
        typPost = postType[post.getTypeId() - 1];
        if (post.getTypeId() == 1 && (post.getDirection()) != null){
            viewHolder.separator.setBackground(context.getResources().getDrawable(R.drawable.ic_location_on));
            viewHolder.tvPostCategory.setText(post.getDirection().getPlace().getName());
        } else {
            viewHolder.separator.setBackground(context.getResources().getDrawable(R.drawable.ic_play_arrow_16dp));
            viewHolder.tvPostCategory.setText(typPost);
        }
        if (post.getUser().getGallery() != null) {
            GlideApp.with(context)
                    .load(post.getUser().getGallery().getMedia().get(0).getPath())
                    .apply(RequestOptions.circleCropTransform())
                    .fallback(R.drawable.avatar_default)
                    .into(viewHolder.imgPostAvatar);
        }

        viewHolder.tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());

        viewHolder.tvPostTitle.setText(post.getName());
        viewHolder.tvPostContent.setText(post.getContent());
        viewHolder.tvCmtCount.setText(post.getCommentsCount()==0 ? "" : post.getCommentsCount()+" bình luận");

        viewHolder.btnPostLike.setIcon(context.getResources().getDrawable(R.drawable.ic_like));
        viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorBlack));
        viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorBlack));
        viewHolder.btnPostLike.setText("Thích");
        if (post.getLikesCount() == 0) {
            viewHolder.tvlikeCount.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tvlikeCount.setVisibility(View.VISIBLE);
            viewHolder.tvlikeCount.setText(post.getLikesCount()+" thích");
        }
        if (post.getLiked() != 0){
            viewHolder.likeFlag = true;
            viewHolder.btnPostLike.setIcon(context.getResources().getDrawable(R.drawable.ic_like_pressed));
            viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorPrimary));
            viewHolder.btnPostLike.setText("Đã thích");
            viewHolder.tvlikeCount.setVisibility(View.VISIBLE);
            viewHolder.tvlikeCount.setText(post.getLikesCount()+" thích");
        }
        try {
            viewHolder.tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
        } catch (ParseException e) {

        }
        viewHolder.btnReadMore.setVisibility(View.GONE);
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
        viewHolder.btnPostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.likeFlag){
                    viewHolder.btnPostLike.setClickable(false);
                    mWebService.likePost(token, post.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            Post p = response.body();
                            viewHolder.btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like_pressed));
                            viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorPrimary));
                            viewHolder.btnPostLike.setText("Bỏ thích");
                            viewHolder.tvlikeCount.setVisibility(View.VISIBLE);
                            viewHolder.tvlikeCount.setText((p.getLikesCount() == 0 ? 1 : p.getLikesCount()) +" thích");
                            viewHolder.likeFlag = true;
                            viewHolder.btnPostLike.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(context,"Có lỗi vui lòng thử lại sau",Toast.LENGTH_LONG).show();
                            viewHolder.btnPostLike.setClickable(true);
                        }
                    });


                } else {
                    viewHolder.btnPostLike.setClickable(false);
                    mWebService.unlikePost(token, post.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            Post p = response.body();
                            viewHolder.btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like));
                            viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorBlack));
                            viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorBlack));
                            viewHolder.btnPostLike.setText("Thích");
                            if (p.getLikesCount() <= 1) {
                                viewHolder.tvlikeCount.setVisibility(View.INVISIBLE);
                            } else {
                                viewHolder.tvlikeCount.setVisibility(View.VISIBLE);
                                viewHolder.tvlikeCount.setText((p.getLikesCount())+" thích");
                            }
                            viewHolder.likeFlag = false;
                            viewHolder.btnPostLike.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(context,"Có lỗi vui lòng thử lại sau",Toast.LENGTH_LONG).show();
                            viewHolder.btnPostLike.setClickable(true);
                        }
                    });

                }

            }
        });

        if (post.getGallery() == null || post.getGallery().getMedia().size() == 0) {
            viewHolder.rcvImagePreview.setVisibility(View.GONE);
        } else {
            int size = post.getGallery().getMedia().size();
            APIService mWebService = APIUtils.getWebService();
            switch (size) {
                case 1:
                    mWebService.getImageSize(post.getGallery().getMedia().get(0).getPath()).enqueue(new Callback<ImageSize>() {
                        @Override
                        public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                            ImageSize imgSize = response.body();
                            float ratio = (float) imgSize.getHeight() / imgSize.getWidth();
                            viewHolder.singlePreview.setVisibility(View.VISIBLE);
                            GlideApp.with(context)
                                    .load(APIUtils.BASE_URL_API + post.getGallery().getMedia().get(0).getPath().substring(4) + "&w=" + width)
                                    .fallback(R.drawable.default_background)
                                    .placeholder(R.drawable.default_background)
                                    .centerCrop()
                                    .into(viewHolder.singlePreview);
                            if (ratio >= 1.5) {
                                viewHolder.singlePreview.getLayoutParams().height = (int) (width * 1.5f);
                            } else {
                                viewHolder.singlePreview.getLayoutParams().height = (int) (width * ratio);
                            }
                            viewHolder.singlePreview.requestLayout();
                        }

                        @Override
                        public void onFailure(Call<ImageSize> call, Throwable t) {
                            viewHolder.singlePreview.setVisibility(View.GONE);
                        }
                    });
                    break;
                case 2:
                    mWebService.getImageSize(post.getGallery().getMedia().get(0).getPath()).enqueue(new Callback<ImageSize>() {
                        @Override
                        public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                            float ratio1 = (float) response.body().getHeight() / response.body().getWidth();
                            mWebService.getImageSize(post.getGallery().getMedia().get(1).getPath()).enqueue(new Callback<ImageSize>() {
                                @Override
                                public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                                    float ratio2 = (float) response.body().getHeight() / response.body().getWidth();
                                    PostImageAdapter adapter = new PostImageAdapter();
                                    adapter.setContext(context);
                                    adapter.setRatio1(ratio1);
                                    adapter.setRatio2(ratio2);
                                    adapter.setMedias(post.getGallery().getMedia());
                                    adapter.setPreview(true);
                                    SpannedGridLayoutManager layoutManager;

                                    viewHolder.rcvImagePreview.setVisibility(View.VISIBLE);
                                    viewHolder.rcvImagePreview.setAdapter(adapter);
                                    float ratio = (ratio1 + ratio2) / 2;
                                    if (ratio < 1) {
                                        layoutManager = new SpannedGridLayoutManager(
                                                SpannedGridLayoutManager.Orientation.HORIZONTAL, 2);
                                    } else {
                                        layoutManager = new SpannedGridLayoutManager(
                                                SpannedGridLayoutManager.Orientation.VERTICAL, 2);
                                    }
                                    viewHolder.rcvImagePreview.getLayoutParams().height = width;
                                    viewHolder.rcvImagePreview.addItemDecoration(new SpaceItemDecorator(new Rect(2, 2, 2, 2)));
                                    viewHolder.rcvImagePreview.setLayoutManager(layoutManager);
                                    layoutManager.setItemOrderIsStable(true);
                                    viewHolder.rcvImagePreview.requestLayout();
                                }

                                @Override
                                public void onFailure(Call<ImageSize> call, Throwable t) {
                                    viewHolder.rcvImagePreview.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ImageSize> call, Throwable t) {
                            viewHolder.rcvImagePreview.setVisibility(View.GONE);
                        }
                    });
                    break;
                default:
                    mWebService.getImageSize(post.getGallery().getMedia().get(0).getPath()).enqueue(new Callback<ImageSize>() {
                        @Override
                        public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                            float ratio = (float) response.body().getHeight() / response.body().getWidth();
                            PostImageAdapter adapter = new PostImageAdapter();
                            adapter.setContext(context);
                            adapter.setRatio1(ratio);
                            adapter.setMedias(post.getGallery().getMedia());
                            adapter.setPreview(true);
                            SpannedGridLayoutManager layoutManager;

                            viewHolder.rcvImagePreview.setVisibility(View.VISIBLE);
                            viewHolder.rcvImagePreview.setAdapter(adapter);

                            if (size == 3) {
                                if (ratio < 1) {
                                    layoutManager = new SpannedGridLayoutManager(
                                            SpannedGridLayoutManager.Orientation.HORIZONTAL, 2);
                                } else {
                                    layoutManager = new SpannedGridLayoutManager(
                                            SpannedGridLayoutManager.Orientation.VERTICAL, 2);
                                }
                            } else {
                                if (ratio < 1.1 && ratio >= 1) {
                                    layoutManager = new SpannedGridLayoutManager(
                                            SpannedGridLayoutManager.Orientation.VERTICAL, 2);
                                } else {
                                    layoutManager = new SpannedGridLayoutManager(
                                            SpannedGridLayoutManager.Orientation.VERTICAL, 6);
                                }
                            }

                            viewHolder.rcvImagePreview.getLayoutParams().height = width;
                            viewHolder.rcvImagePreview.addItemDecoration(new SpaceItemDecorator(new Rect(2, 2, 2, 2)));
                            viewHolder.rcvImagePreview.setLayoutManager(layoutManager);
                            layoutManager.setItemOrderIsStable(true);
                            viewHolder.rcvImagePreview.requestLayout();
                        }

                        @Override
                        public void onFailure(Call<ImageSize> call, Throwable t) {
                            viewHolder.rcvImagePreview.setVisibility(View.GONE);
                        }
                    });
            }
        }

    }

    public void incrementalLoad() {
        APIService mWebService = APIUtils.getWebService();
        if (isByUserId) {
            mWebService.getPostPageByUserId(token, userId, pageCount++,"DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    int lastPosition = listPost.size();
                    if (list != null) {
                        for (Post p : list) {
                            listPost.add(p);
                        }
                        notifyItemRangeChanged(lastPosition, list.size());
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(context, "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            mWebService.getPostPage(token, pageCount++, 5, "id", "DESC").enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    List<Post> list = response.body();
                    if (list != null) {
                        list.remove(0);
                        int lastPosition = listPost.size();
                        for (Post p : list) {
                            listPost.add(p);
                        }
                        notifyItemRangeChanged(lastPosition, list.size());
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
        String token;
        int userId;
        ImageView imgPostAvatar;
        TextView tvPostUserName;
        TextView tvTime;
        TextView tvPostCategory;
        TextView tvPostTitle;
        TextView tvPostContent;
        MaterialButton btnReadMore;
        RecyclerView rcvImagePreview;
        MaterialButton btnPostLike;
        MaterialButton btnPostComent;
        TextView tvlikeCount;
        TextView tvCmtCount;
        TextView separator;
        boolean likeFlag;
        ImageView singlePreview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPostAvatar = (ImageView) itemView.findViewById(R.id.imgPostAvatar);
            tvPostUserName = (TextView) itemView.findViewById(R.id.tvPostUserName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvPostCategory = (TextView) itemView.findViewById(R.id.tvPostCategory);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tvPostTitle);
            tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContent);
            btnReadMore = (MaterialButton) itemView.findViewById(R.id.btnReadMore);
            rcvImagePreview = (RecyclerView) itemView.findViewById(R.id.rcvImagePreview);
            btnPostLike = (MaterialButton) itemView.findViewById(R.id.btnPostLike);
            btnPostComent = (MaterialButton) itemView.findViewById(R.id.btnPostComment);
            tvlikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            tvCmtCount = (TextView) itemView.findViewById(R.id.tvCmtCount);
            separator = (TextView) itemView.findViewById(R.id.separator);
            likeFlag = false;
            singlePreview = (ImageView) itemView.findViewById(R.id.imgSinglePreview);
//            frame = (FrameLayout) itemView.findViewById(R.id.framePostImage);

            imgPostAvatar.setOnClickListener((v) -> eventViewProfile());
            tvPostUserName.setOnClickListener((v) -> eventViewProfile());
            btnReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    intent.putExtra("token", token);
                    context.startActivity(intent);
                }
            });
            tvPostContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    intent.putExtra("token", token);
                    context.startActivity(intent);
                }
            });
            tvPostTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("id", postId);
                    intent.putExtra("token", token);
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
                    intent.putExtra("token", token);
                    context.startActivity(intent);
                }
            });
        }

        private void eventViewProfile() {
            Intent i = new Intent(context, ProfileMemberActivity.class);
            i.putExtra("firstname", user.getFirstName());
            i.putExtra("lastname", user.getLastName());
            i.putExtra("id", user.getId());
            i.putExtra("token", token);
            if (userId == user.getId()) {
                i.putExtra("owner", true);
            } else {
                i.putExtra("owner", false);
            }
            context.startActivity(i);
        }
    }
}
