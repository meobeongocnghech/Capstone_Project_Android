package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.google.gson.Gson;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.File;
import java.util.ArrayList;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAddActivity extends AppCompatActivity {
    //
    static final int OPEN_MEDIA_PICKER = 1;
    TextView tvPostUserName;
    Spinner spnCategory;
    EditText edtTitlePost;
    EditText edtContent;
    MaterialButton btnAddPhoto;
    MaterialButton btnLocation;
    MaterialButton btnPlan;
    RecyclerView rcvImageSelect;
    MaterialButton btnPost;
    ArrayList<Place> places;
    ImageAddAdapter imageAddAdapter;
    ImageView imgPostAvatar;
    int typeId;
    APIService mWebService = APIUtils.getWebService();
    int placeId = 0;
    int idUpdate;
    String point = "";
    Direction direction;
    int planId = 0;
    AlertDialog.Builder alertDialogBuilder;
    private HashTagHelper helper;

    private boolean isPostPlan = false;
    private String planName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);
        alertDialogBuilder = new AlertDialog.Builder(PostAddActivity.this);
        helper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.addPostToolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgPostAvatar = (ImageView) findViewById(R.id.imgPostAvatar);
        btnPost = (MaterialButton) findViewById(R.id.btnPost);
        tvPostUserName = (TextView) findViewById(R.id.tvPostUserName);
        spnCategory = (Spinner) findViewById(R.id.spnCategory);
        edtTitlePost = (EditText) findViewById(R.id.edtTitlePost);
        edtContent = (EditText) findViewById(R.id.edtContent);
        helper.handle(edtContent);
        btnAddPhoto = (MaterialButton) findViewById(R.id.btnAddPhoto);
        btnLocation = (MaterialButton) findViewById(R.id.btnLocation);
        btnPlan = (MaterialButton) findViewById(R.id.btnPlan);
        rcvImageSelect = (RecyclerView) findViewById(R.id.rcvImageSelect);

        isPostPlan = getIntent().getBooleanExtra("isPostPlan", false);
        if(isPostPlan) {
            planName = getIntent().getStringExtra("planName");
            planId = getIntent().getIntExtra("planId", 0);
            btnLocation.setVisibility(View.GONE);
            btnPlan.setText(planName);
            btnPlan.setFocusable(false);
            btnPlan.setClickable(false);
        } else {
            btnPlan.setVisibility(View.GONE);
        }

        rcvImageSelect.setLayoutManager(new LinearLayoutManager(PostAddActivity.this, LinearLayoutManager.HORIZONTAL, false));

        imageAddAdapter = new ImageAddAdapter();
        imageAddAdapter.setContext(this);
        if (!Session.getAvatarPath(this).isEmpty()) {
            GlideApp.with(this)
                    .load(APIUtils.BASE_URL_API + Session.getAvatarPath(this).substring(4))
                    .apply(RequestOptions.circleCropTransform())
                    .fallback(R.drawable.avatar_default)
                    .placeholder(R.drawable.avatar_default)
                    .into(imgPostAvatar);
        }

        places = new ArrayList<>();
        btnLocation.setText("Chọn địa điểm");
        typeId = 4;
        tvPostUserName.setText(Session.getUser(getApplicationContext()).getFirstName() + " " + Session.getUser(getApplicationContext()).getLastName());
        idUpdate = getIntent().getIntExtra("id", 0);
        if (idUpdate > 0) {
            edtTitlePost.setText(getIntent().getStringExtra("title"));
            edtContent.setText(getIntent().getStringExtra("content"));
            btnPost.setText("Sửa");
            btnAddPhoto.setVisibility(View.GONE);
            btnPlan.setVisibility(View.GONE);
            btnLocation.setVisibility(View.GONE);
        }
        mWebService.searchPlace(Session.getToken(PostAddActivity.this), 1, 100, "id", "").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                if (response.code() == 200){
                    places = response.body();
                    if (places != null)
                        places.remove(0);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {

            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(PostAddActivity.this, "Tìm kiếm địa điểm",
                        "Nhập tên địa điểm có dấu...", null, createSampleData(),
                        new SearchResultListener<SearchPlace>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog, SearchPlace item, int position) {
                                btnLocation.setText(item.getTitle());
                                placeId = item.getId();
                                point = item.getmTitle();
                                btnPlan.setText("Kế hoạch");
                                btnPlan.setClickable(false);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = edtContent.getText().toString();
                String title = edtTitlePost.getText().toString();
                if (placeId > 0) {
                    typeId = 1;
                    direction = new Direction();
                    direction.setPlaceId(placeId);
                } else if (planId > 0) {
                    typeId = 2;
                } else {
                    typeId = 4;
                }
                if (title.isEmpty() || content.isEmpty()) {
                    alertDialogBuilder.setTitle("Cảnh báo");
                    alertDialogBuilder.setMessage("Vui lòng điền đầy đủ nội dung bài viết")
                            .setCancelable(false)
                            .setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else if (!btnPost.getText().equals("Sửa")) {
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("name", edtTitlePost.getText().toString());
                    builder.addFormDataPart("content", edtContent.getText().toString());
                    builder.addFormDataPart("typeId", typeId + "");

                    Gson gson = new Gson();
                    if (planId > 0) {
                        builder.addFormDataPart("directionId", planId + "");
                    } else if (placeId > 0) {
                        builder.addFormDataPart("direction", gson.toJson(direction));
                    }

                    for (String s : imageAddAdapter.getListImg()) {
                        File file = new File(s);
                        builder.addFormDataPart("medias", "medias", RequestBody.create(MediaType.parse("image"), file));
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(PostAddActivity.this, R.style.FullDialogStyle);
                    progressDialog.setTitle("Đăng bài");
                    progressDialog.setMessage("Đang thực hiện ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mWebService.addPostWithImages(Session.getToken(PostAddActivity.this), builder.build()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            progressDialog.dismiss();
//                            Toast.makeText(PostAddActivity.this, "Tạo thành công", Toast.LENGTH_LONG).show();
                            if (isPostPlan) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                onBackPressed();
                            } else {
                                Intent intent = new Intent(PostAddActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(PostAddActivity.this, "Xảy ra lỗi", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(PostAddActivity.this, R.style.FullDialogStyle);
                    progressDialog.setTitle("Cập nhật");
                    progressDialog.setMessage("Đang thực hiện ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mWebService.updatePost(Session.getToken(PostAddActivity.this), idUpdate, edtTitlePost.getText().toString(), edtContent.getText().toString()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            progressDialog.dismiss();
//                            Toast.makeText(PostAddActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(PostAddActivity.this, "Cập nhật thất bại", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    });
                }

            }
        });
//
//        btnPlan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SimpleSearchDialogCompat(PostAddActivity.this, "Tìm kiếm kế hoạch của bạn",
//                        "Nhập tên kế hoạch có dấu...", null, createPlanData(),
//                        new SearchResultListener<PlanOwn>() {
//                            @Override
//                            public void onSelected(BaseSearchDialogCompat dialog, PlanOwn item, int position) {
//                                btnPlan.setText(item.getmTitle());
//                                btnLocation.setText("Địa điểm");
//                                btnLocation.setClickable(false);
//                                planId = item.getId();
//                                dialog.dismiss();
//                            }
//                        }).show();
//            }
//        });

        btnAddPhoto.setOnClickListener((v) -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PostAddActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(PostAddActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            3);

                } else {
                    ActivityCompat.requestPermissions(PostAddActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
            Intent intent = new Intent(this, Gallery.class);
            // Set the title
            intent.putExtra("title", "Chọn ảnh");
            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
            intent.putExtra("mode", 2);
            intent.putExtra("maxSelection", 10);
            startActivityForResult(intent, OPEN_MEDIA_PICKER);
        });


    }

    private ArrayList<SearchPlace> createSampleData() {
        ArrayList<SearchPlace> items = new ArrayList<>();
        if (places != null) {
            for (Place p : places) {
                items.add(new SearchPlace(p.getName(), p.getId()));
            }
        }

        return items;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                if (selectionResult != null && selectionResult.size() > 0) {
                    imageAddAdapter.addItem(selectionResult);
                    btnAddPhoto.setText("Bạn đã chọn " + (imageAddAdapter.getItemCount()) + " ảnh");
                } else {
                    btnAddPhoto.setText("Chọn ảnh");
                }

            } else {
                btnAddPhoto.setText("Chọn ảnh");
            }
            rcvImageSelect.setVisibility(View.VISIBLE);
            rcvImageSelect.setAdapter(imageAddAdapter);
        }
    }

    public void hideRcv() {
        rcvImageSelect.setVisibility(View.GONE);
    }

    public void setTotalCountImages() {
        if (imageAddAdapter.getItemCount() > 0) {
            btnAddPhoto.setText("Bạn đã chọn " + imageAddAdapter.getItemCount() + " ảnh");
        } else {
            btnAddPhoto.setText("Chọn ảnh");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

//    private ArrayList<PlanOwn> createPlanData() {
//        ArrayList<PlanOwn> items = new ArrayList<>();
//        mWebService.getListPlan(Session.getToken(PostAddActivity.this), 1, 20, "id").enqueue(new Callback<List<Plan>>() {
//            @Override
//            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
//                if (response.code() == 200){
//                    if (response.body().size() > 1) {
//                        List<Plan> p = response.body();
//                        p.remove(0);
//                        for (Plan pl : p) {
//                            PlanOwn po = new PlanOwn(pl.getGroup().getName(), pl.getId());
//                            items.add(po);
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<List<Plan>> call, Throwable t) {
//
//            }
//        });
//        return items;
//    }
}
