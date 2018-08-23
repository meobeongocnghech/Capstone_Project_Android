package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.LoginActivity;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.plan.ChecklistActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.PlanLocation;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.util.DateTimeUtils;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

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
    int typeId;
    APIService mWebService = APIUtils.getWebService();
    int placeId;
    int idUpdate;
    String point = "";
    Direction d;

    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int READ_EXTERNAL_REQUEST = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addPostToolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnPost = (MaterialButton) findViewById(R.id.btnPost);
        tvPostUserName = (TextView) findViewById(R.id.tvPostUserName);
        spnCategory = (Spinner) findViewById(R.id.spnCategory);
        edtTitlePost = (EditText) findViewById(R.id.edtTitlePost);
        edtContent = (EditText) findViewById(R.id.edtContent);
        btnAddPhoto = (MaterialButton) findViewById(R.id.btnAddPhoto);
        btnLocation = (MaterialButton) findViewById(R.id.btnLocation);
        btnPlan = (MaterialButton) findViewById(R.id.btnPlan);
        rcvImageSelect = (RecyclerView) findViewById(R.id.rcvImageSelect);

        rcvImageSelect.setLayoutManager(new LinearLayoutManager(PostAddActivity.this, LinearLayoutManager.HORIZONTAL, false));

        imageAddAdapter = new ImageAddAdapter();
        imageAddAdapter.setContext(this);

        places = new ArrayList<>();
        btnLocation.setText("Chọn địa điểm");
        placeId = -1;
        typeId = 4;
        tvPostUserName.setText(Session.getUser(getApplicationContext()).getFirstName()+ " " +Session.getUser(getApplicationContext()).getLastName());
        idUpdate = getIntent().getIntExtra("id", 0);
        if (idUpdate > 0) {
            edtTitlePost.setText(getIntent().getStringExtra("title"));
            edtContent.setText(getIntent().getStringExtra("content"));
            btnPost.setText("Sửa");
        }
        mWebService.searchPlace(Session.getToken(PostAddActivity.this), 1, 100, "id", "").enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                places = response.body();
                if (places != null)
                    places.remove(0);
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
                                Toast.makeText(PostAddActivity.this, item.getId() + "", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostAddActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                String content = edtContent.getText().toString();
                String title = edtTitlePost.getText().toString();
                if (placeId != -1) {
                    typeId = 1;
                    d = new Direction();
                    d.setPlaceId(placeId);
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
                    Post p = new Post();
                    p.setId(idUpdate);
                    p.setName(edtTitlePost.getText().toString());
                    p.setContent(edtContent.getText().toString());
                    p.setDirection(d);
                    p.setTypeId(typeId);

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("name", edtTitlePost.getText().toString());
                    builder.addFormDataPart("content", edtContent.getText().toString());

                    Gson gson = new Gson();
                    builder.addFormDataPart("direction", gson.toJson(d));

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
                            Toast.makeText(PostAddActivity.this, "Tạo thành công", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PostAddActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
                            Toast.makeText(PostAddActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(PostAddActivity.this, "Cập nhật thất bại", Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(PostAddActivity.this,MainActivity.class));
                        }
                    });
                }

            }
        });

        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PostAddActivity.this, "Pending this action...", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddPhoto.setOnClickListener((v) -> {
            Intent intent = new Intent(this, Gallery.class);
            // Set the title
            intent.putExtra("title", "Chọn ảnh");
            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
            intent.putExtra("mode", 2);
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
                if (selectionResult.size() > 0) {
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
}
