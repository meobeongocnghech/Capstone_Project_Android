package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.ChecklistItem;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChecklistActivity extends AppCompatActivity {
    private RecyclerView rcvChecklistItem;
    private MaterialButton btnEditCheckList;
    private EditText edtAddChecklist;
    private MaterialButton btnAddChecklist;
    private APIService mWebService;
    private int id;
    private String state;
    private Plan plan;
    private String token;
    private List<ChecklistItem> items;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChecklist);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        token = getIntent().getStringExtra("token");

        NewPlanActivity newPlanActivity = new NewPlanActivity();
        mWebService = APIUtils.getWebService();
        state = getIntent().getStringExtra("state") == null ? "" : getIntent().getStringExtra("state");
        id = getIntent().getIntExtra("id", -1);
        rcvChecklistItem = (RecyclerView) findViewById(R.id.rcvChecklistItem);
        btnEditCheckList = (MaterialButton) findViewById(R.id.btnEditCheckList);
        edtAddChecklist = (EditText) findViewById(R.id.edtAddChecklist);
        btnAddChecklist = (MaterialButton) findViewById(R.id.btnAddChecklist);

        btnAddChecklist.setVisibility(View.GONE);
        edtAddChecklist.setVisibility(View.GONE);
        rcvChecklistItem.setLayoutManager(new LinearLayoutManager(ChecklistActivity.this));
        ChecklistAdapter checklistAdapter = new ChecklistAdapter(ChecklistActivity.this);
        if (id != -1){
            mWebService.getPlanById(MainActivity.user.getToken(), id).enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    plan = response.body();
                    items = plan.getChecklist().getItems();
                    checklistAdapter.setList(items);
                    checklistAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {

                }
            });
        }
        if (state.equalsIgnoreCase("new")){
            btnEditCheckList.setVisibility(View.GONE);
            checklistAdapter.setList(newPlanActivity.checkLists);
            checklistAdapter.notifyDataSetChanged();
            List<ChecklistItem> items = new ArrayList<>();
            btnAddChecklist.setVisibility(View.VISIBLE);
            edtAddChecklist.setVisibility(View.VISIBLE);
            btnAddChecklist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChecklistItem c = new ChecklistItem();
                    c.setContent(edtAddChecklist.getText().toString());
                    c.setState(0);
                    newPlanActivity.checkLists.add(c);
                    checklistAdapter.setList(newPlanActivity.checkLists);
                    checklistAdapter.notifyDataSetChanged();
                }
            });
        } else {
            btnAddChecklist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChecklistItem c = new ChecklistItem();
                    c.setContent(edtAddChecklist.getText().toString());
                    c.setState(0);
                    items.add(c);
                    checklistAdapter.setList(items);
                    checklistAdapter.notifyDataSetChanged();
                }
            });

            btnEditCheckList.setOnClickListener((v) -> {
                if (btnEditCheckList.getText().toString().equalsIgnoreCase("sửa")) {
                    btnEditCheckList.setText("Xong");
                    btnAddChecklist.setVisibility(View.VISIBLE);
                    edtAddChecklist.setVisibility(View.VISIBLE);
                } else {
                    btnEditCheckList.setText("Sửa");
                    btnAddChecklist.setVisibility(View.GONE);
                    edtAddChecklist.setVisibility(View.GONE);

                    plan.getChecklist().setItems(items);
                    mWebService.updatePlan(token, plan).enqueue(new Callback<Plan>() {
                        @Override
                        public void onResponse(Call<Plan> call, Response<Plan> response) {
                            Toast.makeText(ChecklistActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Plan> call, Throwable t) {
                            Toast.makeText(ChecklistActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }

        rcvChecklistItem.setNestedScrollingEnabled(false);
        rcvChecklistItem.setAdapter(checklistAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
