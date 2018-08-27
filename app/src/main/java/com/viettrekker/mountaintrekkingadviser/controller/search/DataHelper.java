package com.viettrekker.mountaintrekkingadviser.controller.search;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.widget.Filter;

import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.Session;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static APIService mWebService = APIUtils.getWebService();
    public static List<User> users = new ArrayList<>();
    public static List<Place> places = new ArrayList<>();
    public static List<Post> posts = new ArrayList<>();
    private static Call<List<User>> callUsers;
    private static Call<List<Place>> callPlaces;
    private static Call<List<Post>> callPosts;
    private static List<MySearchSuggestion> suggestionList;
    public static boolean finishLoad;

    public interface OnFindSuggestionsListener {
        void onResults(List<MySearchSuggestion> results);
    }

//    public static List<MySearchSuggestion> getHistory(Context context, int count) {
//
//        List<MySearchSuggestion> suggestionList = new ArrayList<>();
//        MySearchSuggestion searchSuggestion;
//        for (int i = 0; i < suggestions.size(); i++) {
//            searchSuggestion = suggestions.get(i);
//            searchSuggestion.setIsHistory(true);
//            suggestionList.add(searchSuggestion);
//            if (suggestionList.size() == count) {
//                break;
//            }
//        }
//        return suggestionList;
//    }
//
//    public static void resetSuggestionsHistory() {
//        for (MySearchSuggestion searchSuggestion : suggestions) {
//            searchSuggestion.setIsHistory(false);
//        }
//    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                finishLoad = false;

                FilterResults results = new FilterResults();
                if (!(constraint == null || constraint.toString().trim().length() == 0)) {
//                    suggestionList = null;
                    if (callUsers != null) callUsers.cancel();
                    callUsers = mWebService.searchUserSuggestion(Session.getToken(context),
                            1, 4, constraint.toString().trim());
                    callUsers.enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            users = response.body();
                            if (users != null)
                            users.remove(0);
                            if (callPlaces != null) callPlaces.cancel();
                            callPlaces = mWebService.searchPlaceSuggestion(Session.getToken(context),
                                    1, 4, constraint.toString().trim());
                            callPlaces.enqueue(new Callback<List<Place>>() {
                                @Override
                                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                                    places = response.body();
                                    if (places != null)
                                    places.remove(0);

                                    if (callPosts != null) callPosts.cancel();
                                    callPosts = mWebService.searchPostSuggestion(Session.getToken(context),
                                            1, 4, "created_at", "DESC", constraint.toString().trim());
                                    callPosts.enqueue(new Callback<List<Post>>() {
                                        @Override
                                        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                                            posts = response.body();
                                            if (posts != null)
                                            posts.remove(0);

                                            finishLoad = true;

                                            suggestionList = new ArrayList<>();
                                            int i = 0;
                                            if (users != null)
                                            for (User user : users) {
                                                suggestionList.add(new MySearchSuggestion(user.getFirstName() + " " + user.getLastName(),user.getAvatar().getPath().isEmpty() ? "" : user.getAvatar().getPath().substring(4) , user.getId()));
                                                if (i == 2) break;
                                                i++;
                                            }

                                            i = 0;
                                            if (places != null)
                                            for (Place place : places) {
                                                if (i == 2) break;
                                                suggestionList.add(new MySearchSuggestion(place.getName(), "place", place.getId()));
                                                i++;
                                            }

                                            i = 0;
                                            if (posts != null)
                                            for (Post post : posts) {
                                                if (i == 2) break;
                                                suggestionList.add(new MySearchSuggestion(post.getName(), "post", post.getId()));
                                                i++;
                                            }

                                            suggestionList.add(new MySearchSuggestion(constraint.toString(), "search", 0));
                                            results.values = suggestionList;
                                            results.count = suggestionList.size();
                                            listener.onResults((List<MySearchSuggestion>) results.values);
                                        }

                                        @Override
                                        public void onFailure(Call<List<Post>> call, Throwable t) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<List<Place>> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {

                        }
                    });
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<MySearchSuggestion>) results.values);
                }
            }
        }.filter(query);

    }
}