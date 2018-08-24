package com.viettrekker.mountaintrekkingadviser.controller.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.bumptech.glide.request.RequestOptions;
import com.viettrekker.mountaintrekkingadviser.GlideApp;
import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PlaceDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.post.PostDetailActivity;
import com.viettrekker.mountaintrekkingadviser.controller.profile.ProfileMemberActivity;
import com.viettrekker.mountaintrekkingadviser.util.LocalDisplay;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.text.ParseException;
import java.util.List;


public class SlidingSearchResultsFragment extends BaseExampleFragment {
    private final String TAG = SlidingSearchResultsFragment.class.getSimpleName();

    private SearchFragment searchFragment;
    private String token;
    private int userId;

    public void setSearchFragment(SearchFragment searchFragment) {
        this.searchFragment = searchFragment;
    }

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private FloatingSearchView mSearchView;

    //private RecyclerView mSearchResultsList;

    private ImageButton search;
    private RelativeLayout floating_search_layout;
    private FrameLayout frame;

    private boolean mIsDarkSearchTheme = false;

    private String mLastQuery = "";

    public SlidingSearchResultsFragment() {
        // Required empty public constructor
    }

    public void setSearch(ImageButton search) {
        this.search = search;
    }

    public void setFrame(FrameLayout frame) {
        this.frame = frame;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_floating_search_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        token = Session.getToken(getActivity());
        userId = Session.getUserId(getActivity());
        mSearchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
//        mSearchResultsList = (RecyclerView) view.findViewById(R.id.search_results_list);
        //search = (ImageButton) view.findViewById(R.id.search);
        floating_search_layout = (RelativeLayout) view.findViewById(R.id.floating_search_layout);

        search.setOnClickListener((v) -> {
            frame.setVisibility(View.VISIBLE);
            floating_search_layout.setVisibility(View.VISIBLE);
            int width = LocalDisplay.getScreenWidth(getContext());
            int cx = width - LocalDisplay.dp2px(5, getContext());
            int cy = LocalDisplay.dp2px(33, getContext());
            Animator anim = ViewAnimationUtils.createCircularReveal(mSearchView, cx, cy, 0, cx);
            mSearchView.setVisibility(View.VISIBLE);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSearchView.setSearchFocused(true);
                }
            });
            anim.start();
        });

        setupFloatingSearch();
        setupResultsList();
        setupDrawer();
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<MySearchSuggestion> results) {


                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    if (mSearchView.getQuery().trim().isEmpty()) {
                                        mSearchView.clearSuggestions();
                                    } else {
                                        if (results != null) {
                                            mSearchView.swapSuggestions(results);
                                        }
                                    }

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                String [] rs = searchSuggestion.getBody().split("\\n+");
                if (rs[1].equalsIgnoreCase("search")) {
                    ((MainActivity) getActivity()).swipeTab(3);
                    SearchCardAdapter adapter = new SearchCardAdapter();
                    adapter.setQuery(mSearchView.getQuery());
                    adapter.setUsers(DataHelper.users);
                    adapter.setPlaces(DataHelper.places);
                    adapter.setPosts(DataHelper.posts);
                    adapter.setContext(getActivity());
                    adapter.setToken(token);
                    adapter.setUserId(userId);
                    searchFragment.getRcvSearch().setAdapter(adapter);
                    mSearchView.setSearchFocused(false);
                } else if (rs[1].equalsIgnoreCase("place")) {
                    Intent i = new Intent(getContext(), PlaceDetailActivity.class);
                    try {
                        i.putExtra("id", Integer.parseInt(rs[2]));
                    } catch (Exception e) {
                        mSearchView.setSearchFocused(false);
                        Toast.makeText(getContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show();
                    }
                    i.putExtra("token", Session.getToken(getActivity()));
                    mSearchView.setSearchFocused(false);
                    startActivity(i);
                } else if (rs[1].equalsIgnoreCase("post")) {
                    Intent i = new Intent(getContext(), PostDetailActivity.class);
                    try {
                        i.putExtra("id", Integer.parseInt(rs[2]));
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show();
                    }
                    i.putExtra("token", token);
                    mSearchView.setSearchFocused(false);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getContext(), ProfileMemberActivity.class);
                    try {
                        i.putExtra("id", Integer.parseInt(rs[rs.length - 1]));
                        i.putExtra("owner", (Integer.parseInt(rs[rs.length - 1])) == userId);
                    } catch (Exception e) {
                        mSearchView.setSearchFocused(false);
                        Toast.makeText(getContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show();
                    }
                    i.putExtra("token", token);
                    mSearchView.setSearchFocused(false);
                    startActivity(i);
                }
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                if (DataHelper.finishLoad) {
                    ((MainActivity) getActivity()).swipeTab(3);
                    SearchCardAdapter adapter = new SearchCardAdapter();
                    adapter.setQuery(mSearchView.getQuery());
                    adapter.setUsers(DataHelper.users);
                    adapter.setPlaces(DataHelper.places);
                    adapter.setPosts(DataHelper.posts);
                    adapter.setContext(getContext());
                    searchFragment.getRcvSearch().setAdapter(adapter);
                    mSearchView.setSearchFocused(false);
                }

                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                //mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));
                floating_search_layout.setBackgroundColor(getResources().getColor(R.color.colorAlpha50Black));

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle("Tìm kiếm");

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                int width = LocalDisplay.getScreenWidth(getContext());
                int cx = width - LocalDisplay.dp2px(5, getContext());
                int cy = LocalDisplay.dp2px(33, getContext());
                Animator anim = ViewAnimationUtils.createCircularReveal(mSearchView, cx, cy, cx, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSearchView.setVisibility(View.GONE);
                        floating_search_layout.setBackgroundColor(Color.argb(0, 127, 127, 127));
                        floating_search_layout.setVisibility(View.GONE);
                        frame.setVisibility(View.GONE);
                    }
                });
                anim.setStartDelay(250);
                anim.start();

                Log.d(TAG, "onFocusCleared()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                MySearchSuggestion suggestion = (MySearchSuggestion) item;

                String [] data = new String[]{};
                if (suggestion.isHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));
                } else {
                    //leftIcon.setAlpha(0.0f);
                    data = suggestion.getBody().split("\\n");

                    if (data[1].equalsIgnoreCase("search")) {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_search_black_24dp, null));
                    } else if (data[1].equalsIgnoreCase("post")) {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_search_post_24dp, null));
                    } else if (data[1].equalsIgnoreCase("place")) {
                        leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_place_search_24dp, null));
                    } else {
                        if (data.length == 2) {
                            leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.ic_account_circle_black_24dp, null));
                        } else {
                            GlideApp.with(getContext())
                                    .load(APIUtils.BASE_URL_API + data[1] + "&w=" + LocalDisplay.dp2px(60, getContext()))
                                    .fallback(R.drawable.ic_search_black_24dp)
                                    .placeholder(R.drawable.ic_search_black_24dp)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(leftIcon);
                        }
                    }
                }
//                if (data.length == 3) {
//                    textView.setText(data[0]);
//                } else {
//                    textView.setText(data[0]);
//                }
                textView.setText(data[0]);
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                //mSearchResultsList.setTranslationY(newHeight);
            }
        });

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {

                Log.d(TAG, "onClearSearchClicked()");
            }
        });
    }

    private void setupResultsList() {

//        mSearchResultsList.setAdapter(mSearchResultsAdapter);
//        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public boolean onActivityBackPress() {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        if (!mSearchView.setSearchFocused(false)) {
            return false;
        }
        return true;
    }

    private void setupDrawer() {
        attachSearchViewActivityDrawer(mSearchView);
    }

}
