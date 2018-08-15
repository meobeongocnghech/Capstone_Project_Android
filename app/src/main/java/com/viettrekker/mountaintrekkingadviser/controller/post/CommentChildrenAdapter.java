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
import android.text.method.ScrollingMovementMethod;
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

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Comment;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommentChildrenAdapter extends RecyclerView.Adapter<CommentChildrenAdapter.ViewHolder>{
    private List<Comment> list;
    private Context context;

    public void setList(List<Comment> list) {
        this.list = list;
    }

    public CommentChildrenAdapter(Context context) {
        this.context = context;
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
        viewHolder.tvCmtContent.setText(comment.getContent());
        viewHolder.tvUserCmt.setText(comment.getUser().getFirstName() + " " + list.get(i).getUser().getLastName());
        viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorGray));
        viewHolder.btnLikeCmt.setText("Thích");
        if (comment.getLiked() != 0){
            viewHolder.btnLikeCmt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btnLikeCmt.setText("Đã thích");
        }
        viewHolder.likeCount.setText(comment.getLikesCount()+"");
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
                        if (MainActivity.user.getId() == user.getId()){
                            if (menuItem.getTitle().equals("Sửa")){
                                ((PostDetailActivity)context).edtComment.setText(comment.getContent());
                                ((PostDetailActivity)context).edtComment.requestFocus();
                                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(((PostDetailActivity) context).edtComment, InputMethodManager.SHOW_IMPLICIT);
                                ((PostDetailActivity)context).edtComment.setSelection(((PostDetailActivity)context).edtComment.getText().length());
                            } else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                alertDialogBuilder.setTitle("Cảnh báo");
                                alertDialogBuilder.setMessage("Bạn có muốn xóa bình luận?")
                                        .setCancelable(false)
                                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int index) {
                                                list.remove(i);
                                                notifyItemRemoved(i);
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
        TextView tvCmtContent;
        TextView tvUserCmt;
        AppCompatImageButton btnActionCmt;
        TextView btnLikeCmt;
        TextView likeCount;
        TextView tvTime;
        boolean likeFlag;
        boolean rmFlag;
        TextView tvReadMoreChild;
        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imgAvtCmt = (ImageView) itemView.findViewById(R.id.imgAvtCmtRep);
            tvCmtContent = (TextView) itemView.findViewById(R.id.tvCmtContentRep);
            tvUserCmt =(TextView) itemView.findViewById(R.id.tvUserCmtRep);
            btnActionCmt = (AppCompatImageButton) itemView.findViewById(R.id.btnActionCmtRep);
            btnLikeCmt = (TextView) itemView.findViewById(R.id.tvLikeCmtRep);
            likeCount = (TextView) itemView.findViewById(R.id.likeCountRep);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeRep);
            tvReadMoreChild = (TextView) itemView.findViewById(R.id.tvReadMoreChild);
            rmFlag = false;
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
        if (MainActivity.user.getId() == user.getId()) {
            i.putExtra("owner", true);
        } else {
            i.putExtra("owner", false);
        }
        i.putExtra("id", user.getId());
        context.startActivity(i);
    }
}
