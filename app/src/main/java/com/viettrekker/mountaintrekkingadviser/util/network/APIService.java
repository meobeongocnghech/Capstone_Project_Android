package com.viettrekker.mountaintrekkingadviser.util.network;

import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Service interface defining abstract methods to call web service API
 *
 * @author LongNC
 * 25/07/2018
 */
public interface APIService {

    /**
     * User login request
     *
     * @param email user email
     * @param password user password
     * @return User model
     */
    @POST("auth/signin")
    @FormUrlEncoded
    Call<User> postLogin(@Field("email") String email,
                         @Field("password") String password);

    /**
     * User register request
     *
     * @param email user email
     * @param password user password
     * @param firstname user firstname
     * @param lastname user lastname
     * @param gender user gender (1 for male, 0 for female)
     * @param birthdate user birthdate (yyyy-MM-dd'T'hh:mm:ss.SSS'Z')
     * @return Log message
     */
    @POST("auth/signup")
    @FormUrlEncoded
    Call<MyMessage> postRegister(@Field("email") String email,
                                 @Field("password") String password,
                                 @Field("firstname") String firstname,
                                 @Field("lastname") String lastname,
                                 @Field("gender") int gender,
                                 @Field("birthdate") String birthdate);

    @GET("place/paging")
    Call<List<Place>> getPlaces(@Header("AUTH_TOKEN_ID") String token,
                                @Query("page") int page,
                                @Query("pageSize") int size,
                                @Query("orderBy") String order);

    @GET("noti?newerThanId=1&newest=true")
    Call<List<Notification>> getNoti(@Header("AUTH_TOKEN_ID") String token);

    @GET("noti")
    Call<List<Notification>> getOldNoti(@Header("AUTH_TOKEN_ID") String token,
                                     @Query("olderThanId") int id);

    @GET("post/paging")
    Call<List<Post>> getPostPage(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("page") int page,
                                 @Query("pageSize") int size,
                                 @Query("orderBy") String order);

    @GET("member/post")
    Call<List<Post>> getPostPageByUserId(@Header("AUTH_TOKEN_ID") String token,
                                         @Query("userId") int id,
                                         @Query("page") int page);

    @GET("post")
    Call<Post> getPostByPostId(@Header("AUTH_TOKEN_ID") String token,
                                         @Query("id") int id);

    @POST("post/like")
    Call<Post> likePost(@Header("AUTH_TOKEN_ID") String token,
                               @Field("id") int id);

    @POST("comment/like")
    Call<Post> likeComment(@Header("AUTH_TOKEN_ID") String token,
                           @Field("id") int id,
                           @Field("targetId") int targetId);
}