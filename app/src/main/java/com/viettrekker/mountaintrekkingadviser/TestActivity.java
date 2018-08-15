package com.viettrekker.mountaintrekkingadviser;

import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import retrofit2.Call;
import retrofit2.Response;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayout.Spec;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.viettrekker.mountaintrekkingadviser.customview.PtrLoadingHeader;
import com.viettrekker.mountaintrekkingadviser.customview.RentalsSunHeaderView;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

public class TestActivity extends AppCompatActivity {

    private String mUrl = "http://img4.duitang.com/uploads/blog/201407/07/20140707113856_hBf3R.thumb.jpeggg";
    private long mStartLoadingTime = -1;
    private boolean mImageHasLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        APIService s = APIUtils.getWebService();
        s.removeComment("1|eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImxpbmhudEBnbWFpbC5jb20iLCJwYXNzd29yZCI6IiQyYiQwOCRoSkU3VUtQTXlXcHdKYmpqSE5kR3Z1NzBFUk1DLlZDZDlTZnhZOHFMMUlxa1M2ZFNtd0dneSIsImlhdCI6MTUzMDkzOTU5OSwiZXhwIjoxNTc0MTM5NTk5fQ.Ih0keKGJ2XK7TE9icXcY7qy87B-39VDOC5YT70Ey_E8",
                100, 325).enqueue(new retrofit2.Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                System.out.println("khong loi roi " + response.body().toString());
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println("loi roi " + t.getMessage());
            }
        });
}}
