package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostDetailActivity;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> listNoti;
    private int olderId;
    private Fragment fragment;
    private Context context;
    private String token;
    private boolean stopLoad = false;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        token = Session.getToken(((NotificationFragment) fragment).getActivity());
    }

    public void setOlderId(int olderId) {
        this.olderId = olderId;
    }

    public void setListNoti(List<Notification> listNoti) {
        this.listNoti = listNoti;
    }

    public NotificationAdapter() {

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification noti = listNoti.get(position);
        holder.setNotification(noti);
        switch (noti.getTypeId()) {
            case 1:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_like));
                break;
            case 2:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_like));
                break;
            case 3:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_comment));
                break;
            case 4:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_comment));
                break;
            case 9:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 10:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 11:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 12:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 13:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 14:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 15:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
            case 16:
                holder.notiType.setImageDrawable(context.getDrawable(R.drawable.ic_noti_schedule));
                break;
        }
        holder.token = token;
        if (noti.getState() == 0 || noti.getState() == 1) {
            holder.layout.setBackgroundResource(R.color.colorPrimaryLight);
        } else {
            holder.layout.setBackgroundResource(R.color.colorWhite);
        }
        String user = noti.getUser().getFirstName() + " " + noti.getUser().getLastName();
        String content = noti.getContent().replace("${{source}}", "<b>" + user + "</b>");
        if (noti.getContent().contains("${{source.gender}}")) {
            content = noti.getContent().replace("${{source.gender}}", noti.getUser().getGender() == 0 ? "anh ấy" : "cô ấy");
        }
        holder.tvNotiTitle.setText(Html.fromHtml(content));
//        if (!noti.getUser().getAvatar().getPath().isEmpty()) {
//            GlideApp.with(holder.itemView)
//                    .load(APIUtils.BASE_URL_API + noti.getUser().getAvatar().getPath().substring(4) + "&w=" + LocalDisplay.dp2px(72, context))
//                    .placeholder(R.drawable.avatar_default)
//                    .fallback(R.drawable.avatar_default)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(holder.imgNoti);
//        }

        DateTimeUtils datetime = new DateTimeUtils();
        try {
            holder.tvNotiTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), noti.getUpdated_at().getTime()));
        } catch (Exception ex) {

        }

        if (position == getItemCount() - 1 && !stopLoad) {
            ((NotificationFragment) fragment).showProgress();
            APIService mWebService = APIUtils.getWebService();
            mWebService.getOldNoti(token, olderId).enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    List<Notification> list = response.body();
                    if (list != null || !list.isEmpty()) {
                        setOlderId(list.get(0).getOldestId());
                        list.remove(0);
                        for (Notification notifi : list) {
                            listNoti.add(notifi);
                            notifyDataSetChanged();
                        }
                    }
                    ((NotificationFragment) fragment).stopProgress();
                    if (list.size() < 10) {
                        stopLoad = true;
                    }
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {
                    ((NotificationFragment) fragment).stopProgress();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (listNoti == null) {
            listNoti = new ArrayList<>();
        }
        return listNoti.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        String token;
        Notification notification;
        ImageView imgNoti;
        TextView tvNotiTime;
        TextView tvNotiTitle;
        AppCompatImageButton btnNotiOption;
        ConstraintLayout layout;
        ImageView notiType;

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imgNoti = (ImageView) itemView.findViewById(R.id.imgNotiAvatar);
            tvNotiTime = (TextView) itemView.findViewById(R.id.tvNotiTime);
            tvNotiTitle = (TextView) itemView.findViewById(R.id.tvNotiTitle);
            btnNotiOption = (AppCompatImageButton) itemView.findViewById(R.id.btnNotiOption);
            layout = (ConstraintLayout) itemView.findViewById(R.id.constrainlayout);
            notiType = (ImageView) itemView.findViewById(R.id.imgNotiType);

            btnNotiOption.setOnClickListener((v) -> {
                PopupMenu popupMenu = new PopupMenu(context, btnNotiOption);
                popupMenu.getMenuInflater().inflate(R.menu.action_notification, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        APIService service = APIUtils.getWebService();
                        service.markReadNotification(token,
                                notification.getId()).enqueue(new Callback<Notification>() {
                            @Override
                            public void onResponse(Call<Notification> call, Response<Notification> response) {
                                layout.setBackgroundResource(R.color.colorWhite);
                            }

                            @Override
                            public void onFailure(Call<Notification> call, Throwable t) {

                            }
                        });

                        return true;
                    }
                });
                popupMenu.show();
            });
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PostDetailActivity.class);
            APIService service = APIUtils.getWebService();
            service.markReadNotification(token,
                    notification.getId()).enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
                    layout.setBackgroundResource(R.color.colorWhite);
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {

                }
            });
            switch (notification.getTypeId()) {
                case 1:
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("token", Session.getToken(context));
                    context.startActivity(intent);
                    break;
                case 2:
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("token", Session.getToken(context));
                    context.startActivity(intent);
                    break;
                case 3:
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("token", Session.getToken(context));
                    context.startActivity(intent);
                    break;
                case 4:
                    intent.putExtra("token", Session.getToken(context));
                    intent.putExtra("id", notification.getComment().getTargetId());
                    intent.putExtra("cmtPosition", notification.getTargetId());
                    context.startActivity(intent);
                    break;
                case 9:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 10:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 11:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 12:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 13:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 14:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 15:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
                case 16:
                    intent = new Intent(context, PlanDetailActivity.class);
                    intent.putExtra("id", notification.getTargetId());
                    intent.putExtra("userId", notification.getTargetOwner().getId());
                    context.startActivity(intent);
                    break;
            }
        }
    }


}
