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
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.plan.ChecklistActivity;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.SearchPlace;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import org.w3c.dom.Text;

import java.io.File;
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
public class PostAddActivity extends AppCompatActivity{
//
static final int OPEN_MEDIA_PICKER = 1;
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
    RecyclerView rcvImageSelect;
    ArrayList<Place> places;
    int typeId;
    APIService mWebService = APIUtils.getWebService();
    int placeId;
    int idUpdate;
    String point = "";
    TextView tvBack;
    private List<Uri> mUris = new ArrayList<>();
    private StringBuilder mBuilder = new StringBuilder();
    private ProgressDialog mProgressDialog;

    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int READ_EXTERNAL_REQUEST = 2;
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
        rcvImageSelect = (RecyclerView) findViewById(R.id.rcvImageSelect);
        tvBack = (TextView) findViewById(R.id.tvBack);

        rcvImageSelect.setLayoutManager(new LinearLayoutManager(PostAddActivity.this, LinearLayoutManager.HORIZONTAL,false));
        rcvImageSelect.setNestedScrollingEnabled(false);

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
                                point = item.getmTitle();
                                Toast.makeText(PostAddActivity.this,item.getId()+"",Toast.LENGTH_LONG).show();
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
                    Direction d = new Direction();
                    Post p = new Post();
                    p.setId(idUpdate);
                    p.setName(edtTitlePost.getText().toString());
                    p.setContent(edtContent.getText().toString());
                    d.setPlaceId(placeId);
                    Gson gs = new Gson();
                    p.setDirection(d);
                    p.setTypeId(typeId);
                    mWebService.addPost(MainActivity.user.getToken(), p).enqueue(new Callback<Post>() {
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
                    mWebService.updatePost(MainActivity.user.getToken(), idUpdate, edtTitlePost.getText().toString(),edtContent.getText().toString()).enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            Toast.makeText(PostAddActivity.this, "Cập nhật thành công",Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(PostAddActivity.this, "Cập nhật thất bại",Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(PostAddActivity.this,MainActivity.class));
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

        btnAddPhoto.setOnClickListener((v) -> {
            Intent intent = new Intent(this, Gallery.class);
            // Set the title
            intent.putExtra("title", "Chọn ảnh");
            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
            intent.putExtra("mode", 2);
            intent.putExtra("maxSelection", 5); // Optional
            startActivityForResult(intent, OPEN_MEDIA_PICKER);
        });



    }

    private ArrayList<SearchPlace> createSampleData(){
        ArrayList<SearchPlace> items = new ArrayList<>();
        for (Place p : places) {
            items.add(new SearchPlace(p.getName(), p.getId()));
        }
        return items;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        ImageAddAdapter imageAddAdapter = new ImageAddAdapter();
        if (requestCode == OPEN_MEDIA_PICKER) {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                    data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mUris.add(uri);
                    mBuilder.append(i + "-")
                            .append(getRealPathFromURI(uri))
                            .append("\n");
//                    mTextInput.setText(mBuilder.toString());
                }

                // Sau khi get đc data thì ta upload thôi
                uploadFiles();
            }
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                if (selectionResult.size() > 0){
                    imageAddAdapter.setContext(this);
                    imageAddAdapter.setListImg(selectionResult);
                    cbImageSelect.setChecked(true);
                    btnAddPhoto.setText("Bạn đã chọn "+ (selectionResult.size()) +" ảnh");
                } else {
                    imageAddAdapter.setListImg(selectionResult);
                    cbImageSelect.setChecked(false);
                    btnAddPhoto.setText("Chọn ảnh");
                }

            } else {
                cbImageSelect.setChecked(false);
                btnAddPhoto.setText("Chọn ảnh");
            }
            rcvImageSelect.setAdapter(imageAddAdapter);
        }
    }

    public void uploadFiles() {
        if (mUris.isEmpty()) {
            Toast.makeText(this, "Please select some image", Toast.LENGTH_SHORT).show();
            return;
        }
        // Hàm call api sẽ mất 1 thời gian nên mình show 1 dialog nhé.
        showProgress();
        // Trong retrofit 2 để upload file ta sử dụng Multipart, khai báo 1 MultipartBody.Part
        // uploaded_file là key mà mình đã định nghĩa trong khi khởi tạo server

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (int i = 0; i < mUris.size(); i++) {
            Uri uri = mUris.get(i);
            File file = new File(getRealPathFromURI(uri));
            // Khởi tạo RequestBody từ những file đã được chọn
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    file);
            // Add thêm request body vào trong builder
            builder.addFormDataPart("uploaded_file", file.getName(), requestBody);
        }

        MultipartBody requestBody = builder.build();

    }
    private void dissmissDialog() {
        mProgressDialog.dismiss();
    }

    private void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
        }
        mProgressDialog.show();
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
