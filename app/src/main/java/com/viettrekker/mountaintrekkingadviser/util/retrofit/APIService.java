package com.viettrekker.mountaintrekkingadviser.util.retrofit;

import com.viettrekker.mountaintrekkingadviser.Constant;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @POST(Constant.PATH_SIGNIN)
    @FormUrlEncoded
    Call<User> postLogin(@Field(Constant.PARAM_EMAIL) String email,
                         @Field(Constant.PARAM_PASSWORD) String password);

    @POST(Constant.PATH_SIGNUP)
    @FormUrlEncoded
    Call<MyMessage> postRegister(@Field(Constant.PARAM_EMAIL) String email,
                                 @Field(Constant.PARAM_PASSWORD) String password,
                                 @Field(Constant.PARAM_FIRSTNAME) String firstname,
                                 @Field(Constant.PARAM_LASTNAME) String lastname,
                                 @Field(Constant.PARAM_GENDER) int gender,
                                 @Field(Constant.PARAM_BIRTHDATE) String birthdate);
}