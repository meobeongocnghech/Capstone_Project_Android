package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAddActivity extends AppCompatActivity{
//
    ImageView imgPostAvatar;
    TextView tvPostUserName;
    Spinner spnCategory;
    EditText edtTitlePost;
    EditText edtContent;
    MaterialButton btnAddPhoto;
    MaterialButton btnLocation;
    MaterialButton btnPlan;
    CheckBox cbImageSelect;
    CheckBox cbLocationSelect;
    CheckBox cbPlanSelect;
    TextView tvPostButton;
    ArrayList<Place> places;
    int typeId;
    APIService mWebService = APIUtils.getWebService();
    int placeId;
    int idUpdate;
    String titleUpdate;
    String contentUpdate;
    TextView tvBack;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);
        imgPostAvatar = (ImageView) findViewById(R.id.imgPostAvatar);
        tvPostUserName = (TextView) findViewById(R.id.tvPostUserName);
        spnCategory = (Spinner) findViewById(R.id.spnCategory);
        edtTitlePost = (EditText) findViewById(R.id.edtTitlePost);
        edtContent = (EditText) findViewById(R.id.edtContent);
        btnAddPhoto = (MaterialButton) findViewById(R.id.btnAddPhoto);
        btnLocation = (MaterialButton) findViewById(R.id.btnLocation);
        btnPlan = (MaterialButton) findViewById(R.id.btnPlan);
        cbImageSelect = (CheckBox) findViewById(R.id.cbImageSelect);
        cbLocationSelect = (CheckBox) findViewById(R.id.cbLocationSelect);
        cbPlanSelect = (CheckBox) findViewById(R.id.cbPlanSelect);
        tvPostButton = (TextView) findViewById(R.id.tvPostButton);
        tvBack = (TextView) findViewById(R.id.tvBack);
        places = new ArrayList<>();
        btnLocation.setText("Chọn địa điểm");
        cbLocationSelect.setChecked(false);
        placeId = -1;
        typeId = 4;
        idUpdate = getIntent().getIntExtra("id", 0);
        if (idUpdate > 0){
            edtTitlePost.setText(getIntent().getStringExtra("title"));
            edtContent.setText(getIntent().getStringExtra("content"));
            tvPostButton.setText("Sửa");
        }
        mWebService.searchPlace(MainActivity.user.getToken(),1,100,"id","").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                places = response.body();
                places.remove(0);
            }

            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {

            }
        });

        cbLocationSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    btnLocation.setText("Chọn địa điểm");
                    placeId = -1;
                }
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(PostAddActivity.this, "Tìm kiếm địa điểm",
                        "Nhập tên địa điểm có dấu...", null, createSampleData(),
                        new SearchResultListener<SearchPlace>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,SearchPlace item, int position) {
                                btnLocation.setText(item.getTitle());
                                cbLocationSelect.setChecked(true);
                                placeId = item.getId();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        tvPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostAddActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                String content = edtContent.getText().toString();
                String title = edtTitlePost.getText().toString();
                if (cbLocationSelect.isChecked()) {
                    typeId = 1;
                }
                if (title.isEmpty() || content.isEmpty()){
                    alertDialogBuilder.setTitle("Cảnh báo");
                    alertDialogBuilder.setMessage("Vui lòng điền đầy đủ nội dung bài viết")
                            .setCancelable(false)
                            .setNegativeButton("Đồng ý",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else if  (!tvPostButton.getText().equals("Sửa")){
                    mWebService.addPost(MainActivity.user.getToken(), typeId, edtContent.getText().toString(), edtTitlePost.getText().toString()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            Toast.makeText(PostAddActivity.this, "Đăng bài thành công!",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PostAddActivity.this,MainActivity.class));
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {

                        }
                    });
                }else {
                    mWebService.updatePost(MainActivity.user.getToken(), idUpdate,titleUpdate,contentUpdate).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            Toast.makeText(PostAddActivity.this, "Cập nhật thành công",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PostAddActivity.this,MainActivity.class));
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(PostAddActivity.this, "Cập nhật thất bại",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PostAddActivity.this,MainActivity.class));
                        }
                    });
                }

            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostAddActivity.this,"Pedding this action...",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private ArrayList<SearchPlace> createSampleData(){
        ArrayList<SearchPlace> items = new ArrayList<>();
        for (Place p : places) {
            items.add(new SearchPlace(p.getName(), p.getId()));
        }
        return items;
    }
}
