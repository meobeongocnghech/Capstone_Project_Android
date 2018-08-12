package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity{
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
    GridLayout gridPostPicture;
    ImageView imgPreview1;
    ImageView imgPreview2;
    ImageView imgPreview3;
    ImageView imgPreview4;
    TextView tvCount;
    MaterialButton btnPostLike;
    MaterialButton btnPostComent;
    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};
    DateTimeUtils datetime = new DateTimeUtils();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        int id = getIntent().getIntExtra("id",0);
        boolean cmtFocus = getIntent().getIntExtra("action",0);



        APIService mWebService = APIUtils.getWebService();
        mWebService.getPostByPostId(MainActivity.user.getToken(),id).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {imgPostAvatar = (ImageView) findViewById(R.id.imgPostAvatarDetail);
                tvPostUserName = (TextView)findViewById(R.id.tvPostUserNameDetail);
                tvTime = (TextView) findViewById(R.id.tvTimeDetail);
                tvPostCategory = (TextView) findViewById(R.id.tvPostCategoryDetail);
                btnPostOption = (ImageButton) findViewById(R.id.btnPostOptionDetail);
                tvPostTitle = (TextView) findViewById(R.id.tvPostTitleDetail);
                tvPostContent = (TextView) findViewById(R.id.tvPostContentDetail);
                tvCount = (TextView) findViewById(R.id.tvCount);
                btnPostLike = (MaterialButton) findViewById(R.id.btnPostLikeDetail);
                btnPostComent = (MaterialButton) findViewById(R.id.btnPostCommentDetail);

                Post post = response.body();
                tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
                String typPost = postType[post.getTypeId()-1];
                tvPostCategory.setText(typPost);
                tvPostTitle.setText(post.getName());
                tvPostContent.setText(post.getContent());
                try {
                   tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
                }catch (ParseException e){

                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

    }
}
