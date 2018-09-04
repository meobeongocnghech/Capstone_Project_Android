package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Layout;
import android.text.util.Linkify;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.vanniktech.emoji.EmojiTextView;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Comment;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentChildrenAdapter extends RecyclerView.Adapter<CommentChildrenAdapter.ViewHolder>{
    private List<Comment> list;
    private Context context;
    private int userId;
    public void setList(List<Comment> list) {
        this.list = list;
    }
    private APIService mWebService = APIUtils.getWebService();
    private String token;
    private int accountUserId;

    public CommentChildrenAdapter(Context context, int commentId, String token, int uId) {
        this.context = context;
        this.userId = commentId;
        this.token = token;
        accountUserId = uId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_commentreply_item,viewGroup,false);
        return new CommentChildrenAdapter.ViewHolder(view, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CommentChildrenAdapter.ViewHolder viewHolder, int i) {
        Comment comment = list.get(i);
        User user = comment.getUser();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        viewHolder.likeCount.setText(comment.getLikesCount() == 0 ? "" : comment.getLikesCount()+"");
        HashTagHelper helper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorPrimary), null);
        helper.handle(viewHolder.tvCmtContent);
        viewHolder.tvCmtContent.setAutoLinkMask(Linkify.WEB_URLS);
        viewHolder.tvCmtContent.setText(comment.getContent());
        viewHolder.tvUserCmt.setText(comment.getUser().getFirstName() + " " + list.get(i).getUser().getLastName());
        viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));

        viewHolder.btnLikeCmt.setText("Thích");
        if (comment.getLiked() != 0){
            viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnLikeCmt.setText("Đã thích");
            viewHolder.likeCount.setText(comment.getLikesCount()+"");
        }
        viewHolder.btnLikeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.likeFlag){
                    mWebService.likeComment(token, userId,comment.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (response.code() == 200){
                                viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                viewHolder.btnLikeCmt.setText("Đã thích");
                                comment.setLikesCount(comment.getLikesCount()+1);
                                viewHolder.likeCount.setText((comment.getLikesCount()) +"");

                                viewHolder.likeFlag = true;
                                ((PostDetailActivity)context).edtComment.clearFocus();
                            }

                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {

                        }
                    });

                } else {
                    mWebService.unlikeComment(token, userId,comment.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (response.code() == 200){
                                viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));
                                viewHolder.btnLikeCmt.setText("Thích");
                                comment.setLikesCount(  comment.getLikesCount()-1);
                                viewHolder.likeCount.setText(comment.getLikesCount() <1 ? "" : (comment.getLikesCount()) +"");
                                viewHolder.likeFlag = false;
                                ((PostDetailActivity)context).edtComment.clearFocus();
                            }

                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {

                        }
                    });

                }
            }
        });
        DateTimeUtils datetime = new DateTimeUtils();
        try {
            viewHolder.tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), comment.getUpdated_at().getTime()));
        }catch (Exception ex){

        }
        viewHolder.tvReadMoreChild.setVisibility(View.INVISIBLE);
        viewHolder.rmFlag = false;
        ViewTreeObserver vto = viewHolder.tvCmtContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout l = viewHolder.tvCmtContent.getLayout();
                if ( l != null){
                    int lines = l.getLineCount();
                    if ( lines > 0) {
                        if ( l.getEllipsisCount(lines-1) > 0) {
                            viewHolder.tvCmtContent.setMaxLines(3);
                            viewHolder.tvReadMoreChild.setVisibility(View.VISIBLE);
                            viewHolder.tvReadMoreChild.setText("Xem thêm");
                            viewHolder.rmFlag = false;
                        }
                    }
                }
            }
        });

        viewHolder.btnActionCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.btnActionCmt);
                popupMenu.getMenuInflater().inflate(R.menu.action_popup_menu, popupMenu.getMenu());
                Menu m = popupMenu.getMenu();
                m.findItem(R.id.actUpdate).setVisible(false);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Báo cáo")){
                            alertDialogBuilder.setTitle("Báo cáo bài viết");
                            EditText edtRP = new EditText(context);
                            edtRP.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertDialogBuilder.setView(edtRP);
                            alertDialogBuilder.setMessage("Vui lòng cho chúng tôi biết lý do báo cáo bài viết này.")
                                    .setCancelable(false)
                                    .setPositiveButton("Gửi",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            if (!edtRP.getText().toString().matches("")){
                                                mWebService.reportComent(token, userId, comment.getId(), edtRP.getText().toString()).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        if (response.code() == 200){
                                                            dialog.cancel();
                                                            Toast.makeText(context,"Bạn đã báo cáo bình luận này",Toast.LENGTH_LONG).show();
                                                        }

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
                        if (accountUserId == user.getId()){
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn có muốn xóa bình luận?")
                                        .setCancelable(false)
                                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int index) {
                                                mWebService.removeComment(token,userId,comment.getId()).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        if (response.code() == 200){
                                                            list.remove(i);
                                                            notifyDataSetChanged();
                                                            Toast.makeText(context, "Đã xóa",Toast.LENGTH_SHORT).show();
                                                        }

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
                            } else
                            if (menuItem.getTitle().equals("Xóa") ){
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn chỉ có thể thao tác trên bình luận của mình.")
                                        .setCancelable(false)
                                        .setNegativeButton("Đóng",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        if (user.getAvatar() != null && !user.getAvatar().getPath().isEmpty()) {
            GlideApp.with(viewHolder.itemView)
                    .load(APIUtils.BASE_URL_API + comment.getUser().getAvatar().getPath().substring(4) + "&w=" + LocalDisplay.dp2px(60, context))
                    .fallback(R.drawable.avatar_default)
                    .placeholder(R.drawable.avatar_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.imgAvtCmt);
        }

        viewHolder.imgAvtCmt.setOnClickListener((v) -> eventViewProfile(user));
        viewHolder.tvUserCmt.setOnClickListener((v) -> eventViewProfile(user));
    }

    @Override
    public int getItemCount() {
        if (list == null){
            list = new ArrayList<>();
        }
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvtCmt;
        EmojiTextView tvCmtContent;
        TextView tvUserCmt;
        AppCompatImageButton btnActionCmt;
        TextView btnLikeCmt;
        TextView likeCount;
        TextView tvTime;
        boolean likeFlag;
        boolean rmFlag;
        TextView tvReadMoreChild;
        TextView likeCountRep;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imgAvtCmt = (ImageView) itemView.findViewById(R.id.imgAvtCmtRep);
            tvCmtContent = (EmojiTextView) itemView.findViewById(R.id.tvCmtContentRep);
            tvUserCmt =(TextView) itemView.findViewById(R.id.tvUserCmtRep);
            btnActionCmt = (AppCompatImageButton) itemView.findViewById(R.id.btnActionCmtRep);
            btnLikeCmt = (TextView) itemView.findViewById(R.id.tvLikeCmtRep);
            likeCount = (TextView) itemView.findViewById(R.id.likeCountRep);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeRep);
            tvReadMoreChild = (TextView) itemView.findViewById(R.id.tvReadMoreChild);
            likeCountRep = (TextView) itemView.findViewById(R.id.likeCountRep);
            rmFlag = false;

            tvReadMoreChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!rmFlag){
                        tvCmtContent.setMaxLines(100);
                        tvReadMoreChild.setText("Thu gọn");
                        rmFlag = true;
                    } else {
                        tvCmtContent.setMaxLines(3);
                        tvReadMoreChild.setVisibility(View.VISIBLE);
                        tvReadMoreChild.setText("Xem thêm");
                        rmFlag = false;
                    }
                }
            });
            btnActionCmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, btnActionCmt);
                    popupMenu.getMenuInflater().inflate(R.menu.action_popup_menu, popupMenu.getMenu());
                    popupMenu.show();
                }
            });
        }
    }

    private void eventViewProfile(User user) {
        Intent i = new Intent(context, ProfileMemberActivity.class);
        i.putExtra("firstname", user.getFirstName());
        i.putExtra("lastname", user.getLastName());
        if (accountUserId == user.getId()) {
            i.putExtra("owner", true);
        } else {
            i.putExtra("owner", false);
        }
        i.putExtra("id", user.getId());
        context.startActivity(i);
    }
}
