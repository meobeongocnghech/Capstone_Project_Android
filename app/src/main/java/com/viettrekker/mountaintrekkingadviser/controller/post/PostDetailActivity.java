package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager;
//import com.arasthel.spannedgridlayoutmanager.sample.GridItemAdapter;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
    RecyclerView rcvPostImage;
    RecyclerView rcvCmtItem;
    CommentListAdapter cmtListAdapter;
    boolean likeFlag;
    private String[] postType = {"Bài viết đánh giá", "Bài viết hướng dẫn", "Bài viết chia sẻ", "Bài viết khác"};

    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";

    public PostDetailActivity() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            return;
        }

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
        rcvPostImage = (RecyclerView) findViewById(R.id.rcvPostImage);
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

        rcvCmtItem = (RecyclerView) findViewById(R.id.rcvCmtListDetail);
        cmtListAdapter = new CommentListAdapter(PostDetailActivity.this);


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
                if (post.getLiked() != 0){
                    likeFlag = true;
                    btnPostLike.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_like_pressed));
                    btnPostLike.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    btnPostLike.setIconTint(getApplicationContext().getResources().getColorStateList(R.color.colorPrimary));
                    btnPostLike.setText("Đã thích");
                }
                try {
                   tvTime.setText(DateTimeUtils.caculatorTime(Calendar.getInstance().getTime().getTime(), post.getUpdated_at().getTime()));
                }catch (ParseException e){

                }
                if (cmtFocus){
                    edtComment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
                }
                imgPostAvatar.setOnClickListener((v) -> eventViewProfile(user));
                tvPostUserName.setOnClickListener((v) -> eventViewProfile(user));

                PostImageAdapter imageAdapter = new PostImageAdapter();
                imageAdapter.setMedias(post.getGallery().getMedia());
                imageAdapter.setContext(PostDetailActivity.this);
                SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(
                        SpannedGridLayoutManager.Orientation.VERTICAL, 3);
                spannedGridLayoutManager.setItemOrderIsStable(true);
                rcvPostImage.addItemDecoration(new SpaceItemDecorator1(new Rect(10, 10, 10, 10)));
                rcvPostImage.setLayoutManager(spannedGridLayoutManager);
                rcvPostImage.setAdapter(imageAdapter);
                int size = post.getGallery().getMedia().size();
                int width = LocalDisplay.getScreenWidth(getBaseContext());
                rcvPostImage.getLayoutParams().height = (int)(width/3 * Math.ceil((double)size/3));
                rcvPostImage.requestLayout();
                //scrollToPosition();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                onBackPressed();
            }
        });

        prepareTransitions();
    }

    private void eventViewProfile(User user) {
        Intent i = new Intent(this, ProfileMemberActivity.class);
        i.putExtra("firstname", user.getFirstName());
        i.putExtra("lastname", user.getLastName());
        if (MainActivity.user.getId() == user.getId()) {
            i.putExtra("owner", true);
        } else {
            i.putExtra("owner", false);
        }
        i.putExtra("id", user.getId());
        startActivity(i);
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

    private void prepareTransitions() {
        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = rcvPostImage
                                .findViewHolderForAdapterPosition(PostDetailActivity.currentPosition);
                        if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.postImage));
                    }
                });
    }
}
