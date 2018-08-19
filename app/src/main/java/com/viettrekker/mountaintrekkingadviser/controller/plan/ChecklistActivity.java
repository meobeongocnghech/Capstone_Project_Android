package com.viettrekker.mountaintrekkingadviser.controller.plan;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
    RecyclerView rcvChecklistItem;
    MaterialButton btnEditCheckList;
    ImageView imgBackInCheckList;
    EditText edtAddChecklist;
    ImageView imgAddChecklist;
    APIService mWebService;
    int id;
    String state;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        NewPlanActivity newPlanActivity = new NewPlanActivity();
        mWebService = APIUtils.getWebService();
        state = getIntent().getStringExtra("state") == null ? "" : getIntent().getStringExtra("state");
        id = getIntent().getIntExtra("id", -1);
        rcvChecklistItem = (RecyclerView) findViewById(R.id.rcvChecklistItem);
        btnEditCheckList = (MaterialButton) findViewById(R.id.btnEditCheckList);
        imgBackInCheckList = (ImageView) findViewById(R.id.imgBackInCheckList);
        edtAddChecklist = (EditText) findViewById(R.id.edtAddChecklist);
        imgAddChecklist = (ImageView) findViewById(R.id.imgAddChecklist);

        imgAddChecklist.setVisibility(View.GONE);
        edtAddChecklist.setVisibility(View.GONE);
        rcvChecklistItem.setLayoutManager(new LinearLayoutManager(ChecklistActivity.this));
        ChecklistAdapter checklistAdapter = new ChecklistAdapter(ChecklistActivity.this);
        if (id != -1){
            mWebService.getPlanById(MainActivity.user.getToken(), id).enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    Plan plan = response.body();
                    List<ChecklistItem> items = plan.getChecklist().getItems();
                    checklistAdapter.setList(items);
                    checklistAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {

                }
            });
        }
        if (state.equalsIgnoreCase("new")){
            btnEditCheckList.setText("Xong");
            checklistAdapter.setList(newPlanActivity.checkLists);
            checklistAdapter.notifyDataSetChanged();
            List<ChecklistItem> items = new ArrayList<>();
            imgAddChecklist.setVisibility(View.VISIBLE);
            edtAddChecklist.setVisibility(View.VISIBLE);
            imgAddChecklist.setOnClickListener(new View.OnClickListener() {
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
            btnEditCheckList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        }

        rcvChecklistItem.setNestedScrollingEnabled(false);
        rcvChecklistItem.setAdapter(checklistAdapter);

    }
}
