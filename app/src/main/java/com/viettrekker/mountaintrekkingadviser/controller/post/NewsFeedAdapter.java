package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
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
    private int userId;
    private Context context;

    public NewsFeedAdapter(Context context) {
        this.context = context;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setListPost(List<Post> listPost) {
        this.listPost = listPost;
    }

    public void setByUserId(boolean b) {isByUserId = b;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_post_item,viewGroup,false);

        ViewHolder viewHolder = new NewsFeedAdapter.ViewHolder(view);
        viewHolder.context = context;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int postion) {
        viewHolder.user = listPost.get(postion).getUser();
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
        typPost = postType[post.getTypeId()-1];
        viewHolder.tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
        viewHolder.tvPostCategory.setText(typPost);
        viewHolder.tvPostTitle.setText(post.getName());
        viewHolder.tvPostContent.setText(post.getContent());
        viewHolder.btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like));
        viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorBlack));
        viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorBlack));
        if (post.getLiked()!=0){
            viewHolder.btnPostLike.setIcon(context.getDrawable(R.drawable.ic_like_pressed));
            viewHolder.btnPostLike.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnPostLike.setIconTint(context.getResources().getColorStateList(R.color.colorPrimary));
            viewHolder.btnPostLike.setText("Đã thích");
        }
        try {
            viewHolder.tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
        }catch (ParseException e){

        }
        viewHolder.btnReadMore.setVisibility(View.INVISIBLE);
        ViewTreeObserver vto = viewHolder.tvPostContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout l = viewHolder.tvPostContent.getLayout();
                if ( l != null){
                    int lines = l.getLineCount();
                    if ( lines > 0) {
                        if ( l.getEllipsisCount(lines-1) > 0) {
                            viewHolder.btnReadMore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
//        int lineContent = viewHolder.tvPostContent.getLayout().getLineCount();
//        if (lineContent > 0){
//            if (viewHolder.tvPostContent.getLayout().getEllipsisCount(lineContent-1) > 0){
//                viewHolder.btnReadMore.setVisibility(View.VISIBLE);
//            }
//        }
        viewHolder.tvCount.setVisibility(View.GONE);
        for (ImageView imgs: listImgView) {
            imgs.setVisibility(View.GONE);
        }
        imgSize = medias.size();
        if (imgSize > 4) {
            imgSize = 4;
            viewHolder.tvCount.setVisibility(View.VISIBLE);
            viewHolder.tvCount.setText("+" + (medias.size() - 4));
        }

        int screenWidth = LocalDisplay.getScreenWidth(context);
        for (int i = 0 ; i < imgSize ; i++) {
            listImgView.get(i).setVisibility(View.VISIBLE);
            Picasso.get().load(APIUtils.BASE_URL_API + medias.get(i).getPath().substring(4)).into(listImgView.get(i));
        }
    }

    public void incrementalLoad() {
        APIService mWebService = APIUtils.getWebService();
        if (isByUserId) {
            mWebService.getPostPageByUserId(MainActivity.user.getToken(), userId,pageCount++).enqueue(new Callback<List<Post>>() {
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
            mWebService.getPostPage(MainActivity.user.getToken(),pageCount++,5,"id").enqueue(new Callback<List<Post>>() {
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
        ImageView imgPostAvatar;
        TextView tvPostUserName;
        TextView tvTime;
        TextView tvPostCategory;
        ImageButton btnPostOption;
        TextView tvPostTitle;
        TextView tvPostContent;
        MaterialButton btnReadMore;
        GridLayout gridPostPicture;
        ImageView imgPreview1;
        ImageView imgPreview2;
        ImageView imgPreview3;
        ImageView imgPreview4;
        TextView tvCount;
        MaterialButton btnPostLike;
        MaterialButton btnPostComent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPostAvatar = (ImageView) itemView.findViewById(R.id.imgPostAvatarDetail);
            tvPostUserName = (TextView) itemView.findViewById(R.id.tvPostUserNameDetail);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvPostCategory = (TextView) itemView.findViewById(R.id.tvPostCategory);
            btnPostOption = (ImageButton) itemView.findViewById(R.id.btnPostOption);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tvPostTitleDetail);
            tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContentDetail);
            btnReadMore = (MaterialButton) itemView.findViewById(R.id.btnReadMore);
            gridPostPicture = (GridLayout) itemView.findViewById(R.id.gridPostPicture);
            imgPreview1 = (ImageView) itemView.findViewById(R.id.imgPreview1);
            imgPreview2 = (ImageView) itemView.findViewById(R.id.imgPreview2);
            imgPreview3 = (ImageView) itemView.findViewById(R.id.imgPreview3);
            imgPreview4 = (ImageView) itemView.findViewById(R.id.imgPreview4);
            tvCount = (TextView) itemView.findViewById(R.id.tvCount);
            btnPostLike = (MaterialButton) itemView.findViewById(R.id.btnPostLike);
            btnPostComent = (MaterialButton) itemView.findViewById(R.id.btnPostComment);

            imgPostAvatar.setOnClickListener((v) -> eventViewProfile());
            tvPostUserName.setOnClickListener((v) -> eventViewProfile());
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
