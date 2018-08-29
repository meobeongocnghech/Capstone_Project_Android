package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Layout;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.ImageSize;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.PostIdRemove;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    List<Post> listPost;
    int pageCount = 2;
    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};
    private boolean isByUserId = false;
    private boolean isByPlaceId = false;
    private boolean isByPlanId = false;
    private int userId;
    private int placeId;
    private int directionId;
    private Context context;
    private int width;
    private Fragment fragment;
    private APIService mWebService = APIUtils.getWebService();
    private String token;
    private boolean continueLoad = true;

    public NewsFeedAdapter(Context context, Fragment fragment, String token) {
        this.context = context;
        this.token = token;
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

    public void setByPlaceId(boolean byPlaceId) {
        isByPlaceId = byPlaceId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public void setByPlanId(boolean byPlanId) {
        isByPlanId = byPlanId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
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
        viewHolder.post = listPost.get(postion);
        viewHolder.isByUserId = isByUserId;
        viewHolder.isByPlanId = isByPlanId;
        String typPost = "null";
        Post post = listPost.get(postion);
        DateTimeUtils datetime = new DateTimeUtils();
        typPost = postType[post.getTypeId() - 1];
        if (isByPlanId) {
            viewHolder.separator.setVisibility(View.GONE);
            viewHolder.tvPostCategory.setVisibility(View.GONE);
        }else if (post.getTypeId() == 1 && (post.getDirection()) != null){
            viewHolder.separator.setBackground(context.getResources().getDrawable(R.drawable.ic_location_on));
            viewHolder.tvPostCategory.setText(post.getDirection().getPlace().getName());
        } else {
            viewHolder.separator.setBackground(context.getResources().getDrawable(R.drawable.ic_play_arrow_16dp));
            viewHolder.tvPostCategory.setText(typPost);
        }
        if (post.getUser().getAvatar() != null && !post.getUser().getAvatar().getPath().isEmpty()) {
            GlideApp.with(context)
                    .load(APIUtils.BASE_URL_API + post.getUser().getAvatar().getPath().substring(4))
                    .apply(RequestOptions.circleCropTransform())
                    .fallback(R.drawable.avatar_default)
                    .into(viewHolder.imgPostAvatar);
        }

        viewHolder.tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
        if (post.getUser().getState() == 1) {
            viewHolder.tvPostUserName.setPaintFlags(viewHolder.tvPostUserName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        viewHolder.tvPostTitle.setText(post.getName());
        viewHolder.tvPostContent.setAutoLinkMask(Linkify.WEB_URLS);
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
                            if (response.code() == 200){
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
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
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
                                    adapter.setPostId(listPost.get(postion).getId());
                                    adapter.setToken(token);
                                    adapter.setByUserId(isByUserId);
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
                            adapter.setPostId(listPost.get(postion).getId());
                            adapter.setToken(token);
                            adapter.setByUserId(isByUserId);
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
        if (continueLoad) {
            APIService mWebService = APIUtils.getWebService();
            if (isByUserId) {
                mWebService.getPostPageByUserId(token, userId, pageCount++,"DESC").enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        List<Post> list = response.body();
                        int lastPosition = listPost.size();
                        if (list != null && !list.isEmpty()) {
                            for (Post p : list) {
                                if (p.getState() != 1) {
                                    listPost.add(p);
                                }
                            }
                            notifyItemRangeChanged(lastPosition, list.size());
                        } else {
                            continueLoad = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Toast.makeText(context, "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (isByPlaceId) {
                mWebService.getPostPageByPlaceId(token, pageCount++, 5, placeId, "DESC").enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        List<Post> list = response.body();
                        if (list != null) {
                            list.remove(0);
                            if (list.isEmpty()) {
                                continueLoad = false;
                            } else {
                                int lastPosition = listPost.size();
                                for (Post p : list) {
                                    if (p.getState() != 1) {
                                        listPost.add(p);
                                    }
                                }
                                notifyItemRangeChanged(lastPosition, list.size());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Toast.makeText(context, "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                    }
                });
            } else if(isByPlanId) {
                mWebService.getPostPageByPlanId(token, pageCount++, 5, directionId, "DESC").enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        List<Post> list = response.body();
                        if (list != null) {
                            list.remove(0);
                            if (list.isEmpty()) {
                                continueLoad = false;
                            } else {
                                int lastPosition = listPost.size();
                                for (Post p : list) {
                                    if (p.getState() != 1) {
                                        listPost.add(p);
                                    }
                                }
                                notifyItemRangeChanged(lastPosition, list.size());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {

                    }
                });
            } else {
                mWebService.getPostPage(token, pageCount++, 5, "id", "DESC").enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        List<Post> list = response.body();
                        if (list != null) {
                            list.remove(0);
                            if (list.isEmpty()) {
                                continueLoad = false;
                            } else {
                                int lastPosition = listPost.size();
                                for (Post p : list) {
                                    if (p.getState() != 1) {
                                        listPost.add(p);
                                    }
                                }
                                notifyItemRangeChanged(lastPosition, list.size());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        Toast.makeText(context, "Xảy ra lỗi!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
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
        Post post;
        boolean isByUserId;
        boolean isByPlanId;
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
        ImageButton btnPostOption;
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
            btnPostOption = (ImageButton) itemView.findViewById(R.id.btnPostOption);
            likeFlag = false;
            singlePreview = (ImageView) itemView.findViewById(R.id.imgSinglePreview);
//            frame = (FrameLayout) itemView.findViewById(R.id.framePostImage);

            imgPostAvatar.setOnClickListener((v) -> eventViewProfile());
            tvPostUserName.setOnClickListener((v) -> eventViewProfile());
            btnReadMore.setOnClickListener((v) -> eventViewPostDetail());
            tvPostContent.setOnClickListener((v) -> eventViewPostDetail());
            tvPostTitle.setOnClickListener((v) -> eventViewPostDetail());
            singlePreview.setOnClickListener((v) -> eventViewPostDetail());
            btnPostComent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    boolean action = true;
                    intent.putExtra("id", postId);
                    intent.putExtra("action", action);
                    intent.putExtra("token", token);
                    intent.putExtra("isByUserId", isByUserId);
                    context.startActivity(intent);
                }
            });

            btnPostOption.setOnClickListener((v) -> {
                APIService mWebService = APIUtils.getWebService();
                PopupMenu popupMenu = new PopupMenu(context, btnPostOption);
                popupMenu.getMenuInflater().inflate(R.menu.action_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Báo cáo")){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                            alertDialogBuilder.setTitle("Báo cáo bài viết");
                            EditText edtRP = new EditText(context);
                            edtRP.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertDialogBuilder.setView(edtRP);
                            alertDialogBuilder.setMessage("Báo cáo của bạn đã được ghi nhận")
                                    .setCancelable(false)
                                    .setPositiveButton("Gửi",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            if (!edtRP.getText().toString().matches("")){
                                                mWebService.reportPost(Session.getToken(context), postId,edtRP.getText().toString()).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        dialog.cancel();
                                                        Toast.makeText(context,"Đã báo cáo bài viết.",Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Post> call, Throwable t) {

                                                        Toast.makeText(context,"Báo cáo không thành công, thử lại sau.",Toast.LENGTH_LONG).show();
                                                        dialog.cancel();
                                                    }
                                                });
                                                dialog.cancel();
                                            } else {
                                                Toast.makeText(context,"Vui lòng điền lý do.",Toast.LENGTH_LONG).show();
//                                                        edtRP.requestFocus();
                                            }
                                        }
                                    }).setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else
                        if (userId == user.getId()){
                            if (menuItem.getTitle().equals("Sửa")){
                                Intent intent = new Intent(context, PostAddActivity.class);
                                intent.putExtra("id", postId);
                                intent.putExtra("title", post.getName());
                                intent.putExtra("content", post.getContent());
                                context.startActivity(intent);
                            } else {
                                //Remove post pending
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn có muốn xóa bài viết này?")
                                        .setCancelable(false)
                                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                PostIdRemove pi = new PostIdRemove();
                                                List<Integer> li = new ArrayList<>();
                                                li.add(post.getId());
                                                pi.setId(li);
                                                mWebService.removePost(Session.getToken(context),pi).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        Toast.makeText(context, "Đã xóa",Toast.LENGTH_SHORT).show();
                                                        context.startActivity(new Intent(context,MainActivity.class));
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Post> call, Throwable t) {
                                                        Toast.makeText(context, "Có lỗi xảy ra, thử lại sau.",Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        })
                                        .setNegativeButton("Quay lại",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } else {
                            if (menuItem.getTitle().equals("Sửa") || menuItem.getTitle().equals("Xóa") ){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn chỉ có thể thao tác trên bài viết của mình.")
                                        .setCancelable(false)
                                        .setNegativeButton("Đóng",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        }

                        return true;
                    }
                });
                popupMenu.show();
            });
        }

        private void eventViewProfile() {
            if (!isByUserId) {
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

        private void eventViewPostDetail() {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("id", postId);
            intent.putExtra("token", token);
            intent.putExtra("isByUserId", isByUserId);
            intent.putExtra("isByPlanId", isByPlanId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
