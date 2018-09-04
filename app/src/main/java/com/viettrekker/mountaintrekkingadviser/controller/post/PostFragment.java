package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.util.Session;

public class PostFragment extends Fragment {

    private NestedScrollView layout;
    private SwitchCompat switchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        layout = (NestedScrollView) view.findViewById(R.id.postScrollView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchView = (SwitchCompat) view.findViewById(R.id.swtMode);
        Chip chip = (Chip) view.findViewById(R.id.postHint);

        loadNewsfeedData();
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    loadPlaceData();
                    ((MainActivity) getActivity()).setTitle("Địa điểm");
                    loadChipAnimation(chip, "Địa điểm");
                } else {
                    loadNewsfeedData();
                    ((MainActivity) getActivity()).setTitle("Bảng tin");
                    loadChipAnimation(chip, "Bảng tin");
                }
            }
        });

        layout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int i, int i1, int i2, int i3) {
                if (i1 > 0 && !switchView.isChecked()) {
                    NewsFeedFragment fragment = null;
                    for (Fragment fragment1 : getFragmentManager().getFragments()) {
                        if (fragment1.getTag() != null && fragment1.getTag().equalsIgnoreCase("newsfeed")) {
                            fragment = (NewsFeedFragment) fragment1;
                        }
                    }
                    LinearLayoutManager layoutManager = fragment.getmLayoutManager();
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((i1 >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                i1 > i3) {

                            int visibleItemCount = layoutManager.getChildCount();
                            int totalItemCount = layoutManager.getItemCount();
                            int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                fragment.incrementalLoad();
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadPlaceData() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        PagePlaceFragment placeFragment = new PagePlaceFragment();
        placeFragment.setToken(Session.getToken(getActivity()));

        fragmentTransaction.replace(R.id.postFragment, placeFragment, "place");
        fragmentTransaction.commitAllowingStateLoss();
        ((MainActivity) getActivity()).hideAdd();
    }

    private void loadNewsfeedData() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
        newsFeedFragment.setUserId(Session.getUserId(getActivity()));
        newsFeedFragment.setToken(Session.getToken(getActivity()));

        fragmentTransaction.replace(R.id.postFragment, newsFeedFragment, "newsfeed");
        fragmentTransaction.commitAllowingStateLoss();
        ((MainActivity) getActivity()).showAdd();
    }

    private void loadChipAnimation(Chip chip, String hint) {
        chip.setVisibility(View.VISIBLE);
        chip.setAlpha(1f);
        chip.setText(hint);
        chip.animate().alpha(1f).setDuration(200).start();
        chip.animate().alpha(0f).setDuration(200).setStartDelay(700).start();
    }

    public void scrollToTop() {
        layout.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100, 100, 0.5f, 5, 0, 1, 1, 0, 0));
        layout.fling(0);
        layout.smoothScrollTo(0, 0);
    }

    public int getCurrentScrollY() {
        return layout.getScrollY();
    }

    public void refreshData() {
        if (switchView.isChecked()) {
            loadPlaceData();
        } else {
            loadNewsfeedData();
        }
    }
}
