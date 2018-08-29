package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Comment;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.PostIdRemove;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.arasthel.spannedgridlayoutmanager.sample.GridItemAdapter;

public class PostDetailActivity extends AppCompatActivity {
    Boolean cmtFocus;
    ImageView imgPostAvatar;
    TextView tvPostUserName;
    TextView tvTime;
    TextView tvPostCategory;
    ImageButton btnPostOption;
    TextView tvPostTitle;
    TextView tvPostContent;
    TextView tvCount;
    EditText edtComment;
    MaterialButton btnPostLike;
    MaterialButton btnPostComent;
    TextView tvCmtCount;
    TextView tvLikeCount;
    TextView separator;
    int id;
    boolean isByUserId;
    MaterialButton btnSendCmtDetail;
    RecyclerView rcvPostImage;
    RecyclerView rcvCmtItem;
    APIService mWebService;
    ImageView ownerAvatar;
    Post post;
    User user;
    boolean likeFlag;
    boolean updateCmt = false;
    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};

    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";

    public PostDetailActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.postDetailToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWebService = APIUtils.getWebService();
        InputMethodManager imm = (InputMethodManager) PostDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            return;
        }

        isByUserId = getIntent().getBooleanExtra("isByUserId", false);

        int id = getIntent().getIntExtra("id", 0);
        rcvPostImage = (RecyclerView) findViewById(R.id.rcvPostImage);
        cmtFocus = getIntent().getBooleanExtra("action", false);
        tvPostUserName = (TextView) findViewById(R.id.tvPostUserNameDetail);
        tvTime = (TextView) findViewById(R.id.tvTimeDetail);
        tvPostCategory = (TextView) findViewById(R.id.tvPostCategoryDetail);
        btnPostOption = (ImageButton) findViewById(R.id.btnPostOptionDetail);
        tvPostTitle = (TextView) findViewById(R.id.tvPostTitleDetail);
        tvPostContent = (TextView) findViewById(R.id.tvPostContentDetail);
