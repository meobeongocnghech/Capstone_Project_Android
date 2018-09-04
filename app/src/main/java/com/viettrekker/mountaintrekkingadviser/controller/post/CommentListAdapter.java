package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Layout;
import android.text.util.Linkify;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiInformation;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.EmojiUtils;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Comment;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder>{
    private List<Comment> list;
    private Context context;
    private APIService mWebService = APIUtils.getWebService();
    int targetCmtid = -1;
    private String token;
    private int userId;
    private boolean isByUserId;

    int idCmtEdit = -1;
    public List<Comment> getList() {
        return list;
    }

    public void setByUserId(boolean byUserId) {
        isByUserId = byUserId;
    }

    public void setList(List<Comment> list) {
        this.list = list;
    }

    public CommentListAdapter(Context context) {
        this.context = context;
        token = Session.getToken(context);
        userId = Session.getUserId(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment_item,parent,false);

        return new CommentListAdapter.ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Comment comment = list.get(position);
        User user = comment.getUser();
        DateTimeUtils datetime = new DateTimeUtils();
        CommentChildrenAdapter cmtChild = new CommentChildrenAdapter(context, comment.getTargetId(), token, userId);
        HashTagHelper helper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorPrimary), null);
        helper.handle(viewHolder.tvCmtContent);
        viewHolder.tvCmtContent.setAutoLinkMask(Linkify.WEB_URLS);
        viewHolder.tvCmtContent.setText(comment.getContent());
        viewHolder.tvUserCmt.setText(comment.getUser().getFirstName() + " " + list.get(position).getUser().getLastName());
        viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));
        viewHolder.btnLikeCmt.setText("Thích");
        viewHolder.likeCount.setText(comment.getLikesCount() == 0 ? "" : comment.getLikesCount()+"");
        if (comment.getLiked() != 0){
            viewHolder.likeFlag = true;
            viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnLikeCmt.setText("Đã thích");
            viewHolder.likeCount.setText(comment.getLikesCount()+"");
        }

        try {
            viewHolder.tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), comment.getUpdated_at().getTime()));
        }catch (Exception ex){

        }
        if (viewHolder.rcvCmtItem != null){
            cmtChild.setList(comment.getChildren());
            cmtChild.notifyDataSetChanged();
            viewHolder.rcvCmtItem.setAdapter(cmtChild);
        }

        if (comment.getUser().getAvatar() != null && !comment.getUser().getAvatar().getPath().isEmpty()) {
            GlideApp.with(viewHolder.itemView)
                    .load(APIUtils.BASE_URL_API + comment.getUser().getAvatar().getPath().substring(4)+"&w=" + LocalDisplay.dp2px(60, context))
                    .fallback(R.drawable.avatar_default)
                    .placeholder(R.drawable.avatar_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.imgAvtCmt);
        }

        viewHolder.rcvCmtItem.setVisibility(View.GONE);
        viewHolder.cmtCount.setText(comment.getChildren().size() == 0 ? "" : comment.getChildren().size()+"");
        viewHolder.imgAvtCmt.setOnClickListener((v) -> eventViewProfile(user));
        viewHolder.tvUserCmt.setOnClickListener((v) -> eventViewProfile(user));

        viewHolder.tvReadMore.setVisibility(View.INVISIBLE);
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
                            viewHolder.tvReadMore.setVisibility(View.VISIBLE);
                            viewHolder.tvReadMore.setText("Xem thêm");
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
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Báo cáo")){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Báo cáo bài viết");
                            EditText edtRP = new EditText(context);
                            edtRP.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertDialogBuilder.setView(edtRP);
                            alertDialogBuilder.setMessage("Vui lòng cho chúng tôi biết lý do báo cáo bài viết này.")
                                    .setPositiveButton("Gửi",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            if (!edtRP.getText().toString().matches("")){
                                                mWebService.reportComent(token,comment.getTargetId(), comment.getId(), edtRP.getText().toString()).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        if (response.code() == 200){
                                                            dialog.cancel();
                                                            Toast.makeText(context,"Báo cáo của bạn đã được ghi nhận",Toast.LENGTH_LONG).show();

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
                        if (userId == comment.getUser().getId()){
                            if (menuItem.getTitle().equals("Sửa")){
                                ((PostDetailActivity)context).edtComment.setText(viewHolder.tvCmtContent.getText().toString());
                                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(((PostDetailActivity)context).edtComment, InputMethodManager.SHOW_IMPLICIT);
                                ((PostDetailActivity)context).edtComment.setSelection(((PostDetailActivity)context).edtComment.getText().length());
                                ((PostDetailActivity)context).setUpdateCmt(true);
                                idCmtEdit = comment.getId();
                            }else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn có muốn xóa bình luận?")
                                        .setCancelable(false)
                                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                mWebService.removeComment(token, comment.getTargetId(),comment.getId()).enqueue(new Callback<Post>() {
                                                    @Override
                                                    public void onResponse(Call<Post> call, Response<Post> response) {
                                                        if (response.code() == 200){
                                                            list.remove(position);
                                                            notifyDataSetChanged();
                                                            Toast.makeText(context, "Đã xóa",Toast.LENGTH_SHORT).show();
                                                            ((PostDetailActivity)context).tvCmtCount.setText(list.size()== 0 ? "" : list.size() + " bình luận");

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

                            }
                        } else {
                            if (menuItem.getTitle().equals("Sửa") || menuItem.getTitle().equals("Xóa") ){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        viewHolder.btnLikeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!viewHolder.likeFlag){
                    mWebService.likeComment(token, comment.getTargetId(),comment.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (response.code() == 200){
                                viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                viewHolder.btnLikeCmt.setText("Đã thích");
                                comment.setLikesCount(comment.getLikesCount() + 1);
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
                    mWebService.unlikeComment(token, comment.getTargetId(),comment.getId()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (response.code() == 200){
                                viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));
                                viewHolder.btnLikeCmt.setText("Thích");
                                comment.setLikesCount(comment.getLikesCount() - 1);
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

        viewHolder.btnReplyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.commentFlag){
                    viewHolder.rcvCmtItem.setVisibility(View.VISIBLE);
                    viewHolder.btnReplyComment.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    ((PostDetailActivity)context).edtComment.setHint("Trả lời bình luận của "+ comment.getUser().getFirstName());
                    viewHolder.commentFlag = true;
                    targetCmtid = comment.getId();
                    if (comment.getChildren().size() == 0){
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(((PostDetailActivity)context).edtComment, InputMethodManager.SHOW_IMPLICIT);
                        ((PostDetailActivity)context).edtComment.requestFocus();
                        ((PostDetailActivity)context).rcvCmtItem.smoothScrollToPosition(position);
                        ((PostDetailActivity)context).rcvCmtItem.setNestedScrollingEnabled(false);
                    }
                } else {
                    viewHolder.rcvCmtItem.setVisibility(View.GONE);
                    viewHolder.btnReplyComment.setTextColor(context.getResources().getColor(R.color.colorGray));
                    ((PostDetailActivity)context).edtComment.setHint("Bình luận về bài viết");
                    viewHolder.commentFlag = false;
                }
            }
        });

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
        MaterialButton btnLikeCmt;
        TextView likeCount;
        TextView tvTime;
        RecyclerView rcvCmtItem;
        MaterialButton btnReplyComment;
        boolean commentFlag ;
        boolean likeFlag;
        TextView cmtCount;
        TextView tvReadMore;
        boolean rmFlag;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imgAvtCmt = (ImageView) itemView.findViewById(R.id.imgAvtCmt);
            tvCmtContent = (EmojiTextView) itemView.findViewById(R.id.tvCmtContent);
            tvUserCmt =(TextView) itemView.findViewById(R.id.tvUserCmt);
            btnActionCmt = (AppCompatImageButton) itemView.findViewById(R.id.btnActionCmt);
            btnLikeCmt = (MaterialButton) itemView.findViewById(R.id.btnLikeCmt);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            btnReplyComment = (MaterialButton) itemView.findViewById(R.id.btnReplyComment);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            cmtCount = (TextView) itemView.findViewById(R.id.cmtCount) ;
            tvReadMore = (TextView) itemView.findViewById(R.id.tvReadMore);
            if (context != null){
                rcvCmtItem = (RecyclerView) itemView.findViewById(R.id.rcvCmtChild);
                rcvCmtItem.setLayoutManager(new LinearLayoutManager(context));
            }
            commentFlag = false;
            rmFlag = false;



            tvReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!rmFlag){
                        tvCmtContent.setMaxLines(100);
                        tvReadMore.setText("Thu gọn");
                        rmFlag = true;
                    } else {
                        tvCmtContent.setMaxLines(3);
                        tvReadMore.setVisibility(View.VISIBLE);
                        tvReadMore.setText("Xem thêm");
                        rmFlag = false;
                    }
                }
            });

        }

    }
    private void eventViewProfile(User user) {
        Intent i = new Intent(context, ProfileMemberActivity.class);
        i.putExtra("firstname", user.getFirstName());
        i.putExtra("lastname", user.getLastName());
        if (userId == user.getId()) {
            i.putExtra("owner", true);
        } else {
            i.putExtra("owner", false);
        }
        i.putExtra("id", user.getId());
        context.startActivity(i);
    }

    public int findCmtPosition(int id) {
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }
}
