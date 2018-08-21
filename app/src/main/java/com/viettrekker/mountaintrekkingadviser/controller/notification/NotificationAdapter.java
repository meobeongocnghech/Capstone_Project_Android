package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.plan.PlanFragment;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostDetailActivity;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;

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
        holder.token = token;
        if (noti.getState() == 0 || noti.getState() == 1) {
            holder.layout.setBackgroundResource(R.color.colorPrimaryLight);
        } else {
            holder.layout.setBackgroundResource(R.color.colorWhite);
        }
        String user = noti.getUser().getFirstName() + " " + noti.getUser().getLastName();
        String content = noti.getContent().replace("${{source}}", "<b>" + user + "</b>");
        holder.tvNotiTitle.setText(Html.fromHtml(content));
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
            if (notification.getTypeId() == 4) {
                intent.putExtra("token",MainActivity.user.getToken());
                intent.putExtra("id", notification.getComment().getTargetId());
                intent.putExtra("cmtPosition", notification.getTargetId());
                context.startActivity(intent);
            } else {
                intent.putExtra("id", notification.getTargetId());
                intent.putExtra("token",MainActivity.user.getToken());
                context.startActivity(intent);
            }
        }
    }


}