//        tvCount = (TextView) findViewById(R.id.tvCount);
        btnPostLike = (MaterialButton) findViewById(R.id.btnPostLikeDetail);
        btnPostComent = (MaterialButton) findViewById(R.id.btnPostCommentDetail);
        edtComment = (EditText) findViewById(R.id.edtInputComment);
        btnSendCmtDetail = (MaterialButton) findViewById(R.id.btnSendCmtDetail);
        tvCmtCount = (TextView) findViewById(R.id.tvCmtCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        separator = (TextView) findViewById(R.id.separator);
        ownerAvatar = (ImageView) findViewById(R.id.ownerAvatar);
        imgPostAvatar = (ImageView) findViewById(R.id.imgPostAvatarDetail);
        tvPostUserName = (TextView) findViewById(R.id.tvPostUserNameDetail);
        tvTime = (TextView) findViewById(R.id.tvTimeDetail);
        tvPostCategory = (TextView) findViewById(R.id.tvPostCategoryDetail);
        btnPostOption = (ImageButton) findViewById(R.id.btnPostOptionDetail);
        tvPostTitle = (TextView) findViewById(R.id.tvPostTitleDetail);
        tvPostContent = (TextView) findViewById(R.id.tvPostContentDetail);
        btnPostLike = (MaterialButton) findViewById(R.id.btnPostLikeDetail);
        btnPostComent = (MaterialButton) findViewById(R.id.btnPostCommentDetail);
        likeFlag = false;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnPostComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtComment.requestFocus();
                imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        rcvCmtItem = (RecyclerView) findViewById(R.id.rcvCmtListDetail);
        rcvCmtItem.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
        CommentListAdapter cmtListAdapter = new CommentListAdapter(PostDetailActivity.this);

        btnSendCmtDetail.setClickable(false);
        btnSendCmtDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateCmt) {
                    updateCmt = false;
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mWebService.updateComment(Session.getToken(PostDetailActivity.this), id, cmtListAdapter.idCmtEdit,
                            edtComment.getText().toString()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (response.body() != null) {
                                cmtListAdapter.setList(response.body().getComments());
                                cmtListAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PostDetailActivity.this, "Có lỗi xảy ra, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                            edtComment.setText("");
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {

                        }
                    });
                } else if (!edtComment.getText().toString().isEmpty()) {
                    if (edtComment.getHint().toString().contains("Bình luận bài viết")) {
                        mWebService.commentPost(Session.getToken(PostDetailActivity.this), id, edtComment.getText().toString()).enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(Call<Post> call, Response<Post> response) {
                                edtComment.setText("");
                                edtComment.clearFocus();
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                List<Comment> list = response.body().getComments();
                                cmtListAdapter.getList().add(list.get(list.size() - 1));
                                cmtListAdapter.notifyDataSetChanged();
                                tvCmtCount.setText(response.body().getCommentsCount() + " bình luận");
                            }

                            @Override
                            public void onFailure(Call<Post> call, Throwable t) {

                            }
                        });
                    } else {
                        mWebService.commentOnComment(Session.getToken(PostDetailActivity.this), id, cmtListAdapter.targetCmtid, edtComment.getText().toString()).enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(Call<Post> call, Response<Post> response) {
                                edtComment.setText("");
                                edtComment.clearFocus();
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                List<Comment> list = response.body().getComments();
                                cmtListAdapter.setList(response.body().getComments());

                                cmtListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<Post> call, Throwable t) {

                            }
                        });
                    }
                }
            }
        });

        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    btnSendCmtDetail.setClickable(false);
                    ColorStateList stateList = new ColorStateList(
                            new int[][]{{}}, new int[]{Color.parseColor("#bcbcbc")});
                    btnSendCmtDetail.setIconTint(stateList);
                } else {
                    btnSendCmtDetail.setClickable(true);
                    ColorStateList stateList = new ColorStateList(
                            new int[][]{{}}, new int[]{Color.parseColor("#00c853")});
                    btnSendCmtDetail.setIconTint(stateList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnPostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!likeFlag) {
                    btnPostLike.setClickable(false);
                    mWebService.likePost(Session.getToken(PostDetailActivity.this), post.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            btnPostLike.setIcon(getDrawable(R.drawable.ic_like_pressed));
                            btnPostLike.setTextColor(getResources().getColor(R.color.colorPrimary));
                            btnPostLike.setIconTint(getResources().getColorStateList(R.color.colorPrimary));
                            btnPostLike.setText("Bỏ thích");
                            tvLikeCount.setText(response.body().getLikesCount() == 0 ? "" : response.body().getLikesCount() + " thích");
                            likeFlag = true;
                            btnPostLike.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            btnPostLike.setClickable(true);
                        }
                    });
                } else {
                    btnPostLike.setClickable(false);
                    mWebService.unlikePost(Session.getToken(PostDetailActivity.this), post.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            tvLikeCount.setText(response.body().getLikesCount() == 0 ? "" : response.body().getLikesCount() + " thích");
                            btnPostLike.setIcon(getDrawable(R.drawable.ic_like));
                            btnPostLike.setTextColor(getResources().getColor(R.color.colorBlack));
                            btnPostLike.setIconTint(getResources().getColorStateList(R.color.colorBlack));
                            btnPostLike.setText("Thích");
                            likeFlag = false;
                            btnPostLike.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            btnPostLike.setClickable(true);
                        }
                    });
                }
            }
        });

        btnPostOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(PostDetailActivity.this, btnPostOption);
                popupMenu.getMenuInflater().inflate(R.menu.action_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Báo cáo")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetailActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                            alertDialogBuilder.setTitle("Báo cáo bài viết");
                            EditText edtRP = new EditText(PostDetailActivity.this);
                            edtRP.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertDialogBuilder.setView(edtRP);
                            alertDialogBuilder.setMessage("Báo cáo của bạn đã được ghi nhận")
                                    .setCancelable(false)
                                    .setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (!edtRP.getText().toString().matches("")) {
                                                mWebService.reportPost(Session.getToken(PostDetailActivity.this), post.getId(), edtRP.getText().toString()).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        dialog.cancel();
                                                        Toast.makeText(PostDetailActivity.this, "Đã báo cáo bài viết.", Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Post> call, Throwable t) {

                                                        Toast.makeText(PostDetailActivity.this, "Báo cáo không thành công, thử lại sau.", Toast.LENGTH_LONG).show();
                                                        dialog.cancel();
                                                    }
                                                });
                                                dialog.cancel();
                                            } else {
                                                Toast.makeText(PostDetailActivity.this, "Vui lòng điền lý do.", Toast.LENGTH_LONG).show();
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
                        } else if (Session.getUserId(PostDetailActivity.this) == post.getUser().getId()) {
                            if (menuItem.getTitle().equals("Sửa")) {
                                Intent intent = new Intent(PostDetailActivity.this, PostAddActivity.class);
                                intent.putExtra("id", post.getId());
                                intent.putExtra("title", post.getName());
                                intent.putExtra("content", post.getContent());
                                startActivity(intent);
                            } else {
                                //Remove post pending
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetailActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
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
                                                mWebService.removePost(Session.getToken(PostDetailActivity.this), pi).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        Toast.makeText(PostDetailActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Post> call, Throwable t) {
                                                        Toast.makeText(PostDetailActivity.this, "Có lỗi xảy ra, thử lại sau.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        })
                                        .setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } else {
                            if (menuItem.getTitle().equals("Sửa") || menuItem.getTitle().equals("Xóa")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetailActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn chỉ có thể thao tác trên bài viết của mình.")
                                        .setCancelable(false)
                                        .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
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
            }
        });

        String token = Session.getToken(this);
        mWebService.getPostByPostId(token, id).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    post = response.body();
                    user = post.getUser();
                    tvCmtCount.setText(post.getCommentsCount() <= 0 ? "" : post.getCommentsCount() + " bình luận");


                    tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
                    String typPost = postType[post.getTypeId() - 1];
                    boolean isByPlanId = getIntent().getBooleanExtra("isByPlanId", false);
                    if (isByPlanId) {
                        separator.setVisibility(View.GONE);
                        tvPostCategory.setVisibility(View.GONE);
                    } else if (post.getTypeId() == 1 && post.getDirection() != null) {
                        separator.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_location_on));
                        tvPostCategory.setText(post.getDirection().getPlace().getName());
                    } else {
                        tvPostCategory.setText(typPost);
                        separator.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_play_arrow_16dp));
                    }
                    tvPostTitle.setText(post.getName());
                    tvPostContent.setAutoLinkMask(Linkify.WEB_URLS);
                    tvPostContent.setText(post.getContent());
                    cmtListAdapter.setList(post.getComments());
                    cmtListAdapter.notifyDataSetChanged();
                    rcvCmtItem.setNestedScrollingEnabled(false);
                    rcvCmtItem.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    rcvCmtItem.setAdapter(cmtListAdapter);

                    int cmtPosition = getIntent().getIntExtra("cmtPosition", -1);
                    if (cmtPosition != -1) {
                        int position = cmtListAdapter.findCmtPosition(cmtPosition);
                        if (position <= cmtListAdapter.getItemCount()) {
                            rcvCmtItem.smoothScrollToPosition(position);
                        }
                    }
                    rcvCmtItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int position = cmtListAdapter.findCmtPosition(cmtPosition);
                            rcvCmtItem.smoothScrollToPosition(position);
                        }
                    });

                    btnPostLike.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_like));
                    btnPostLike.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
                    btnPostLike.setIconTint(getApplicationContext().getResources().getColorStateList(R.color.colorBlack));
                    btnPostLike.setText("Thích");
                    tvLikeCount.setText(post.getLikesCount() == 0 ? "" : post.getLikesCount() + " thích");
                    if (post.getLiked() != 0) {
                        likeFlag = true;
                        btnPostLike.setIcon(PostDetailActivity.this.getResources().getDrawable(R.drawable.ic_like_pressed));
                        btnPostLike.setTextColor(PostDetailActivity.this.getResources().getColor(R.color.colorPrimary));
                        btnPostLike.setIconTint(PostDetailActivity.this.getResources().getColorStateList(R.color.colorPrimary));
                        btnPostLike.setText("Đã thích");
                    }
                    try {
                        tvTime.setText(DateTimeUtils.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
                    } catch (ParseException e) {

                    }
                    if (cmtFocus) {
                        edtComment.requestFocus();
                        imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
                    }
                    imgPostAvatar.setOnClickListener((v) -> eventViewProfile(user));
                    tvPostUserName.setOnClickListener((v) -> eventViewProfile(user));

                    if (!post.getUser().getAvatar().getPath().isEmpty()) {
                        GlideApp.with(PostDetailActivity.this)
                                .load(APIUtils.BASE_URL_API + post.getUser().getAvatar().getPath().substring(4))
                                .placeholder(R.drawable.avatar_default)
                                .fallback(R.drawable.avatar_default)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgPostAvatar);
                    }

                    if (!Session.getAvatarPath(PostDetailActivity.this).isEmpty()) {
                        GlideApp.with(PostDetailActivity.this)
                                .load(APIUtils.BASE_URL_API + Session.getAvatarPath(PostDetailActivity.this).substring(4))
                                .placeholder(R.drawable.avatar_default)
                                .fallback(R.drawable.avatar_default)
                                .apply(RequestOptions.circleCropTransform())
                                .into(ownerAvatar);
                    }

                    ownerAvatar.setOnClickListener((v) -> eventViewProfile(Session.getUser(PostDetailActivity.this)));

                    if (post.getGallery().getMedia().size() == 0) {
                        rcvPostImage.setVisibility(View.GONE);
                    } else {
                        HashMap<Integer, float[]> map = new HashMap<>();
                        int mediaIdx = 0;
                        for (int i = 0; i < post.getGallery().getMedia().size(); i++) {
                            float ratio1 = (float) post.getGallery().getMedia().get(mediaIdx).getHeight() / post.getGallery().getMedia().get(mediaIdx).getWidth();
                            mediaIdx++;
                            if (ratio1 >= 1.2) {
                                float ratio2 = (float) post.getGallery().getMedia().get(mediaIdx).getHeight() / post.getGallery().getMedia().get(mediaIdx).getWidth();
                                mediaIdx++;
                                if (ratio2 >= 1.2) {
                                    mediaIdx++;
                                    float ratio3 = (float) post.getGallery().getMedia().get(mediaIdx).getHeight() / post.getGallery().getMedia().get(mediaIdx).getWidth();
                                    if (ratio3 < 1.2) {
                                        map.put(i, new float[]{ratio1, ratio2, ratio3});
                                    } else {
                                        map.put(i, new float[]{ratio1, ratio2});
                                    }
                                } else {
                                    map.put(i, new float[]{ratio1, ratio2});
                                }
                            } else {
                                map.put(i, new float[]{ratio1});
                            }
                        }

                        DetailImageAdapter imageAdapter = new DetailImageAdapter();
                        imageAdapter.setMap(map);
                        imageAdapter.setWidth(LocalDisplay.getScreenWidth(PostDetailActivity.this));
                        imageAdapter.setMedias(post.getGallery().getMedia());
                        rcvPostImage.setAdapter(imageAdapter);
                        rcvPostImage.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
                    }
                } else {
                    onBackPressed();
                    Toast.makeText(PostDetailActivity.this, "Bài viết không còn tồn tại", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                onBackPressed();
            }
        });
    }

    private void eventViewProfile(User user) {
        if (isByUserId) {
            onBackPressed();
        } else {
            Intent i = new Intent(this, ProfileMemberActivity.class);
            i.putExtra("firstname", user.getFirstName());
            i.putExtra("lastname", user.getLastName());
            if (Session.getUserId(PostDetailActivity.this) == user.getId()) {
                i.putExtra("owner", true);
            } else {
                i.putExtra("owner", false);
            }
            i.putExtra("id", user.getId());
            i.putExtra("token", Session.getToken(PostDetailActivity.this));
            startActivity(i);
        }
    }

    private void scrollToPosition() {
        rcvPostImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                rcvPostImage.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = rcvPostImage.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(PostDetailActivity.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    rcvPostImage.post(() -> layoutManager.scrollToPosition(PostDetailActivity.currentPosition));
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void setUpdateCmt(boolean updateCmt) {
        this.updateCmt = updateCmt;
    }
}
