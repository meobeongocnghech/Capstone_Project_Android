package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.view.View;
import android.view.ViewGroup;

import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import in.srain.cube.views.ptr.PtrFrameLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceViewPagerAdapter extends FragmentPagerAdapter {
    private List<Place> listPlace;
    private final int PAGE_SIZE = 5;
    private int page = 1;
    private final String orderById = "id";
    private ViewGroup layout;

    public PlaceViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == listPlace.size() - 1) {
            load(listPlace.size() + 3, 1, orderById);
        }
        PlacePageLayoutFragment fragment = new PlacePageLayoutFragment();
        fragment.setPlace(listPlace.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        if (listPlace == null) {
            listPlace = new ArrayList<>();
        }
        return listPlace.size();
    }

    public void load(int size, ViewGroup layout) {
        this.layout = layout;
        load(size, 1, orderById);
    }

    private void notifyLoadComplete() {
        ((PtrFrameLayout) layout).refreshComplete();
    }


    private void load(int size, int page, String order) {
        APIService mWebService = APIUtils.getWebService();

        mWebService.getPlaces(MainActivity.user.getToken(), page, size, order).enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                List<Place> list = response.body();
                if (list != null) {
                    listPlace = list;
                    listPlace.remove(0);
                    notifyLoadComplete();
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {

            }
        });
    }
}
