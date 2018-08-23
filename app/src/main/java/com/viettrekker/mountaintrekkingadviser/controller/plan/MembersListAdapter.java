package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder> {
    private List<Member> users;
    private Context context;
    private String token;
    private int userId;
    private int planId;

    APIService mWebService = APIUtils.getWebService();

    AlertDialog.Builder alertDialogBuilder;

    public void setContext(Context context) {
        this.context = context;
        token = Session.getToken(context);
        userId = Session.getUserId(context);
    }

    public void setPlanId(int planId) {
        this.planId = planId;
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
        Collections.sort(users, new Comparator<Member>() {
            @Override
            public int compare(Member member, Member t1) {
                if (member.getRoleInGroupId() > t1.getRoleInGroupId()) {
                    return 1;
                } else if (member.getRoleInGroupId() < t1.getRoleInGroupId()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        viewHolder.member = users.get(i);
        viewHolder.token = token;
        viewHolder.userId = userId;
        token = Session.getToken(context);
        userId = Session.getUserId(context);
        alertDialogBuilder = new AlertDialog.Builder(context);
        viewHolder.tvNameItem.setText(viewHolder.member.getFirstname() + " " + viewHolder.member.getLastname());

        viewHolder.tvGenderItem.setText("Thành viên");
        viewHolder.imgPhoneCall.setVisibility(View.VISIBLE);
        viewHolder.imgRemoveUser.setVisibility(View.GONE);
        viewHolder.imgApproveRequest.setVisibility(View.GONE);
        if (userId == viewHolder.member.getUserId()) {
            viewHolder.imgPhoneCall.setVisibility(View.GONE);
        }
            if (viewHolder.member.getRoleInGroupId() == 4) {
            viewHolder.imgPhoneCall.setVisibility(View.INVISIBLE);
            viewHolder.imgRemoveUser.setVisibility(View.VISIBLE);
            viewHolder.tvGenderItem.setText("Đang mời...");
        } else if (viewHolder.member.getRoleInGroupId() == 5) {
            viewHolder.tvGenderItem.setText("Yêu cầu tham gia");
            viewHolder.imgPhoneCall.setVisibility(View.GONE);
            viewHolder.imgRemoveUser.setVisibility(View.VISIBLE);
            viewHolder.imgApproveRequest.setVisibility(View.VISIBLE);
        } else if (viewHolder.member.getRoleInGroupId() == 3) {
            viewHolder.imgRemoveUser.setVisibility(View.VISIBLE);
        } else if (viewHolder.member.getRoleInGroupId() == 1) {
            viewHolder.imgPhoneCall.setVisibility(View.VISIBLE);
            viewHolder.tvGenderItem.setText("Trưởng đoàn");
        }

        viewHolder.imgPhoneCall.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + String.format("0%d", viewHolder.member.getPhone()) ));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (ActivityCompat.shouldShowRequestPermissionRationale((PlanDetailActivity)context, Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions((PlanDetailActivity)context,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            3);

                } else {
                    ActivityCompat.requestPermissions((PlanDetailActivity)context,
                            new String[]{Manifest.permission.CALL_PHONE},
                            3);
                }
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(intent);
        });

        viewHolder.imgApproveRequest.setOnClickListener((v) -> {
            mWebService.approveRequest(token, planId, viewHolder.member.getUserId()).enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    if (response.code() == 200){
                        viewHolder.tvGenderItem.setText("Thành viên");
                        viewHolder.imgPhoneCall.setVisibility(View.VISIBLE);
                        viewHolder.imgRemoveUser.setVisibility(View.VISIBLE);
                        viewHolder.imgApproveRequest.setVisibility(View.GONE);
                    } else
                        Toast.makeText(context,"Thao tác thất bại, vui lòng thử lại sau!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {
                    Toast.makeText(context,"Có lỗi xảy ra, vui lòng thử lại sau!",Toast.LENGTH_LONG).show();
                }
            });
        });

        viewHolder.imgRemoveUser.setOnClickListener((v) -> {
            alertDialogBuilder.setTitle("Loại bỏ khỏi kế hoạch");
            alertDialogBuilder.setMessage("Bạn có chắc chắn muốn loại bỏ " + viewHolder.member.getFirstname() + " khỏi kế hoạch của bạn?")
                    .setPositiveButton("Loại bỏ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mWebService.kickMember(token,planId, viewHolder.member.getUserId()).enqueue(new Callback<Plan>() {
                                @Override
                                public void onResponse(Call<Plan> call, Response<Plan> response) {
                                    if (response.code() == 200){
                                        users.remove(viewHolder.member);
                                        notifyDataSetChanged();
                                        Toast.makeText(context,"Đã loại bỏ",Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(context,"Thao tác thất bại, vui lòng thử lại sau!",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(Call<Plan> call, Throwable t) {
                                    Toast.makeText(context,"Có lỗi xảy ra, vui lòng thử lại sau!",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
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
        ImageView imgApproveRequest;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imgAvtItem = (ImageView) itemView.findViewById(R.id.imgAvtItem);
            tvNameItem = (TextView) itemView.findViewById(R.id.tvNameItem);
            tvGenderItem = (TextView) itemView.findViewById(R.id.tvGenderItem);
            imgPhoneCall = (ImageView) itemView.findViewById(R.id.imgPhoneCall);
            imgRemoveUser = (ImageView) itemView.findViewById(R.id.imgInviteState);
            imgApproveRequest = (ImageView) itemView.findViewById(R.id.imgApproveRequest);
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
