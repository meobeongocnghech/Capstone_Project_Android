package com.viettrekker.mountaintrekkingadviser.util.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create service interface to connect to server
 *
 * @author LongNC
 * 25/07/2018
 */
public class APIUtils {

    /* API URL
     * Change the domain or specific IP address (run on local server)
     * in order to call service properly
     */
    public static final String BASE_URL_API = "http://10.22.117.231:8088/api/";

    /**
     * Create service interface using Retrofit library
     *
     * @return APIService Interface
     * @see APIService
     * @see Retrofit <a href="http://square.github.io/retrofit/">http://square.github.io/retrofit/</a>
     */
    public static APIService getWebService() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();

        // JSON Parser
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_API)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofit.create(APIService.class);
    }
}
