package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationAdapter;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder> {
    private List<Member> users;
    private Context context;
    private String token;
    private int userId;

    APIService mWebService = APIUtils.getWebService();

    public void setContext(Context context) {
        this.context = context;
        token = Session.getToken(context);
        userId = Session.getUserId(context);
    }

    public void setUsers(List<Member> users) {
        this.users = users;
    }

    public MembersListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_user_icon, viewGroup, false);
        return new MembersListAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.imgRemoveUser.setVisibility(View.VISIBLE);
        viewHolder.member = users.get(i);
        viewHolder.token = token;
        viewHolder.userId = userId;
        mWebService.getUserById(token, viewHolder.member.getUserId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                           viewHolder.tvNameItem.setText(response.body().getFirstName() + " " + response.body().getLastName());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(MembersListAdapter.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        viewHolder.tvGenderItem.setText("Thành viên");
        if (userId == viewHolder.member.getUserId() && viewHolder.member.getRoleInGroupId() == 1){
            viewHolder.imgRemoveUser.setVisibility(View.INVISIBLE);
        }
        if (userId == viewHolder.member.getUserId()){
            viewHolder.imgPhoneCall.setVisibility(View.INVISIBLE);
        }
        if (viewHolder.member.getRoleInGroupId() == 1){
            viewHolder.tvGenderItem.setText("Trưởng đoàn");

        } else if(viewHolder.member.getRoleInGroupId() == 4){
            viewHolder.tvGenderItem.setText("Đang mời...");
            viewHolder.imgPhoneCall.setVisibility(View.GONE);
        }
        viewHolder.imgRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.remove(viewHolder.member);
                notifyItemRemoved(i);
//                NewPlanActivity newPlanActivity = new NewPlanActivity();
//                newPlanActivity.members = users;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (users == null){
            users = new ArrayList<>();
        }
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Member member;
        Context context;
        String token;
        int userId;
        ImageView imgAvtItem;
        TextView tvNameItem;
        TextView tvGenderItem;
        ImageView imgPhoneCall;
        ImageView imgRemoveUser;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imgAvtItem = (ImageView) itemView.findViewById(R.id.imgAvtItem);
            tvNameItem = (TextView) itemView.findViewById(R.id.tvNameItem);
            tvGenderItem = (TextView) itemView.findViewById(R.id.tvGenderItem);
            imgPhoneCall = (ImageView) itemView.findViewById(R.id.imgPhoneCall);
            imgRemoveUser = (ImageView) itemView.findViewById(R.id.imgInviteState);
            imgAvtItem.setOnClickListener((v) -> eventViewProfile());
            tvNameItem.setOnClickListener((v) -> eventViewProfile());
        }
        private void eventViewProfile() {
            Intent i = new Intent(context, ProfileMemberActivity.class);
            i.putExtra("id", member.getUserId());
            i.putExtra("token", token);
            if (userId == member.getUserId()) {
                i.putExtra("owner", true);
            } else {
                i.putExtra("owner", false);
            }
            context.startActivity(i);
        }

    }

}
