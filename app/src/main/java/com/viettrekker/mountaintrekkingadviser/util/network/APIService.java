package com.viettrekker.mountaintrekkingadviser.util.network;

import com.google.gson.JsonObject;
import com.viettrekker.mountaintrekkingadviser.controller.MainActivity;
import com.viettrekker.mountaintrekkingadviser.model.Direction;
import com.viettrekker.mountaintrekkingadviser.model.ImageSize;
import com.viettrekker.mountaintrekkingadviser.model.Member;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.model.Notification;
import com.viettrekker.mountaintrekkingadviser.model.Place;
import com.viettrekker.mountaintrekkingadviser.model.Plan;
import com.viettrekker.mountaintrekkingadviser.model.Post;
import com.viettrekker.mountaintrekkingadviser.model.PostIdRemove;
import com.viettrekker.mountaintrekkingadviser.model.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @GET("noti?newerThanId=0&newest=true")
    Call<List<Notification>> getNoti(@Header("AUTH_TOKEN_ID") String token);

    @GET("noti")
    Call<List<Notification>> getOldNoti(@Header("AUTH_TOKEN_ID") String token,
                                     @Query("olderThanId") int id);

    @PUT("noti")
    @FormUrlEncoded
    Call<List<Notification>> setCheckAll(@Header("AUTH_TOKEN_ID") String token,
                                         @Field("check") boolean check);

    @GET("post/paging")
    Call<List<Post>> getPostPage(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("page") int page,
                                 @Query("pageSize") int size,
                                 @Query("orderBy") String orderBy,
                                 @Query("order") String order);

    @GET("post/paging")
    Call<List<Post>> getPostPageByPlaceId(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("page") int page,
                                 @Query("pageSize") int size,
                                 @Query("placeId") int id,
                                 @Query("order") String order);

    @GET("post/paging")
    Call<List<Post>> getPostPageByDirectionId(@Header("AUTH_TOKEN_ID") String token,
                                              @Query("page") int page,
                                              @Query("pageSize") int size,
                                              @Query("directionId") int id,
                                              @Query("order") String order);

    @GET("member/post")
    Call<List<Post>> getPostPageByUserId(@Header("AUTH_TOKEN_ID") String token,
                                         @Query("userId") int id,
                                         @Query("page") int page,
                                         @Query("order") String order);

    @GET("post")
    Call<Post> getPostByPostId(@Header("AUTH_TOKEN_ID") String token,
                                         @Query("id") int id);

    @POST("post/like")
    @FormUrlEncoded
    Call<Post> likePost(@Header("AUTH_TOKEN_ID") String token,
                               @Field("id") int id);
    @POST("post/unlike")
    @FormUrlEncoded
    Call<Post> unlikePost(@Header("AUTH_TOKEN_ID") String token,
                        @Field("id") int id);

    @POST("post/like")
    @FormUrlEncoded
    Call<Post> likeComment(@Header("AUTH_TOKEN_ID") String token,
                           @Field("id") int id,
                           @Field("commentId") int commentId);
    @POST("post/unlike")
    @FormUrlEncoded
    Call<Post> unlikeComment(@Header("AUTH_TOKEN_ID") String token,
                           @Field("id") int id,
                           @Field("commentId") int commentId);

    @POST("post/comment")
    @FormUrlEncoded
    Call<Post> commentPost(@Header("AUTH_TOKEN_ID") String token,
                             @Field("id") int id,
                             @Field("content") String content);

    @POST("post/comment")
    @FormUrlEncoded
    Call<Post> commentOnComment(@Header("AUTH_TOKEN_ID") String token,
                                @Field("id") int id,
                                @Field("commentId") int commentId,
                                @Field("content") String content);

    @POST("post/report")
    @FormUrlEncoded
    Call<Post> reportComent(@Header("AUTH_TOKEN_ID") String token,
                                @Field("id") int id,
                                @Field("commentId") int commentId,
                                @Field("reason") String reason);

    @POST("post/report")
    @FormUrlEncoded
    Call<Post> reportPost(@Header("AUTH_TOKEN_ID") String token,
                            @Field("id") int id,
                            @Field("reason") String reason);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "post/comment", hasBody = true)
    Call<Post> removeComment(@Header("AUTH_TOKEN_ID") String token,
                             @Field("id") int id,
                             @Field("commentId") int commentId);

    @HTTP(method = "DELETE", path = "post", hasBody = true)
    Call<Post> removePost(@Header("AUTH_TOKEN_ID") String token,
                          @Body PostIdRemove id);

    @GET("member")
    Call<User> getUserById(@Header("AUTH_TOKEN_ID") String token,
                           @Query("id") int id);

    @GET("place/paging")
    Call<ArrayList<Place>> searchPlace(@Header("AUTH_TOKEN_ID") String token,
                                       @Query("page") int page,
                                       @Query("pageSize") int pageSize,
                                       @Query("orderBy") String orderBy,
                                       @Query("search") String search);

    @POST("post")
    Call<Post> addPost(@Header("AUTH_TOKEN_ID") String token,
                       @Body Post post);

    @POST("post")
    Call<Post> addPostWithImages(@Header("AUTH_TOKEN_ID") String token,
                       @Body RequestBody in);

    @PUT("post")
    @FormUrlEncoded
    Call<Post> updatePost(@Header("AUTH_TOKEN_ID") String token,
                       @Field("id") int id,
                       @Field("name") String name,
                       @Field("content") String content);
    @POST("auth/profile")
    Call<User> updateUserProfileWithAvatar(@Header("AUTH_TOKEN_ID") String token,
                                 @Body RequestBody in);

    @POST("auth/profile")
    Call<User> updateUserProfile(@Header("AUTH_TOKEN_ID") String token,
                                 @Body RequestBody in);

    @POST("auth/profile")
    @Multipart
    Call<User> updateUserProfileWithCover(@Header("AUTH_TOKEN_ID") String token,
                                 @Part("id") RequestBody id,
                                 @Part("firstname") RequestBody firstname,
                                 @Part("lastname") RequestBody lastname,
                                 @Part("phone") RequestBody phone,
                                 @Part("birthdate") RequestBody date,
                                 @Part("gender") RequestBody gender,
                                 @Part("sosContact") RequestBody sos,
                                 @Part("facebookAuth") RequestBody fbAuth,
                                 @Part("medias") RequestBody medias);

    @GET("image/size")
    Call<ImageSize> getImageSize(@Query("link") String link);

    @PUT("noti")
    @FormUrlEncoded
    Call<Notification> markReadNotification(@Header("AUTH_TOKEN_ID") String token,
                                            @Field("id") int id);

    @GET("plan/paging")
    Call<List<Plan>> getListPlan(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("page") int page,
                                 @Query("pageSize") int pageSize,
                                 @Query("orderBy") String orderBy);

    @GET("plan")
    Call<Plan> getPlanById(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("id") int id);

    @GET("place")
    Call<Place> getPlaceById(@Header("AUTH_TOKEN_ID") String token,
                           @Query("id") int id);

    @GET("member/paging")
    Call<List<User>> searchMember(@Header("AUTH_TOKEN_ID") String token,
                              @Query("search") String search);
    @POST("plan")
    Call<Plan> createPlan(@Header("AUTH_TOKEN_ID") String token,
                                  @Body Plan plan);

    @PUT("plan")
    Call<Plan> updatePlan(@Header("AUTH_TOKEN_ID") String token,
                          @Body Plan plan);

    @GET("member/paging")
    Call<List<User>> searchUserSuggestion(@Header("AUTH_TOKEN_ID") String token,
                                          @Query("page") int page,
                                          @Query("pageSize") int size,
                                          @Query("search") String search);

    @GET("place/paging")
    Call<List<Place>> searchPlaceSuggestion(@Header("AUTH_TOKEN_ID") String token,
                                            @Query("page") int page,
                                            @Query("pageSize") int size,
                                            @Query("search") String search);

    @GET("post/paging")
    Call<List<Post>> searchPostSuggestion(@Header("AUTH_TOKEN_ID") String token,
                                          @Query("page") int page,
                                          @Query("pageSize") int size,
                                          @Query("orderBy") String orderBy,
                                          @Query("order") String order,
                                          @Query("search") String search);

    @GET("post/paging")
    Call<List<Post>> getPostByType(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("page") int page,
                                 @Query("pageSize") int size,
                                 @Query("orderBy") String orderBy,
                                 @Query("order") String order);

    @PUT("post/comment")
    @FormUrlEncoded
    Call<Post> updateComment(@Header("AUTH_TOKEN_ID") String token,
                           @Field("id") int id,
                             @Field("commentId") int commentId,
                           @Field("content") String content);



    @POST("auth/token")
    Call<User> validToken(@Header("AUTH_TOKEN_ID") String token);

    @FormUrlEncoded
    @POST("plan/invite")
    Call<Plan> invitePlan(@Header("AUTH_TOKEN_ID") String token,
                          @Field("id") int id,
                          @Field("userId") int userId,
                          @Field("carry") int carry,
                          @Field("vehicule") String vehicule,
                          @Field("sosDelay") int sosDelay);

    @FormUrlEncoded
    @POST("plan/invite/approve")
    Call<Plan> approveInvitation(@Header("AUTH_TOKEN_ID") String token,
                          @Field("id") int id);

    @FormUrlEncoded
    @POST("plan/invite/reject")
    Call<Plan> rejectInvitation(@Header("AUTH_TOKEN_ID") String token,
                                 @Field("id") int id);

    @FormUrlEncoded
    @POST("plan/request")
    Call<Plan> requestJoin(@Header("AUTH_TOKEN_ID") String token,
                                @Field("id") int id);

    @GET("plan/paging")
    Call<List<Plan>> getListPlanIsPublic(@Header("AUTH_TOKEN_ID") String token,
                                 @Query("page") int page,
                                 @Query("pageSize") int pageSize,
                                 @Query("orderBy") String orderBy,
                                 @Query("isPublic") boolean isPublic);

    @FormUrlEncoded
    @POST("plan/member/quit")
    Call<Plan> quitPlan(@Header("AUTH_TOKEN_ID") String token,
                           @Field("id") int id);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "plan", hasBody = true)
    Call<Plan> removePlan(@Header("AUTH_TOKEN_ID") String token,
                        @Field("id") int id);

    @FormUrlEncoded
    @POST("plan/kick")
    Call<Plan> kickMember(@Header("AUTH_TOKEN_ID") String token,
                        @Field("id") int id,
                          @Field("userId") int userId);

    @FormUrlEncoded
    @PUT("plan/member/update")
    Call<Plan> updateTraffic(@Header("AUTH_TOKEN_ID") String token,
                          @Field("id") int id,
                          @Field("sosDelay") int sosDelay,
                             @Field("vehicule") String vehicule,
                             @Field("carry") int carry);



    @FormUrlEncoded
    @POST("plan/request/approve")
    Call<Plan> approveRequest(@Header("AUTH_TOKEN_ID") String token,
                          @Field("id") int id,
                          @Field("userId") int userId);


    @PUT("auth/password")
    @FormUrlEncoded
    Call<User> changePassword(@Header("AUTH_TOKEN_ID") String token,
                              @Field("newPassword") String newPwd,
                              @Field("password") String pwd);

    @POST("auth/password")
    @FormUrlEncoded
    Call<MyMessage> sendKeyToEmail(@Field("email") String email);

    @PATCH("auth/password")
    @FormUrlEncoded
    Call<User> sendChangePassword(@Field("key") String key,
                                @Field("email") String email);
}
