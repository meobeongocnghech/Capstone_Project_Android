package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.PlanLocation;
import com.viettrekker.mountaintrekkingadviser.model.SearchMember;
import com.viettrekker.mountaintrekkingadviser.model.TimeLines;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanDetailActivity  extends AppCompatActivity{

    TextView tvStartPlace;
    TextView tvEndPlace;
    TextView tvDateStart;
    TextView tvTimeStart;
    TextView tvDateEnd;
    TextView tvTimeEnd;
    TextView tvDurationDate;
    APIService mWebService;
    RecyclerView rcvListMembersPlan;
    RelativeLayout layoutViewMember;
    Button btnTimeLines;
    Button btnCheckList;
    TextView tvDistance;
    ImageView imgBackIcon;
    TextView tvPlanName;
    MaterialButton btnEditablePlan;
    TextView tvMemberCount;
    TextView tvStatePlan;
    ImageView imgAddMember;
    List<User> allMember;
    List<Member> members;
    int id;

    public PlanDetailActivity() {
    }

    public int getId() {
        return id;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_plan_detail);
        tvStartPlace = (TextView) findViewById(R.id.tvStartPlace);
        tvEndPlace = (TextView) findViewById(R.id.tvEndPlace);
        tvDateStart = (TextView) findViewById(R.id.tvDateStart);
        tvTimeStart = (TextView) findViewById(R.id.tvTimeStart);
        tvDateEnd = (TextView) findViewById(R.id.tvDateEnd);
        tvTimeEnd = (TextView) findViewById(R.id.tvTimeEnd);
        tvDurationDate = (TextView) findViewById(R.id.tvDurationDate);
        layoutViewMember = (RelativeLayout) findViewById(R.id.layoutViewMember);
        btnTimeLines = (Button) findViewById(R.id.btnTimeLines);
        btnCheckList = (Button) findViewById(R.id.btnCheckList);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        imgBackIcon = (ImageView) findViewById(R.id.imgBackIcon);
        tvPlanName = (TextView) findViewById(R.id.tvPlanName);
        btnEditablePlan = (MaterialButton) findViewById(R.id.btnEditablePlan);
        tvMemberCount = (TextView) findViewById(R.id.tvMemberCount);
        imgAddMember = (ImageView) findViewById(R.id.imgAddMember);
        tvStatePlan = (TextView) findViewById(R.id.tvStatePlan);
        mWebService = APIUtils.getWebService();
        rcvListMembersPlan = (RecyclerView) findViewById(R.id.rcvListMembersPlan);
        rcvListMembersPlan.setLayoutManager(new LinearLayoutManager(PlanDetailActivity.this));
        MembersListAdapter membersListAdapter = new MembersListAdapter(PlanDetailActivity.this);
        rcvListMembersPlan.setVisibility(View.GONE);
        members = new ArrayList<>();

        //getPlan
        id = getIntent().getIntExtra("id",0);
        mWebService.getPlanById(MainActivity.user.getToken(), id).enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                    Plan plan = response.body();
                    if (plan.getState() == 1){
                        tvStatePlan.setText("Đang thực hiện");
                        tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorOrange));
                    } else if (plan.getState() == -1){
                        tvStatePlan.setText("Kết thúc");
                        tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorGray));
                    } else {
                        tvStatePlan.setText("Sẵn sàng");
                        tvStatePlan.setTextColor(PlanDetailActivity.this.getResources().getColor(R.color.colorPrimary));
                    }
                try {
                    tvDateStart.setText(DateTimeUtils.parseStringDate(DateTimeUtils.changeTimeToLocale(plan.getStartTime())));
                    tvTimeStart.setText(DateTimeUtils.parseStringTime(DateTimeUtils.changeTimeToLocale(plan.getStartTime())));
                    tvDateEnd.setText(DateTimeUtils.parseStringDate(DateTimeUtils.changeTimeToLocale(plan.getFinishTime())));
                    tvTimeEnd.setText(DateTimeUtils.parseStringTime(DateTimeUtils.changeTimeToLocale(plan.getFinishTime())));
                    Date d1 = DateTimeUtils.changeTimeToLocale(plan.getStartTime());
                    Date d2 = DateTimeUtils.changeTimeToLocale(plan.getFinishTime());
                    tvDurationDate.setText(DateTimeUtils.caculatorStringTime(d1,d2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvPlanName.setText(plan.getGroup().getName());
                List<Member> members = plan.getGroup().getMembers();
                List<User> users = new ArrayList<>();
                for (Member m: members) {
                    mWebService.getUserById(MainActivity.user.getToken(),m.getUserId()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
//                            m.setFirstName(response.body().getFirstName());
//                            m.setLastName(response.body().getLastName());
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                membersListAdapter.setUsers(members);
                membersListAdapter.notifyDataSetChanged();
                tvMemberCount.setText(members.size() + " người");

                Type type = new TypeToken<ArrayList<PlanLocation>>(){}.getType();
                Gson gson = new Gson();
                List<PlanLocation> locs = gson.fromJson(plan.getDirection().getPlanLocation(),type);
                PlanLocation startLoc = locs.get(0);
                PlanLocation endLoc = locs.get(1);
                tvStartPlace.setText(startLoc.getName());
                tvEndPlace.setText(endLoc.getName());
                Location start = new Location("");
                start.setLatitude(startLoc.getLat());
                start.setLongitude(startLoc.getLng());
                Location end = new Location("");
                end.setLatitude(endLoc.getLat());
                end.setLongitude(endLoc.getLng());
                float dist =  start.distanceTo(end)/1000;
                tvDistance.setText("Khoảng " + (double) Math.floor(dist * 10) / 10 + "km/chiều");

            }


            @Override
            public void onFailure(Call<Plan> call, Throwable t) {

            }
        });
        imgAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcvListMembersPlan.getVisibility() == View.GONE){
                    rcvListMembersPlan.setVisibility(View.VISIBLE);
                }
                new SimpleSearchDialogCompat(PlanDetailActivity.this, "Tìm kiếm bạn đồng hành",
                        "Nhập tên có dấu...", null, createMemberData(),
                        new SearchResultListener<SearchMember>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchMember searchMember, int i) {
                                Member mem = new Member();
                                mem.setRoleInGroupId(4);
                                mem.setUserId(searchMember.getId());
//                                mem.setFirstName(searchMember.getFirstName());
//                                mem.setLastName(searchMember.getLastName());
                                mem.setVehicule("");
                                if (!checkMemberExist(mem)){
                                    members.add(mem);
                                    membersListAdapter.notifyDataSetChanged();
                                    tvMemberCount.setText(members.size() + " người");
                                } else {
                                    Toast.makeText(PlanDetailActivity.this,"Bạn không thể mời thêm thành viên này!",Toast.LENGTH_SHORT).show();
                                }

                                baseSearchDialogCompat.dismiss();
                            }
                        }).show();

            }
        });
        allMember = new ArrayList<>();
        mWebService.searchMember(MainActivity.user.getToken(),"").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                allMember = response.body();
                allMember.remove(0);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
        rcvListMembersPlan.setNestedScrollingEnabled(false);
        rcvListMembersPlan.setAdapter(membersListAdapter);
        layoutViewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rcvListMembersPlan.getVisibility() == View.GONE){
                    rcvListMembersPlan.setVisibility(View.VISIBLE);
                } else {
                    rcvListMembersPlan.setVisibility(View.GONE);
                }
            }
        });
        btnTimeLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanDetailActivity.this, TimeLinesActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        btnCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanDetailActivity.this, ChecklistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }
    private ArrayList<SearchMember> createMemberData(){
        ArrayList<SearchMember> items = new ArrayList<>();
        for (User p : allMember) {
            String title = p.getFirstName() + " " + p.getLastName();
            items.add(new SearchMember(title,  p.getId(), p.getFirstName(), p.getLastName()));
        }
        return items;
    }
    private boolean checkMemberExist(Member m){
        for (Member memb: members) {
            if (memb.getUserId()==m.getUserId())
                return true;
        }
        return false;
    }
}
