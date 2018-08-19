package com.viettrekker.mountaintrekkingadviser.controller.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.bumptech.glide.request.RequestOptions;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PlaceDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.SpaceItemDecorator;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.ImageSize;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.ImageUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultAdapter extends RecyclerView.Adapter {

    private List<User> users;
    private List<Place> places;
    private List<Post> posts;

    private boolean isUser;
    private boolean isPlace;
    private boolean isPost;

    public void setUser(boolean user) {
        isUser = user;
    }

    public void setPlace(boolean place) {
        isPlace = place;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    private Context context;

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (isUser) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_item_user, viewGroup, false);
            return new SearchResultAdapter.UserVH(view);
        } else if(isPlace) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_item_place, viewGroup, false);
            return new SearchResultAdapter.PlaceVH(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_item_post, viewGroup, false);
            return new SearchResultAdapter.PostVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (isUser) {
            UserVH vh = (UserVH) viewHolder;
            vh.context = context;
            vh.user = users.get(i);

            if (users.get(i).getGallery() != null) {
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + users.get(i).getGallery().getMedia().get(0).getPath().substring(4))
                        .centerCrop()
                        .fallback(R.drawable.avatar_default)
                        .placeholder(R.drawable.avatar_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(vh.imgSearchAvatar);
            }

            vh.tvSearchUsername.setText(users.get(i).getFirstName() + " " + users.get(i).getLastName());
            if (users.get(i).getEmail() == null || users.get(i).getEmail().isEmpty()) {
                vh.tvSearchEmail.setVisibility(View.GONE);
            } else {
                vh.tvSearchEmail.setText(users.get(i).getEmail());
            }
        } else if (isPlace) {
            PlaceVH vh = (PlaceVH) viewHolder;
            vh.context = context;
            vh.place = places.get(i);

            GlideApp.with(context)
                    .load(APIUtils.BASE_URL_API + places.get(i).getGallery().getMedia().get(0).getPath().substring(4))
                    .placeholder(R.drawable.default_background)
                    .fallback(R.drawable.default_background)
                    .into(vh.imgSearchPlace);
            vh.tvSearchPlaceName.setText(places.get(i).getName());
            try {
                vh.tvSearchAddress.setText(new Geocoder(context).getFromLocation(places.get(i).getLocation().getLatitude(), places.get(i).getLocation().getLongitude(), 1).get(0).getAddressLine(0));
            } catch (Exception e) {
                vh.tvSearchAddress.setVisibility(View.GONE);
            }
        } else if (isPost) {
            PostVH vh = (PostVH) viewHolder;
            vh.context = context;
            vh.post = posts.get(i);

            if (posts.get(i).getUser().getGallery() != null) {
                GlideApp.with(context)
                        .load(APIUtils.BASE_URL_API + posts.get(i).getUser().getGallery().getMedia().get(0).getPath().substring(4))
                        .centerCrop()
                        .placeholder(R.drawable.avatar_default)
                        .fallback(R.drawable.avatar_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(vh.img);
            }

            vh.name.setText(posts.get(i).getUser().getFirstName() + " " + posts.get(i).getUser().getLastName());
            vh.category.setText(postType[posts.get(i).getTypeId() - 1]);
            vh.content.setText(posts.get(i).getContent());
            vh.title.setText(posts.get(i).getName());
            try {
                vh.time.setText(DateTimeUtils.caculatorTime(Calendar.getInstance().getTime().getTime(), posts.get(i).getUpdated_at().getTime()));
            } catch (ParseException e) {
                vh.time.setText("");
            }

            if (posts.get(i).getGallery() == null) {
                vh.images.setVisibility(View.GONE);
            } else {
                APIService mWebService = APIUtils.getWebService();
                if (posts.get(i).getGallery().getMedia().size() == 1) {
                    mWebService.getImageSize(posts.get(i).getGallery().getMedia().get(0).getPath()).enqueue(new Callback<ImageSize>() {
                        @Override
                        public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                            ImageSize size = response.body();
                            float ratio = (float)size.getHeight() / size.getWidth();
                            SmallPreviewImageAdapter adapter = new SmallPreviewImageAdapter();
                            adapter.setContext(context);
                            adapter.setMedias(posts.get(i).getGallery().getMedia());
                            adapter.setRatio1(ratio);
                            SpannedGridLayoutManager layoutManager;

                            vh.images.setAdapter(adapter);
                            if (ratio > 1.5) {
                                layoutManager = new SpannedGridLayoutManager(
                                        SpannedGridLayoutManager.Orientation.HORIZONTAL, 9);
                                vh.images.getLayoutParams().width = Math.round(LocalDisplay.dp2px(160, context) / 1.5f);
                            } else if (ratio < 1) {
                                layoutManager = new SpannedGridLayoutManager(
                                        SpannedGridLayoutManager.Orientation.VERTICAL, 9);
                                vh.images.getLayoutParams().height = Math.round(LocalDisplay.dp2px(160, context) * ratio);
                            } else {
                                layoutManager = new SpannedGridLayoutManager(
                                        SpannedGridLayoutManager.Orientation.HORIZONTAL, 9);
                                vh.images.getLayoutParams().width = Math.round(LocalDisplay.dp2px(160, context) / ratio);
                            }
                            vh.images.setLayoutManager(layoutManager);
                            layoutManager.setItemOrderIsStable(true);
                            vh.images.requestLayout();
                        }

                        @Override
                        public void onFailure(Call<ImageSize> call, Throwable t) {
                            vh.images.setVisibility(View.GONE);
                        }
                    });
                } else if (posts.get(i).getGallery().getMedia().size() >= 3) {
                    mWebService.getImageSize(posts.get(i).getGallery().getMedia().get(0).getPath()).enqueue(new Callback<ImageSize>() {
                        @Override
                        public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                            ImageSize size = response.body();
                            float ratio = (float)size.getHeight() / size.getWidth();
                            SmallPreviewImageAdapter adapter = new SmallPreviewImageAdapter();
                            adapter.setContext(context);
                            adapter.setMedias(posts.get(i).getGallery().getMedia());
                            adapter.setRatio1(ratio);
                            SpannedGridLayoutManager layoutManager;

                            vh.images.setAdapter(adapter);
                            if (ratio <= 1) {
                                layoutManager = new SpannedGridLayoutManager(
                                        SpannedGridLayoutManager.Orientation.VERTICAL, 4);
                            } else {
                                layoutManager = new SpannedGridLayoutManager(
                                        SpannedGridLayoutManager.Orientation.VERTICAL, 4);
                            }
                            vh.images.addItemDecoration(new SpaceItemDecorator(new Rect(2, 2, 2, 2)));
                            vh.images.setLayoutManager(layoutManager);
                            layoutManager.setItemOrderIsStable(true);
                            vh.images.requestLayout();
                        }

                        @Override
                        public void onFailure(Call<ImageSize> call, Throwable t) {
                            vh.images.setVisibility(View.GONE);
                        }
                    });
                } else {
                    mWebService.getImageSize(posts.get(i).getGallery().getMedia().get(0).getPath()).enqueue(new Callback<ImageSize>() {
                        @Override
                        public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                            float ratio1 = (float) response.body().getHeight() / response.body().getWidth();
                            mWebService.getImageSize(posts.get(i).getGallery().getMedia().get(1).getPath()).enqueue(new Callback<ImageSize>() {
                                @Override
                                public void onResponse(Call<ImageSize> call, Response<ImageSize> response) {
                                    float ratio2 = (float) response.body().getHeight() / response.body().getWidth();
                                    SmallPreviewImageAdapter adapter = new SmallPreviewImageAdapter();
                                    adapter.setContext(context);
                                    adapter.setMedias(posts.get(i).getGallery().getMedia());
                                    adapter.setRatio1(ratio1);
                                    adapter.setRatio2(ratio2);
                                    SpannedGridLayoutManager layoutManager;

                                    vh.images.setAdapter(adapter);
                                    if ((ratio1 + ratio2) / 2 < 1) {
                                        layoutManager = new SpannedGridLayoutManager(
                                                SpannedGridLayoutManager.Orientation.HORIZONTAL, 2);
                                    } else {
                                        layoutManager = new SpannedGridLayoutManager(
                                                SpannedGridLayoutManager.Orientation.VERTICAL, 2);
                                    }
                                    vh.images.addItemDecoration(new SpaceItemDecorator(new Rect(2, 2, 2, 2)));
                                    vh.images.setLayoutManager(layoutManager);
                                    layoutManager.setItemOrderIsStable(true);
                                    vh.images.requestLayout();
                                }

                                @Override
                                public void onFailure(Call<ImageSize> call, Throwable t) {
                                    vh.images.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ImageSize> call, Throwable t) {
                            vh.images.setVisibility(View.GONE);
                        }
                    });
                }
            }

            if (posts.get(i).getCommentsCount() == 0) {
                vh.like.setVisibility(View.GONE);
            } else {
                vh.like.setText(posts.get(i).getCommentsCount() + " thích");
            }


            if (posts.get(i).getLikesCount() == 0) {
                vh.comment.setVisibility(View.GONE);
            } else {
                vh.comment.setText(posts.get(i).getLikesCount() + " bình luận");
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isUser) return users.size();
        if (isPlace) return places.size();
        if (isPost) return posts.size();
        return 0;
    }

    static class PostVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        Post post;
        Context context;
        ImageView img;
        TextView name;
        TextView time;
        TextView category;
        TextView title;
        TextView content;
        RecyclerView images;
        TextView like;
        TextView comment;
        public PostVH(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgSearchPostAvatar);
            name = (TextView) itemView.findViewById(R.id.tvSearchPostUserName);
            time = (TextView) itemView.findViewById(R.id.tvSeachTime);
            category = (TextView) itemView.findViewById(R.id.tvSearchPostCategory);
            title = (TextView) itemView.findViewById(R.id.tvSearchPostTitle);
            content = (TextView) itemView.findViewById(R.id.tvSearchPostContent);
            images = (RecyclerView) itemView.findViewById(R.id.rcvSmallImagePreview);
            like = (TextView) itemView.findViewById(R.id.tvSearchLikeCount);
            comment = (TextView) itemView.findViewById(R.id.tvSearchCmtCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, PostDetailActivity.class);
            i.putExtra("id", post.getId());
            i.putExtra("token", MainActivity.user.getToken());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    static class PlaceVH extends RecyclerView.ViewHolder implements OnClickListener {
        Place place;
        Context context;
        ImageView imgSearchPlace;
        TextView tvSearchPlaceName;
        TextView tvSearchAddress;
        public PlaceVH(@NonNull View itemView) {
            super(itemView);
            imgSearchPlace = (ImageView) itemView.findViewById(R.id.imgSearchPlace);
            tvSearchPlaceName = (TextView) itemView.findViewById(R.id.tvSearchPlaceName);
            tvSearchAddress = (TextView) itemView.findViewById(R.id.tvSearchAddress);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, PlaceDetailActivity.class);
            i.putExtra("id", place.getId());
            i.putExtra("token", MainActivity.user.getToken());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    static class UserVH extends RecyclerView.ViewHolder implements OnClickListener {
        User user;
        Context context;
        ImageView imgSearchAvatar;
        TextView tvSearchUsername;
        TextView tvSearchEmail;
        public UserVH(@NonNull View itemView) {
            super(itemView);
            imgSearchAvatar = (ImageView) itemView.findViewById(R.id.imgSearchAvatar);
            tvSearchUsername = (TextView) itemView.findViewById(R.id.tvSearchUsername);
            tvSearchEmail = (TextView) itemView.findViewById(R.id.tvSearchEmail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, ProfileMemberActivity.class);
            i.putExtra("id", user.getId());
            i.putExtra("owner", user.getId() == MainActivity.user.getId());
            i.putExtra("token", MainActivity.user.getToken());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
