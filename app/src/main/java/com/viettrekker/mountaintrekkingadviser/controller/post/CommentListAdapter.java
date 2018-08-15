package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Comment;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder>{
    private List<Comment> list;
    private Context context;


    public List<Comment> getList() {
        return list;
    }

    public void setList(List<Comment> list) {
        this.list = list;
    }

    public CommentListAdapter(Context context) {
        this.context = context;
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
        viewHolder.tvCmtContent.setText(comment.getContent());
        viewHolder.tvUserCmt.setText(comment.getUser().getFirstName() + " " + list.get(position).getUser().getLastName());
        viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));
        viewHolder.btnLikeCmt.setText("Thích");
        if (comment.getLiked() != 0){
            viewHolder.likeFlag = true;
            viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnLikeCmt.setText("Đã thích");
        }
        viewHolder.likeCount.setText(comment.getLikesCount() > 0 ? comment.getLikesCount()+"" : "");
        DateTimeUtils datetime = new DateTimeUtils();
        try {
            viewHolder.tvTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), comment.getUpdated_at().getTime()));
        }catch (Exception ex){

        }
        if (viewHolder.rcvCmtItem != null){
            CommentChildrenAdapter cmtChild = new CommentChildrenAdapter(context);
            cmtChild.setList(comment.getChildren());
            cmtChild.notifyDataSetChanged();
            viewHolder.rcvCmtItem.setAdapter(cmtChild);
        }
        viewHolder.rcvCmtItem.setVisibility(View.GONE);
        viewHolder.cmtCount.setText(comment.getChildren().size() > 0 ? comment.getChildren().size()+"" : "");
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
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                            alertDialogBuilder.setTitle("Báo cáo bài viết");
                            EditText edtRP = new EditText(context);
                            edtRP.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertDialogBuilder.setView(edtRP);
                            alertDialogBuilder.setMessage("Vui lòng cho chúng tôi biết lý do báo cáo bài viết này.")
                                    .setCancelable(false)
                                    .setPositiveButton("Gửi",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            if (!edtRP.getText().toString().trim().equalsIgnoreCase("")){
                                                Toast.makeText(context,"Đã báo cáo bài viết.",Toast.LENGTH_LONG).show();
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
                        if (MainActivity.user.getId() == comment.getUser().getId()){
                            if (menuItem.getTitle().equals("Sửa")){
                                ((PostDetailActivity)context).edtComment.setText(comment.getContent());
                                ((PostDetailActivity)context).edtComment.requestFocus();
                                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(((PostDetailActivity) context).edtComment, InputMethodManager.SHOW_IMPLICIT);
                                ((PostDetailActivity)context).edtComment.setSelection(((PostDetailActivity)context).edtComment.getText().length());
                            }else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn có muốn xóa bình luận?")
                                        .setCancelable(false)
                                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                list.remove(position);
                                                notifyItemRemoved(position);
                                                Toast.makeText(context, "Đã xóa",Toast.LENGTH_SHORT).show();
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
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
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
        TextView tvCmtContent;
        TextView tvUserCmt;
        AppCompatImageButton btnActionCmt;
        TextView btnLikeCmt;
        TextView likeCount;
        TextView tvTime;
        RecyclerView rcvCmtItem;
        TextView tvReplyComment;
        boolean commentFlag ;
        boolean likeFlag;
        TextView cmtCount;
        TextView tvReadMore;
        boolean rmFlag;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imgAvtCmt = (ImageView) itemView.findViewById(R.id.imgAvtCmt);
            tvCmtContent = (TextView) itemView.findViewById(R.id.tvCmtContent);
            tvUserCmt =(TextView) itemView.findViewById(R.id.tvUserCmt);
            btnActionCmt = (AppCompatImageButton) itemView.findViewById(R.id.btnActionCmt);
            btnLikeCmt = (TextView) itemView.findViewById(R.id.btnLikeCmt);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            tvReplyComment = (TextView) itemView.findViewById(R.id.tvReplyComment);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            cmtCount = (TextView) itemView.findViewById(R.id.cmtCount) ;
            tvReadMore = (TextView) itemView.findViewById(R.id.tvReadMore);
            if (context != null){
                rcvCmtItem = (RecyclerView) itemView.findViewById(R.id.rcvCmtChild);
                rcvCmtItem.setLayoutManager(new LinearLayoutManager(context));
            }
            commentFlag = false;
            rmFlag = false;
            tvReplyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!commentFlag){
                        rcvCmtItem.setVisibility(View.VISIBLE);
                        tvReplyComment.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        commentFlag = true;
                    } else {
                        rcvCmtItem.setVisibility(View.GONE);
                        tvReplyComment.setTextColor(context.getResources().getColor(R.color.colorGray));
                        commentFlag = false;
                    }
                }
            });
            btnLikeCmt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!likeFlag){
                        btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        btnLikeCmt.setText("Đã thích");
                        likeFlag = true;
                    } else {
                        btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));
                        btnLikeCmt.setText("Thích");
                        likeFlag = false;
                    }
                }
            });

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
        if (MainActivity.user.getId() == user.getId()) {
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
