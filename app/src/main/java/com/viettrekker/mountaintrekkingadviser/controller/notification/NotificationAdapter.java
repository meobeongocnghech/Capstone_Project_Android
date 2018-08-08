package com.viettrekker.mountaintrekkingadviser.controller.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> listNoti;
    private int olderId;

    public void setOlderId(int olderId) {
        this.olderId = olderId;
    }

    public void setListNoti(List<Notification> listNoti) {
        this.listNoti = listNoti;
    }

    public List<Notification> getListNoti() {
        return listNoti;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification noti = listNoti.get(position);
        if (noti.getState() == 0){
            holder.layout.setBackgroundResource(R.color.colorGreyHint);
        } else {
            holder.layout.setBackgroundResource(R.color.colorDefaultBackground);
        }
        String user = noti.getSource().getLastname() + " " + noti.getSource().getFirstname();
        String content = noti.getContent().replace("${{source}}", user);
        holder.tvNotiTitle.setText(content);
        DateTimeUtils datetime = new DateTimeUtils();
        try {
            holder.tvNotiTime.setText(datetime.caculatorTime(Calendar.getInstance().getTime().getTime(), noti.getUpdated_at().getTime()));
        }catch (Exception ex){

        }
        Picasso.get().load("https://openclipart.org/download/4749/acspike-male-user-icon.svg").error(R.drawable.error_icon).into(holder.imgNoti);
        if (position == getItemCount() - 1 ){
            APIService mWebService = APIUtils.getWebService();
            mWebService.getOldNoti(MainActivity.user.getToken(),olderId ).enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    List<Notification> list = response.body();
                    if (list != null) {
                        setOlderId(list.get(0).getOldestId());
                        list.remove(0);
                        for (Notification notifi: list ) {
                            listNoti.add(notifi);
                            notifyDataSetChanged();
                        }


                    }
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {
//                    Toast.makeText(, "Xảy ra lỗi", Toast.LENGTH_LONG).show();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNoti;
        TextView tvNotiTime;
        TextView tvNotiTitle;
        AppCompatImageButton btnNotiOption;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNoti = (ImageView) itemView.findViewById(R.id.imgNotiAvatar);
            tvNotiTime = (TextView) itemView.findViewById(R.id.tvNotiTime);
            tvNotiTitle = (TextView) itemView.findViewById(R.id.tvNotiTitle);
            btnNotiOption = (AppCompatImageButton) itemView.findViewById(R.id.btnNotiOption);
            layout = (ConstraintLayout) itemView.findViewById(R.id.constrainlayout);
        }
    }


}
