package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
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
    Button btnSendCmtDetail;
    boolean likeFlag;
    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};
    DateTimeUtils datetime = new DateTimeUtils();

    public PostDetailActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        int id = getIntent().getIntExtra("id",0);
        cmtFocus = getIntent().getBooleanExtra("action",false);
        tvPostUserName = (TextView)findViewById(R.id.tvPostUserNameDetail);
        tvTime = (TextView) findViewById(R.id.tvTimeDetail);
        tvPostCategory = (TextView) findViewById(R.id.tvPostCategoryDetail);
        btnPostOption = (ImageButton) findViewById(R.id.btnPostOptionDetail);
        tvPostTitle = (TextView) findViewById(R.id.tvPostTitleDetail);
        tvPostContent = (TextView) findViewById(R.id.tvPostContentDetail);
        tvCount = (TextView) findViewById(R.id.tvCount);
        btnPostLike = (MaterialButton) findViewById(R.id.btnPostLikeDetail);
        btnPostComent = (MaterialButton) findViewById(R.id.btnPostCommentDetail);
        edtComment = (EditText) findViewById(R.id.edtInputComment);
        btnSendCmtDetail = (Button) findViewById(R.id.btnSendCmtDetail);
        likeFlag = false;
        btnPostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!likeFlag){
                   btnPostLike.setIcon(getDrawable(R.drawable.ic_like_pressed));
                   btnPostLike.setTextColor(getResources().getColor(R.color.colorPrimary));
                   btnPostLike.setIconTint(getResources().getColorStateList(R.color.colorPrimary));
                   btnPostLike.setText("Bỏ thích");
                   likeFlag = true;
                } else {
                    btnPostLike.setIcon(getDrawable(R.drawable.ic_like));
                    btnPostLike.setTextColor(getResources().getColor(R.color.colorBlack));
                    btnPostLike.setIconTint(getResources().getColorStateList(R.color.colorBlack));
                    btnPostLike.setText("Thích");
                    likeFlag = false;
                }
            }
        });



        btnPostComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        RecyclerView rcvCmtItem = (RecyclerView) findViewById(R.id.rcvCmtListDetail);
        rcvCmtItem.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentListAdapter cmtListAdapter = new CommentListAdapter(PostDetailActivity.this);


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
                //tvCount = (TextView) findViewById(R.id.tvCount);
                btnPostLike = (MaterialButton) findViewById(R.id.btnPostLikeDetail);
                btnPostComent = (MaterialButton) findViewById(R.id.btnPostCommentDetail);

                Post post = response.body();
                User user = post.getUser();
                btnPostOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), btnPostOption);
                        popupMenu.getMenuInflater().inflate(R.menu.action_popup_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if (menuItem.getTitle().equals("Báo cáo")){
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetailActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                    alertDialogBuilder.setTitle("Báo cáo bài viết");
                                    EditText edtRP = new EditText(PostDetailActivity.this);
                                    edtRP.setInputType(InputType.TYPE_CLASS_TEXT);
                                    alertDialogBuilder.setView(edtRP);
                                    alertDialogBuilder.setMessage("Vui lòng cho chúng tôi biết lý do báo cáo bài viết này.")
                                            .setCancelable(false)
                                            .setPositiveButton("Gửi",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    if (!edtRP.getText().toString().trim().equalsIgnoreCase("")){
                                                        Toast.makeText(getApplicationContext(),"Đã báo cáo bài viết.",Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),"Vui lòng điền lý do.",Toast.LENGTH_LONG).show();
                                                        edtRP.requestFocus();
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
                                if (MainActivity.user.getId() == post.getUser().getId()){
                                    if (menuItem.getTitle().equals("Sửa")){

                                    }
                                } else {
                                    if (menuItem.getTitle().equals("Sửa") || menuItem.getTitle().equals("Xóa") ){
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext(),R.style.Theme_AppCompat_DayNight_Dialog_Alert);
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
                    }


                });
                tvPostUserName.setText(post.getUser().getFirstName() + " " + post.getUser().getLastName());
                String typPost = postType[post.getTypeId()-1];
                tvPostCategory.setText(typPost);
                tvPostTitle.setText(post.getName());
                tvPostContent.setText(post.getContent());
                cmtListAdapter.setList(post.getComments());
                cmtListAdapter.notifyDataSetChanged();
                rcvCmtItem.setAdapter(cmtListAdapter);
                btnPostLike.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_like));
                btnPostLike.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
                btnPostLike.setIconTint(getApplicationContext().getResources().getColorStateList(R.color.colorBlack));
                btnPostLike.setText("Thích");
                if (post.getLiked() != 0){
                    likeFlag = true;
                    btnPostLike.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_like_pressed));
                    btnPostLike.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    btnPostLike.setIconTint(getApplicationContext().getResources().getColorStateList(R.color.colorPrimary));
                    btnPostLike.setText("Đã thích");
                }
                try {
                   tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
                }catch (ParseException e){

                }
                if (cmtFocus){
                    edtComment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
                }
                imgPostAvatar.setOnClickListener((v) -> eventViewProfile(user));
                tvPostUserName.setOnClickListener((v) -> eventViewProfile(user));
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                onBackPressed();
            }
        });



    }

    private void eventViewProfile(User user) {
        Intent i = new Intent(this.getApplicationContext(), ProfileMemberActivity.class);
        i.putExtra("firstname", user.getFirstName());
        i.putExtra("lastname", user.getLastName());
        i.putExtra("owner", false);
        i.putExtra("id", user.getId());
        getApplicationContext().startActivity(i);
    }
}
